package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

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
	
	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<>();
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<>();

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

	public List<IntegracioAccioDto> findAccions(String integracioCodi, IntegracioFiltreDto filtre) {

		return getLlistaAccions(integracioCodi, filtre);
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

		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(info.getCodi()));
		accio.setData(new Date());
		accio.setDescripcio(info.getDescripcio());
		accio.setAplicacio(info.getAplicacio());
		accio.setParametres(info.getParams());
		accio.setTipus(info.getTipus());
		accio.setCodiEntitat(info.getCodiEntitat());
		accio.setTempsResposta(info.getTempsResposta());
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		addAccio(info.getCodi(), accio, obtenirUsuari);
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

		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(info.getCodi()));
		accio.setData(new Date());
		accio.setDescripcio(info.getDescripcio());
		accio.setAplicacio(info.getAplicacio());
		accio.setParametres(info.getParams());
		accio.setTipus(info.getTipus());
		accio.setCodiEntitat(info.getCodiEntitat());
		accio.setTempsResposta(info.getTempsResposta());
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		if (throwable != null) {
			accio.setExcepcioMessage(ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(ExceptionUtils.getStackTrace(throwable));
		}
		addAccio(info.getCodi(),accio, obtenirUsuari);
		logger.debug("Error d'integracio " + info.getDescripcio() + ": " + errorDescripcio + "("
				+ "integracioCodi=" + info.getCodi() + ", "
				+ "parametres=" + info.getParams() + ", "
				+ "tipus=" + info.getTipus() + ", "
				+ "tempsResposta=" + info.getTempsResposta() + ")",
				throwable);
	}

	private Integer countErrors(String codi) {
		Integer accionsAmbError = 0;
		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(codi);
		if (accions != null) {
			for (IntegracioAccioDto integracioAccioDto : accions) {
				if (integracioAccioDto.getEstat().equals(IntegracioAccioEstatEnumDto.ERROR))
					accionsAmbError++;
			}
		}
		return accionsAmbError;
	}
	
	private synchronized LinkedList<IntegracioAccioDto> getLlistaAccions(String integracioCodi, IntegracioFiltreDto filtre) {

		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
			accions = new LinkedList<>();
			accionsIntegracio.put(integracioCodi, accions);
			return accions;
		}
		LinkedList<IntegracioAccioDto> accionsBones = new LinkedList<>();
		for (IntegracioAccioDto accio: accions) {
			if (filtre != null && !filtre.filtresOK(accio, integracioCodi)) {
				continue;
			}
			accionsBones.add(accio);
		}
		return accionsBones;
	}
	private int getMaxAccions(String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(DEFAULT_MAX_ACCIONS);
			maxAccionsIntegracio.put(integracioCodi, max);
		}
		return max.intValue();
	}

	private void addAccio(String integracioCodi, IntegracioAccioDto accio, boolean obtenirUsuari) {

		afegirParametreUsuari(accio, obtenirUsuari);
		var accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
			accions = new LinkedList<>();
			accions.add(accio);
			return;
		}
		var max = getMaxAccions(integracioCodi);
		while (accions.size() >= max) {
			accions.remove(accions.size() - 1);
		}
		try {
			accions.addFirst(accio);
		} catch (Exception ex) {
			log.error("Error afegint la acció: " + ex);
			try {
				accions.add(accio);
			} catch (Exception e) {
				log.error("Error afegint la acció: " + e);
			}
		}
	}
	
	private void afegirParametreUsuari(IntegracioAccioDto accio, boolean obtenirUsuari) {

		if (accio.getParametres() == null) {
			accio.setParametres(new ArrayList<AccioParam>());
		}
		accio.getParametres().add(new AccioParam("Usuari", getUsuariNomCodi(obtenirUsuari)));
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
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);
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

	private static final Logger logger = LoggerFactory.getLogger(IntegracioHelper.class);
	
}
