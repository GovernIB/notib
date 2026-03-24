package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.resourceservice.PersonaResourceService;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de gestió de persones destinatàries d'una notificació.
 * Aquest servei només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al formulari
 * del front.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PersonaResourceServiceImpl
	extends BaseMutableResourceService<PersonaResource, Long, PersonaResourceEntity>
	implements PersonaResourceService {

	@PostConstruct
	public void init() {
		register(null, new InitOnChangeLogicProcessor());
		register(PersonaResource.Fields.interessatTipus, new InteressatTipusOnChangeLogicProcessor());
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que no es retorni mai cap
	 * resultat.
	 */
	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return "id is null";
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta crear un
	 * recurs es llença una excepció.
	 */
	@Override
	protected void beforeCreateSave(
		PersonaResourceEntity entity,
		PersonaResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Create is not allowed");
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta modificar un
	 * recurs es llença una excepció.
	 */
	@Override
	protected void beforeUpdateSave(
		PersonaResourceEntity entity,
		PersonaResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotUpdatedException(getResourceClass(), "" + resource.getId(), "Update is not allowed");
	}

	/*
	 * Lògica onChange que s'executa al carregar el formulari.
	 */
	static class InitOnChangeLogicProcessor implements OnChangeLogicProcessor<PersonaResource> {
		@Override
		public void onChange(
			Serializable id,
			PersonaResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			PersonaResource target) {
			interessatTipusOnChange(previous.getInteressatTipus(), target);
		}
	}

	/*
	 * Lògica onChange pel camp interessatTipus. Segons el valor d'aquest camp canvien els camps visibles / obligatoris.
	 */
	static class InteressatTipusOnChangeLogicProcessor implements OnChangeLogicProcessor<PersonaResource> {
		@Override
		public void onChange(
			Serializable id,
			PersonaResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			PersonaResource target) {
			interessatTipusOnChange((InteressatTipus)fieldValue, target);
		}
	}

	static void interessatTipusOnChange(
		InteressatTipus interessatTipus,
		PersonaResource target) {
		// En funció del tipus d'interessat configura:
		//   - La visibilitat dels camps
		//   - L'obligatorietat dels camps
		//   - Posa a null tots els camps no visibles
		if (InteressatTipus.FISICA.equals(interessatTipus)) {
			target.setVisibleDocumentTipus(false);
			target.setVisibleNif(true);
			target.setVisibleNom(true);
			target.setVisibleLlinatge1(true);
			target.setVisibleLlinatge2(true);
			target.setVisibleTelefon(true);
			target.setVisibleEmail(true);
			target.setVisibleRaoSocial(false);
			target.setVisibleDir3Codi(false);
			target.setVisibleIncapacitat(true);
			target.setRequiredNif(true);
			target.setRequiredNom(true);
			target.setRequiredLlinatge1(true);
			target.setRequiredEmail(false);
			target.setRequiredRaoSocial(false);
			target.setRequiredDir3Codi(false);
			target.setDocumentTipus(null);
			target.setRaoSocial(null);
			target.setDir3Codi(null);
		} else if (InteressatTipus.ADMINISTRACIO.equals(interessatTipus)) {
			target.setVisibleDocumentTipus(false);
			target.setVisibleNif(true);
			target.setVisibleNom(false);
			target.setVisibleLlinatge1(false);
			target.setVisibleLlinatge2(false);
			target.setVisibleTelefon(false);
			target.setVisibleEmail(true);
			target.setVisibleRaoSocial(false);
			target.setVisibleDir3Codi(true);
			target.setVisibleIncapacitat(false);
			target.setRequiredNif(true);
			target.setRequiredNom(false);
			target.setRequiredLlinatge1(false);
			target.setRequiredEmail(false);
			target.setRequiredRaoSocial(false);
			target.setRequiredDir3Codi(true);
			target.setDocumentTipus(null);
			target.setNom(null);
			target.setLlinatge1(null);
			target.setLlinatge2(null);
			target.setTelefon(null);
			target.setRaoSocial(null);
			target.setDir3Codi(null);
			target.setIncapacitat(false);
		} else if (InteressatTipus.JURIDICA.equals(interessatTipus)) {
			target.setVisibleDocumentTipus(false);
			target.setVisibleNif(true);
			target.setVisibleNom(false);
			target.setVisibleLlinatge1(false);
			target.setVisibleLlinatge2(false);
			target.setVisibleTelefon(false);
			target.setVisibleEmail(true);
			target.setVisibleRaoSocial(true);
			target.setVisibleDir3Codi(false);
			target.setVisibleIncapacitat(true);
			target.setRequiredNif(true);
			target.setRequiredNom(false);
			target.setRequiredLlinatge1(false);
			target.setRequiredEmail(false);
			target.setRequiredRaoSocial(true);
			target.setRequiredDir3Codi(false);
			target.setDocumentTipus(null);
			target.setNom(null);
			target.setLlinatge1(null);
			target.setLlinatge2(null);
			target.setTelefon(null);
			target.setDir3Codi(null);
		} else if (InteressatTipus.FISICA_SENSE_NIF.equals(interessatTipus)) {
			target.setVisibleDocumentTipus(true);
			target.setVisibleNif(true);
			target.setVisibleNom(true);
			target.setVisibleLlinatge1(true);
			target.setVisibleLlinatge2(true);
			target.setVisibleTelefon(false);
			target.setVisibleEmail(true);
			target.setVisibleRaoSocial(false);
			target.setVisibleDir3Codi(false);
			target.setVisibleIncapacitat(true);
			target.setRequiredNif(false);
			target.setRequiredNom(true);
			target.setRequiredLlinatge1(true);
			target.setRequiredEmail(true);
			target.setRequiredRaoSocial(false);
			target.setRequiredDir3Codi(false);
			target.setTelefon(null);
			target.setRaoSocial(null);
			target.setDir3Codi(null);
		}
	}

}
