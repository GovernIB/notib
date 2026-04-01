package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import es.caib.notib.persist.resourceentity.*;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de notificacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioResourceServiceImpl
	extends BaseMutableResourceService<NotificacioResource, Long, NotificacioResourceEntity>
	implements NotificacioResourceService {

	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final LegacyHelper legacyHelper;
	private final NotibPermissionHelper notibPermissionHelper;
	private final NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;
	private final DocumentResourceRepository documentResourceRepository;
	private final PersonaResourceRepository personaResourceRepository;
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;

	@PostConstruct
	public void init() {
		register(null, new NotificacioResourceServiceImpl.InitOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitat, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitatDiesNaturals, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		List<String> andConditions = new ArrayList<>();
		// Condició per a mostrar només les notificacions de l'entitat actual
		andConditions.add("entitat.id:" + userSessionHelper.getCurrentEntitatId());
		// Condició per a mostrar només les notificacions sobre les que es tenen permisos. Les notificacions es poden
		// veure si es compleix algun de les següents condicions:
		//   - L'usuari te permisos de lectura sobre l'òrgan gestor de la notificació.
		//   - L'usuari te permisos de lectura sobre el procediment no comú de la notificació.
		//   - L'usuari te permisos de lectura sobre el procediment comú de la notificació i sobre el seu òrgan gestor.
		List<String> permissionOrConditions = new ArrayList<>();
		String readableOrganGestorIds = notibPermissionHelper.
			organGestorIdsWithPermissionRecursive(BasePermission.READ).
			stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!readableOrganGestorIds.isEmpty()) {
			permissionOrConditions.add("organGestor.id in (" + readableOrganGestorIds + ")");
		}
		String readableProcedimentNoComuIds = notibPermissionHelper.
			procedimentServeiNoComuIdsWithPermission(BasePermission.READ, null).
			stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!readableProcedimentNoComuIds.isEmpty()) {
			permissionOrConditions.add("procediment.id in (" + readableProcedimentNoComuIds + ")");
		}
		String readableProcedimentComuIds = notibPermissionHelper.
			procedimentServeiComuIdsWithPermission(BasePermission.READ, null).
			stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!readableProcedimentComuIds.isEmpty()) {
			permissionOrConditions.add("procediment.id in (" + readableProcedimentComuIds + ")");
		}
		andConditions.add("(" + String.join(" or ", permissionOrConditions) + ")");
		return String.join(" and ", andConditions);
	}

	@Override
	public void beforeCreateSave(
		NotificacioResourceEntity entity,
		NotificacioResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entity.setUsuariCodi(authenticationHelper.getCurrentUserName());
		entity.setEntitat(userSessionHelper.getCurrentEntitat());
		entity.setEmisorDir3Codi(entity.getEntitat().getDir3Codi());
		entity.setComunicacioTipus(NotificacioComunicacioTipusEnumDto.ASINCRON);
		entity.setEstat(NotificacioEstatEnumDto.PENDENT);
		entity.setReferencia(UUID.randomUUID().toString());
		entity.setProcedimentCodiNotib(entity.getProcediment().getCodi());
		emplenarProcedimentOrganGestor(entity);
		if (resource.getDocumentsInfo() != null) {
			saveDocuments(entity, resource.getDocumentsInfo());
		}
	}

	@Override
	public void afterCreateSave(
		NotificacioResourceEntity entity,
		NotificacioResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers,
		boolean anyOrderChanged) {
		List<Long> enviamentsIds = new ArrayList<>();
		if (resource.getEnviamentsInfo() != null) {
			resource.getEnviamentsInfo().forEach(e -> {
				Long enviamentId = saveEnviament(entity, e);
				enviamentsIds.add(enviamentId);
			});
		}
		legacyHelper.altaNotificacio(entity.getId(), enviamentsIds);
	}

	private Long saveEnviament(
		NotificacioResourceEntity notificacio,
		NotificacioEnviamentResource enviament) {
		String uuid = UUID.randomUUID().toString();
		NotificacioEnviamentResourceEntity enviamentNou = NotificacioEnviamentResourceEntity.builder().
			resource(enviament).
			notificacio(notificacio).
			build();
		enviamentNou.setNotificaReferencia(uuid);
		enviamentNou.setNotificaEstat(EnviamentEstat.PENDENT);
		NotificacioEnviamentResourceEntity enviamentCreat = notificacioEnviamentResourceRepository.saveAndFlush(enviamentNou);
		PersonaResourceEntity titular = saveDestinatari(enviamentCreat, enviament.getTitularInfo());
		enviamentCreat.setTitular(titular);
		enviamentCreat.setNotificaReferencia(uuid);
		if (enviament.getRepresentantsInfo() != null) {
			enviament.getRepresentantsInfo().forEach(r -> saveDestinatari(enviamentCreat, r));
		}
		return enviamentCreat.getId();
	}

	private void saveDocuments(
		NotificacioResourceEntity notificacio,
		List<DocumentResource> documents) {
		// Crea els documents associats amb la notificació a la base de dades.
		for (int i = 0; i < documents.size(); i++) {
			DocumentResource document = documents.get(i);
			DocumentResourceEntity documentNou = DocumentResourceEntity.builder().resource(document).build();
			String arxiuGestdocId = legacyHelper.notificacioAdjuntCreate(document.getAttachment());
			documentNou.setArxiuGestdocId(arxiuGestdocId);
			DocumentResourceEntity documentCreat = documentResourceRepository.save(documentNou);
			if (i == 0) {
				notificacio.setDocument(documentCreat);
			} else if (i == 1) {
				notificacio.setDocument2(documentCreat);
			} else if (i == 2) {
				notificacio.setDocument3(documentCreat);
			} else if (i == 3) {
				notificacio.setDocument4(documentCreat);
			} else if (i == 4) {
				notificacio.setDocument5(documentCreat);
			}
		}
	}

	private PersonaResourceEntity saveDestinatari(
		NotificacioEnviamentResourceEntity enviament,
		PersonaResource destinatari) {
		// Crea el destinatari a la base de dades.
		return personaResourceRepository.save(
			PersonaResourceEntity.builder().
				resource(destinatari).
				enviament(enviament).
				build());
	}

	private void emplenarProcedimentOrganGestor(NotificacioResourceEntity entity) {
		if (entity.getProcediment() != null && entity.getProcediment().isComu() && entity.getOrganGestor() != null) {
			Optional<ProcedimentOrganGestorResourceEntity> procedimentOrganGestor = procedimentOrganGestorResourceRepository.findByProcedimentAndOrganGestor(
				entity.getProcediment(),
				entity.getOrganGestor());
			procedimentOrganGestor.ifPresent(entity::setProcedimentOrganGestor);
		}
	}

	/*
	 * Lògica onChange que s'executa al carregar el formulari.
	 */
	static class InitOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {
		@Override
		public void onChange(
			Serializable id,
			NotificacioResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			NotificacioResource target) {
			caducitatOnChange(previous.getCaducitatDiesNaturals(), previous, target);
		}
	}

	/*
	 * Lògica onChange pel camp interessatTipus. Segons el valor d'aquest camp canvien els camps visibles / obligatoris.
	 */
	static class CaducitatOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {
		@Override
		public void onChange(
			Serializable id,
			NotificacioResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			NotificacioResource target) {
			if (NotificacioResource.Fields.caducitat.equals(fieldName)) {
				boolean isCaducitatDiesNaturalsInPreviousFieldNames =
					previousFieldNames != null &&
					previousFieldNames.length > 0 &&
					NotificacioResource.Fields.caducitatDiesNaturals.equals(previousFieldNames[0]);
				if (!isCaducitatDiesNaturalsInPreviousFieldNames) {
					Date date = (Date) fieldValue;
					caducitatOnChange(date, previous, target);
				}
			} else if (NotificacioResource.Fields.caducitatDiesNaturals.equals(fieldName)) {
				boolean isCaducitatInPreviousFieldNames =
					previousFieldNames != null &&
						previousFieldNames.length > 0 &&
						NotificacioResource.Fields.caducitat.equals(previousFieldNames[0]);
				if (!isCaducitatInPreviousFieldNames) {
					Integer caducitatDiesNaturals = (Integer) fieldValue;
					caducitatOnChange(caducitatDiesNaturals, previous, target);
				}
			}
		}
	}

	private static void caducitatOnChange(
		Date caducitat,
		NotificacioResource previous,
		NotificacioResource target) {
		Integer numDiesNaturals = null;
		if (caducitat != null) {
			LocalDate dataConvertida = caducitat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			numDiesNaturals = (int)ChronoUnit.DAYS.between(LocalDate.now(), dataConvertida);
		}
		target.setCaducitatDiesNaturals(numDiesNaturals);
		/*// Només feim el canvi si el nombre de dies naturals és diferent a la que ja hi havia per a evitar bucle
		// infinit d'onChange.
		if (!Objects.equals(numDiesNaturals, previous.getCaducitatDiesNaturals())) {
			target.setCaducitatDiesNaturals(numDiesNaturals);
		}*/
	}

	private static void caducitatOnChange(
		Integer caducitatDiesNaturals,
		NotificacioResource previous,
		NotificacioResource target) {
		Date caducitat = null;
		if (caducitatDiesNaturals != null) {
			caducitat = Date.from(
				LocalDate.now().
					plusDays(caducitatDiesNaturals).
					atStartOfDay(ZoneId.systemDefault()).
					toInstant());
		}
		target.setCaducitat(caducitat);
		/*// Només feim el canvi si la caducitat és diferent a la que ja hi havia per a evitar bucle infinit d'onChange.
		if (!Objects.equals(caducitat, previous.getCaducitat())) {
			target.setCaducitat(caducitat);
		}*/
	}

}
