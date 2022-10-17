package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.IntegracioFiltreDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.core.entity.monitor.MonitorIntegracioParamEntity;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.core.repository.monitor.MonitorIntegracioRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class IntegracioHelper {

//	@Resource
//	private UsuariHelper usuariHelper;

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private MonitorIntegracioRepository monitorRepository;
	@Autowired
	private ConversioTipusHelper conversio;
	
	public static final int DEFAULT_MAX_ACCIONS = 250;

	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_REGISTRE = "REGISTRE";
	public static final String INTCODI_NOTIFICA = "NOTIFICA";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_CLIENT = "CALLBACK";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_GESCONADM = "GESCONADM";
	public static final String INTCODI_PROCEDIMENT = "PROCEDIMENTS";
	public static final String INTCODI_CONVERT = "CONVERT";
	public static final String INTCODI_FIRMASERV = "FIRMASERV";

	private static Map<String, Integer> maxAccionsIntegracio = new HashMap<>();
//	private static Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<>();

//	static {
//		LinkedList<IntegracioAccioDto> listAccionsUsuaris = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_USUARIS, listAccionsUsuaris);
//		LinkedList<IntegracioAccioDto> listAccionsRegistre = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_REGISTRE, listAccionsRegistre);
//		LinkedList<IntegracioAccioDto> listAccionsNotifica = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_NOTIFICA, listAccionsNotifica);
//		LinkedList<IntegracioAccioDto> listAccionsArxiu = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_ARXIU, listAccionsArxiu);
//		LinkedList<IntegracioAccioDto> listAccionsClient = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_CLIENT, listAccionsClient);
//		LinkedList<IntegracioAccioDto> listAccionsGestDoc = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_GESDOC, listAccionsGestDoc);
//		LinkedList<IntegracioAccioDto> listAccionsUnitats = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_UNITATS, listAccionsUnitats);
//		LinkedList<IntegracioAccioDto> listAccionsRolsac = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_GESCONADM, listAccionsRolsac);
//		LinkedList<IntegracioAccioDto> listAccionsProcediments = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_PROCEDIMENT, listAccionsProcediments);
//		LinkedList<IntegracioAccioDto> listAccionsConvert = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_CONVERT, listAccionsConvert);
//		LinkedList<IntegracioAccioDto> listAccionsFirma = new LinkedList<>();
//		accionsIntegracio.put(INTCODI_FIRMASERV, listAccionsFirma);
//	}

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
		return integracions;
	}

	public Map<String, Integer> countErrorsGroupByCodi() {
		Map<String ,Integer> errorsGroupByCodi = new HashMap<String, Integer>();
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
		return errorsGroupByCodi;
	}
	
	public void addAccioOk(IntegracioInfo info) {
		addAccioOk(info, true);
	}

	public void addAccioOk(IntegracioInfo info, boolean obtenirUsuari) {

		MonitorIntegracioEntity accio = MonitorIntegracioEntity.builder().codi(info.getCodi()).data(new Date()).descripcio(info.getDescripcio())
			.tipus(info.getTipus()).codiEntitat(info.getCodiEntitat()).tempsResposta(info.getTempsResposta()).estat(IntegracioAccioEstatEnumDto.OK)
//			.parametres(conversio.convertirList(info.getParams(), MonitorIntegracioParamEntity.class))
				.build();
		addAccio(accio, obtenirUsuari);
//		accio.setIntegracio(novaIntegracio(info.getCodi()));
//		accio.setAplicacio(info.getAplicacio());
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio) {

		addAccioError(info, errorDescripcio, null,true);
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio, boolean obtenirUsuari) {

		addAccioError(info, errorDescripcio, null, obtenirUsuari);
	}
	public void addAccioError(IntegracioInfo info, String errorDescripcio, Throwable throwable) {

		addAccioError(info, errorDescripcio, throwable,true);
	}

	public void addAccioError(IntegracioInfo info, String errorDescripcio, Throwable throwable, boolean obtenirUsuari) {

		MonitorIntegracioEntity accio = MonitorIntegracioEntity.builder().codi(info.getCodi()).data(new Date()).descripcio(info.getDescripcio()).tipus(info.getTipus())
				.codiEntitat(info.getCodiEntitat()).tempsResposta(info.getTempsResposta()).estat(IntegracioAccioEstatEnumDto.ERROR).errorDescripcio(errorDescripcio)
				.parametres(conversio.convertirList(info.getParams(), MonitorIntegracioParamEntity.class)).build();
//		accio.setIntegracio(novaIntegracio(info.getCodi()));
//		accio.setAplicacio(info.getAplicacio());
		if (throwable != null) {
			accio.setExcepcioMessage(ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(ExceptionUtils.getStackTrace(throwable));
		}
		addAccio(accio, obtenirUsuari);
		log.debug("Error d'integracio " + info.getDescripcio() + ": " + errorDescripcio + "(integracioCodi=" + info.getCodi() + ", "
				+ "parametres=" + info.getParams() + ", tipus=" + info.getTipus() + ", tempsResposta=" + info.getTempsResposta() + ")", throwable);
	}

	private Integer countErrors(String codi) {
		return monitorRepository.countByCodiAndEstat(codi, IntegracioAccioEstatEnumDto.ERROR);
	}

	@Transactional
	public List<IntegracioAccioDto> findAccions(String integracioCodi, IntegracioFiltreDto filtre) {

		return conversio.convertirList(monitorRepository.findAllByCodi(integracioCodi), IntegracioAccioDto.class);
	}

	private void addAccio(MonitorIntegracioEntity accio, boolean obtenirUsuari) {

		afegirParametreUsuari(accio, obtenirUsuari);
		monitorRepository.saveAndFlush(accio);
	}
	
	private void afegirParametreUsuari(MonitorIntegracioEntity accio, boolean obtenirUsuari) {

		if (accio.getParametres() == null) {
			accio.setParametres(new ArrayList<MonitorIntegracioParamEntity>());
		}
		accio.getParametres().add(MonitorIntegracioParamEntity.builder().codi("Usuari").valor(getUsuariNomCodi(obtenirUsuari)).build());
	}

	private String getUsuariNomCodi(boolean obtenirUsuari) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return "";
		}
		String usuariNomCodi = auth.getName();
		if (!obtenirUsuari) {
			return usuariNomCodi;
		}
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		if (usuari == null) {
			log.warn("Error IntegracioHelper.getUsuariNomCodi -> Usuari no trobat a la bdd");
			return usuariNomCodi;
		}
		return usuari.getNom() + " (" + usuari.getCodi() + ")";
	}

	private IntegracioDto novaIntegracio(String codi) {

		IntegracioDto integracio = new IntegracioDto();
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
		}
		return integracio;
	}

	public void addAplicacioAccioParam(IntegracioInfo info, Long entitatId) {

		String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
		info.setAplicacio(usuariCodi);
		if (entitatId == null) {
			String msg = "No existeix una aplicació amb el codi '" + usuariCodi;
			info.getParams().add(new AccioParam("Codi aplicació", msg));
			return;
		}
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuariCodi, entitatId);
		info.getParams().add(new AccioParam("Codi aplicació", aplicacio != null ? aplicacio.getUsuariCodi() : ""));
	}
}
