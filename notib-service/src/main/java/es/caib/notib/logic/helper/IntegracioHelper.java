package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioParamEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioParamRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_REGISTRE = "REGISTRE";
	public static final String INTCODI_NOTIFICA = "NOTIFICA";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_CLIENT = "CALLBACK";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_GESCONADM = "GESCONADM";
	public static final String INTCODI_PROCEDIMENT = "PROCEDIMENTS";
	public static final String INTCODI_FIRMASERV = "FIRMASERV";
	public static final String INTCODI_VALIDASIG = "VALIDASIG";
	public static final String CARPETA = "CARPETA";

	public List<IntegracioDto> findAll() {

		List<IntegracioDto> integracions = new ArrayList<>();
		integracions.add(novaIntegracio(INTCODI_USUARIS));
		integracions.add(novaIntegracio(INTCODI_REGISTRE));
		integracions.add(novaIntegracio(INTCODI_NOTIFICA));
		integracions.add(novaIntegracio(INTCODI_ARXIU));
		integracions.add(novaIntegracio(INTCODI_CLIENT));
		integracions.add(novaIntegracio(INTCODI_GESDOC));
		integracions.add(novaIntegracio(INTCODI_UNITATS));
		integracions.add(novaIntegracio(INTCODI_GESCONADM));
		integracions.add(novaIntegracio(INTCODI_PROCEDIMENT));
		integracions.add(novaIntegracio(INTCODI_FIRMASERV));
		integracions.add(novaIntegracio(INTCODI_VALIDASIG));
		return integracions;
	}

	public Map<String, Integer> countErrorsGroupByCodi() {

		Map<String ,Integer> errorsGroupByCodi = new HashMap<>();
		errorsGroupByCodi.put(INTCODI_USUARIS,countErrors(INTCODI_USUARIS));
		errorsGroupByCodi.put(INTCODI_REGISTRE,countErrors(INTCODI_REGISTRE));
		errorsGroupByCodi.put(INTCODI_NOTIFICA,countErrors(INTCODI_NOTIFICA));
		errorsGroupByCodi.put(INTCODI_ARXIU,countErrors(INTCODI_ARXIU));
		errorsGroupByCodi.put(INTCODI_CLIENT,countErrors(INTCODI_CLIENT));
		errorsGroupByCodi.put(INTCODI_GESDOC,countErrors(INTCODI_GESDOC));
		errorsGroupByCodi.put(INTCODI_UNITATS,countErrors(INTCODI_UNITATS));
		errorsGroupByCodi.put(INTCODI_GESCONADM,countErrors(INTCODI_GESCONADM));
		errorsGroupByCodi.put(INTCODI_PROCEDIMENT,countErrors(INTCODI_PROCEDIMENT));
		errorsGroupByCodi.put(INTCODI_FIRMASERV,countErrors(INTCODI_FIRMASERV));
		errorsGroupByCodi.put(INTCODI_VALIDASIG,countErrors(INTCODI_VALIDASIG));
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

	private Integer countErrors(String codi) {
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
			var usuari = usuariRepository.findById(auth.getName()).orElse(null);
			if (usuari == null) {
				log.warn("Error IntegracioHelper.getUsuariNomCodi -> Usuari no trobat a la bdd");
				return usuariNomCodi;
			}
			return usuari.getNom() + " (" + usuari.getCodi() + ")";
		} catch (Exception ex) {
			log.error("[Error Integració] Error al buscar l'usuari " + usuariNomCodi);
			return usuariNomCodi;
		}
	}

	private IntegracioDto novaIntegracio(String codi) {

		var integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		} else if (INTCODI_REGISTRE.equals(codi)) {
			integracio.setNom("Registre");
		} else if (INTCODI_NOTIFICA.equals(codi)) {
			integracio.setNom("Notifica");
		} else if (INTCODI_ARXIU.equals(codi)) {
			integracio.setNom("Arxiu");
		} else if (INTCODI_CLIENT.equals(codi)) {
			integracio.setNom("Callback de client");
		} else if (INTCODI_GESDOC.equals(codi)) {
			integracio.setNom("Gestor documental");
		} else if (INTCODI_UNITATS.equals(codi)) {
			integracio.setNom("Unitats organitzatives");
		} else if (INTCODI_GESCONADM.equals(codi)) {
			integracio.setNom("Rolsac");
		} else if (INTCODI_PROCEDIMENT.equals(codi)) {
				integracio.setNom("Procediments");
		} else if (INTCODI_FIRMASERV.equals(codi)) {
			integracio.setNom("Firma servidor");
		} else if (INTCODI_VALIDASIG.equals(codi)) {
			integracio.setNom("Validacio firma");
		}
		return integracio;
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