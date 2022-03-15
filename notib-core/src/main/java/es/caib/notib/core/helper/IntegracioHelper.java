package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.entity.UsuariEntity;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class IntegracioHelper {

	@Resource
	private UsuariHelper usuariHelper;
	
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
	
	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<String, LinkedList<IntegracioAccioDto>>();
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();

	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
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

	public List<IntegracioAccioDto> findAccions(String integracioCodi, String filtre) {
		return getLlistaAccions(integracioCodi, filtre);
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(String integracioCodi) {
		return getLlistaAccions(integracioCodi);
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

	private synchronized LinkedList<IntegracioAccioDto> getLlistaAccions(String integracioCodi, String ... filtres) {
		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
			accions = new LinkedList<>();
			accionsIntegracio.put(integracioCodi, accions);
			return accions;
		}
		int index = 0;
		List<IntegracioAccioDto>  indexBorrar = new ArrayList<>();
		LinkedList<IntegracioAccioDto> accionsBones = new LinkedList<>();
		for (IntegracioAccioDto accio: accions) {
			boolean esborrada = false;
			if ("CALLBACK".equals(integracioCodi) && filtres != null) {
				for (int foo = 0; foo < filtres.length; foo++) {
					if (accio.getAplicacio() != null && !accio.getAplicacio().contains(filtres[foo])) {
						esborrada = true;
						indexBorrar.add(accio);
						continue;
					}
					accionsBones.add(accio);
				}
			}
			if (!esborrada) {
				accio.setIndex(new Long(index++));
			}
		}
		return accionsBones.size() > 0 || filtres.length > 0 ? accionsBones : accions;
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
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
		int max = getMaxAccions(integracioCodi);
		while (accions.size() >= max) {
			accions.remove(accions.size() - 1);
		}
		accions.add(0, accio);
	}
	
	private void afegirParametreUsuari(IntegracioAccioDto accio, boolean obtenirUsuari) {

		String usuariNomCodi = "";
		if (obtenirUsuari) {
			UsuariEntity usuari = null;
			try {
				usuari = usuariHelper.getUsuariAutenticat();
			} catch (Exception e) {}
			if (usuari != null) {
				usuariNomCodi = usuari.getNom() + " (" + usuari.getCodi() + ")";
			}
		}
		if (usuariNomCodi.isEmpty()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				usuariNomCodi = auth.getName();
			}
		}
		if(accio.getParametres() == null) {
			accio.setParametres(new ArrayList<AccioParam>());
		}
		accio.getParametres().add(new AccioParam("Usuari", usuariNomCodi));
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
	
	private static final Logger logger = LoggerFactory.getLogger(IntegracioHelper.class);
	
}
