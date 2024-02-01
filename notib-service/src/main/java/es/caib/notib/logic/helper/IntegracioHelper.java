package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioParamEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioParamRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class IntegracioHelper {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private MonitorIntegracioRepository monitorRepository;
	@Autowired
	private MonitorIntegracioParamRepository monitorParamRepository;
	@Autowired
	private CacheHelper cacheHelper;


	public Map<IntegracioCodiEnum, Integer> countErrorsGroupByCodi() {

		Map<IntegracioCodiEnum ,Integer> errorsGroupByCodi = new HashMap<>();
		IntegracioCodiEnum.stream().forEach(codi -> errorsGroupByCodi.put(codi, countErrors(codi)));
		return errorsGroupByCodi;
	}

	public void addAccioOk(IntegracioInfo info) {
		addAccioOk(info, true);
	}

//	@Transactional
	public void addAccioOk(IntegracioInfo info, boolean obtenirUsuari) {

		var accio = MonitorIntegracioEntity.builder().codi(info.getCodi()).data(new Date()).descripcio(info.getDescripcio()).tipus(info.getTipus())
				.codiEntitat(info.getCodiEntitat()).tempsResposta(info.getTempsResposta()).estat(IntegracioAccioEstatEnumDto.OK).aplicacio(info.getAplicacio()).build();
		assignarAccioAParams(info, accio);
		addAccio(accio, obtenirUsuari);
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio) {

		addAccioError(info, errorDescripcio, null,true);
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio, boolean obtenirUsuari) {

		addAccioError(info, errorDescripcio, null,true);
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio, Throwable throwable) {

		addAccioError(info, errorDescripcio, throwable,true);
	}

//	@Transactional
	public void addAccioError(IntegracioInfo info, String errorDescripcio, Throwable throwable, boolean obtenirUsuari) {

		var accio = MonitorIntegracioEntity.builder().codi(info.getCodi()).data(new Date()).descripcio(info.getDescripcio()).tipus(info.getTipus())
				.codiEntitat(info.getCodiEntitat()).tempsResposta(info.getTempsResposta()).estat(IntegracioAccioEstatEnumDto.ERROR).errorDescripcio(errorDescripcio)
				.aplicacio(info.getAplicacio()).build();

		assignarAccioAParams(info, accio);
		if (throwable != null) {
			accio.setExcepcioMessage(ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(ExceptionUtils.getStackTrace(throwable));
		}
		addAccio(accio, obtenirUsuari);
		log.debug("Error d'integracio " + info.getDescripcio() + ": " + errorDescripcio + "(integracioCodi=" + info.getCodi() + ", "
				+ "parametres=" + info.getParams() + ", tipus=" + info.getTipus() + ", tempsResposta=" + info.getTempsResposta() + ")", throwable);
	}


	private void addAccio(MonitorIntegracioEntity accio, boolean obtenirUsuari) {

		afegirParametreUsuari(accio, obtenirUsuari);
		var st = accio.getExcepcioStacktrace();
		accio.setExcepcioStacktrace(st != null && st.getBytes().length > 2000 ? st.substring(0, 1997) + "..." : st);
		try {
			monitorRepository.save(accio);
		} catch (Exception ex) {
			log.error("Error al desar la acció del monitor.");
			log.error("Acció: {}", accio);
			log.error("Excepció: ", ex);
		}
	}

	private Integer countErrors(IntegracioCodiEnum codi) {
		return monitorRepository.countByCodiAndEstat(codi, IntegracioAccioEstatEnumDto.ERROR);
	}

	private void assignarAccioAParams(IntegracioInfo info, MonitorIntegracioEntity accio) {

		var params = info.getParams().stream()
				.map(p -> MonitorIntegracioParamEntity.builder()
						.monitorIntegracio(accio)
						.codi(p.getCodi())
						.valor(p.getValor()).build())
				.collect(Collectors.toList());
		accio.setParametres(params);
	}

	private void afegirParametreUsuari(MonitorIntegracioEntity accio, boolean obtenirUsuari) {

		if (accio.getParametres() == null) {
			accio.setParametres(new ArrayList<>());
		}
		accio.getParametres().add(MonitorIntegracioParamEntity.builder().monitorIntegracio(accio).codi("Usuari").valor(getUsuariNomCodi(obtenirUsuari)).build());
	}

	private String getUsuariNomCodi(boolean obtenirUsuari) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || Strings.isNullOrEmpty(auth.getName())) {
			return "";
		}
		var usuariNomCodi = auth.getName();
		if (!obtenirUsuari) {
			return usuariNomCodi;
		}
		try {
//			var usuari = usuariRepository.findById(auth.getName()).orElse(null);
			var usuari = cacheHelper.findUsuariByCodi(auth.getName());
			if (usuari == null) {
				log.warn("Error IntegracioHelper.getUsuariNomCodi -> Usuari " + auth.getName() + "no trobat a la bbdd");
				return usuariNomCodi;
			}
			return usuari.getNom() + " (" + usuari.getCodi() + ")";
		} catch (Exception ex) {
			log.error("[Error Integració] Error al buscar l'usuari " + usuariNomCodi);
			return usuariNomCodi;
		}
	}

	public void addAplicacioAccioParam(IntegracioInfo info, Long entitatId) {

		var usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
		info.setAplicacio(usuariCodi);
		if (entitatId == null) {
			var msg = "No existeix una aplicació amb el codi '" + usuariCodi;
			info.getParams().add(new AccioParam("Codi aplicació", msg));
			return;
		}
		var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuariCodi, entitatId);
		info.getParams().add(new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""));
	}

	public void eliminarAntics(Date llindar) {

		try {

			List<Long> ids;
			while (monitorRepository.existeixenAntics(llindar) == 1) {
				ids = monitorRepository.getNotificacionsAntigues(llindar);
				eliminarAntics(ids);
			}
		} catch (Exception ex) {
			log.error("Error esborrant les entrades del monitor d'integracions antigues.", ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void eliminarAntics(List<Long> ids) {

		monitorParamRepository.eliminarAntics(ids);
		monitorRepository.eliminarAntics(ids);
		monitorRepository.flush();
	}
}
