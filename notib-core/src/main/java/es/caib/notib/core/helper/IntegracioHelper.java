/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioDto;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class IntegracioHelper {

	public static final int DEFAULT_MAX_ACCIONS = 20;

	public static final String INTCODI_EMAIL = "EMAIL";
	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_SEU = "SEU";
	public static final String INTCODI_PFIRMA = "PFIRMA";
	public static final String INTCODI_CUSTODIA = "CUSTODIA";
	public static final String INTCODI_REGISTRE = "REGISTRE";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_CONVERT = "CONVERT";
	public static final String INTCODI_CALLBACK = "CALLBACK";
	public static final String INTCODI_DADESEXT = "DADESEXT";

	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<String, LinkedList<IntegracioAccioDto>>();
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();



	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_EMAIL));
		integracions.add(
				novaIntegracio(
						INTCODI_REGISTRE));
		integracions.add(
				novaIntegracio(
						INTCODI_UNITATS));
		integracions.add(
				novaIntegracio(
						INTCODI_PFIRMA));
		integracions.add(
				novaIntegracio(
						INTCODI_CALLBACK));
		integracions.add(
				novaIntegracio(
						INTCODI_CUSTODIA));
		integracions.add(
				novaIntegracio(
						INTCODI_GESDOC));
		integracions.add(
				novaIntegracio(
						INTCODI_SEU));
		integracions.add(
				novaIntegracio(
						INTCODI_USUARIS));
		integracions.add(
				novaIntegracio(
						INTCODI_CONVERT));
		integracions.add(
				novaIntegracio(
						INTCODI_DADESEXT));
		return integracions;
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(
			String integracioCodi) {
		return getLlistaAccions(integracioCodi);
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		addAccio(
				integracioCodi,
				accio);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		addAccio(
				integracioCodi,
				accio);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		accio.setExcepcioMessage(
				ExceptionUtils.getMessage(throwable));
		accio.setExcepcioStacktrace(
				ExceptionUtils.getStackTrace(throwable));
		addAccio(
				integracioCodi,
				accio);
	}



	private synchronized LinkedList<IntegracioAccioDto> getLlistaAccions(
			String integracioCodi) {
		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
			accions = new LinkedList<IntegracioAccioDto>();
			accionsIntegracio.put(
					integracioCodi,
					accions);
		} else {
			int index = 0;
			for (IntegracioAccioDto accio: accions) {
				accio.setIndex(new Long(index++));
			}
		}
		return accions;
	}
	private int getMaxAccions(
			String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(DEFAULT_MAX_ACCIONS);
			maxAccionsIntegracio.put(
					integracioCodi,
					max);
		}
		return max.intValue();
	}

	private void addAccio(
			String integracioCodi,
			IntegracioAccioDto accio) {
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
		int max = getMaxAccions(integracioCodi);
		while (accions.size() >= max) {
			accions.remove(accions.size() - 1);
		}
		accions.add(
				0,
				accio);
	}

	private IntegracioDto novaIntegracio(
			String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_REGISTRE.equals(codi)) {
			integracio.setNom("Registre");
		} else if (INTCODI_PFIRMA.equals(codi)) {
			integracio.setNom("Portafirmes");
		} else if (INTCODI_CUSTODIA.equals(codi)) {
			integracio.setNom("Custòdia doc.");
		} else if (INTCODI_GESDOC.equals(codi)) {
			integracio.setNom("Gestió doc.");
		} else if (INTCODI_CONVERT.equals(codi)) {
			integracio.setNom("Conversió doc.");
		} else if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		} else if (INTCODI_UNITATS.equals(codi)) {
			integracio.setNom("Unitats admin.");
		} else if (INTCODI_SEU.equals(codi)) {
			integracio.setNom("Seu elect.");
		} else if (INTCODI_CALLBACK.equals(codi)) {
			integracio.setNom("Callback PF");
		} else if (INTCODI_DADESEXT.equals(codi)) {
			integracio.setNom("Dades ext.");
		} else if (INTCODI_EMAIL.equals(codi)) {
			integracio.setNom("Email");
		}
		return integracio;
	}

}
