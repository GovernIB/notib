package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.*;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import es.caib.notib.persist.resourceentity.*;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
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
		register(NotificacioResource.Fields.organGestor, new NotificacioResourceServiceImpl.OrganGestorOnChangeLogicProcessor());
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
		// Condició per a mostrar només les notificacions amb permís de lectura
		String permissionFilter = springFilterWithReadPermission();
		if (!permissionFilter.isEmpty()) {
			andConditions.add("(" + permissionFilter + ")");
		}
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
		checkCreatePermission(entity);
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
	 * Condició en format Spring Filter per a mostrar només les notificacions sobre les que es tenen permisos. Les
	 * notificacions es poden veure si es compleix alguna de les següents condicions:
	 *   a) L'usuari te permís sobre l'òrgan gestor de la notificació.
	 *   b) La notificació te un procediment no comú i l'usuari te permís sobre aquest procediment.
	 *   c) La notificació te un procediment comú amb "requereix permisos directes" i l'usuari te permís
	 *      sobre la combinació organ gestor - procediment de la notificació.
	 *   d) La notificació te un procediment comú sense "requereix permisos directes",
	 *      l'usuari te permís sobre la combinació organ gestor - procediment de la notificació i les combinacions
	 *      òrgan gestor - procediment son únicament dels òrgans gestors amb permís de procediments comuns.
	 */
	private String springFilterWithReadPermission() {
		List<Long> organGestorIds = notibPermissionHelper.organGestorIdsWithPermissionRecursive(BasePermission.READ);
		List<Long> procedimentNoComuIds = notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(
			BasePermission.READ,
			null);
		List<Long> procedimentComuOrganGestorIds = notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(
			BasePermission.READ,
			null);
		List<String> permissionOrConditions = new ArrayList<>();
		// a)
		String joinedOrganGestorIds = organGestorIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedOrganGestorIds.isEmpty()) {
			permissionOrConditions.add("organGestor.id in (" + joinedOrganGestorIds + ")");
		}
		// b)
		String joinedProcedimentNoComuIds = procedimentNoComuIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentNoComuIds.isEmpty()) {
			permissionOrConditions.add("procediment.id in (" + joinedProcedimentNoComuIds + ")");
		}
		// c) o d)
		String joinedProcedimentComuOrganGestorIds = procedimentComuOrganGestorIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentComuOrganGestorIds.isEmpty()) {
			permissionOrConditions.add("procedimentOrganGestor.id in (" + joinedProcedimentComuOrganGestorIds + ")");
		}
		return String.join(" or ", permissionOrConditions);
	}

	/*
	 * Es verifica si es tenen permisos per a crear la notificació. Una notificació es pot crear si es compleix
	 * alguna de les següents condicions:
	 *   a) L'usuari te permís sobre l'òrgan gestor de la notificació.
	 *   b) La notificació te un procediment no comú i l'usuari te permís sobre aquest procediment.
	 *   c) La notificació te un procediment comú amb "requereix permisos directes" i l'usuari te permís sobre la
	 *      combinació organ gestor - procediment de la notificació.
	 *   d) La notificació te un procediment comú sense "requereix permisos directes",
	 *      l'usuari te permís sobre la combinació organ gestor - procediment de la notificació i
	 *      les combinacions òrgan gestor - procediment son únicament dels òrgans gestors amb permís de procediments
	 *      comuns.
	 */
	private void checkCreatePermission(NotificacioResourceEntity entity) {
		Permission organGestorPermission = getOrganGestorCreatePermissionForEnviamentTipus(entity.getEnviamentTipus());
		Permission procedimentPermission = getProcedimentCreatePermissionForEnviamentTipus(entity.getEnviamentTipus());
		List<Long> organGestorIds = notibPermissionHelper.organGestorIdsWithPermissionRecursive(organGestorPermission);
		List<Long> procedimentNoComuIds = notibPermissionHelper.procedimentServeiNoComuIdsWithPermission(
			procedimentPermission,
			null);
		List<Long> procedimentComuOrganGestorIds = notibPermissionHelper.procedimentServeiComuOrganGestorIdsWithPermission(
			procedimentPermission,
			null);
		Long organGestorId = entity.getOrganGestor().getId();
		Long procedimentId = entity.getProcediment().getId();
		Long procedimentOrganGestorId = entity.getProcedimentOrganGestor().getId();
		boolean permissionGranted = (organGestorId != null && organGestorIds.contains(organGestorId)) || // a)
			(procedimentId != null && procedimentNoComuIds.contains(procedimentId)) || // b)
			(procedimentOrganGestorId != null && procedimentComuOrganGestorIds.contains(procedimentOrganGestorId)); // c) o d)
		if (!permissionGranted) {
			throw new ResourceNotCreatedException(
				NotificacioResource.class,
				"Not allowed to create notificació. Permission check failed.");
		}
	}

	private Permission getOrganGestorCreatePermissionForEnviamentTipus(EnviamentTipus enviamentTipus) {
		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM5;
		} else if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM6;
		} else {
			return ExtendedPermission.PERM4;
		}
	}

	private Permission getProcedimentCreatePermissionForEnviamentTipus(EnviamentTipus enviamentTipus) {
		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM8;
		} else if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM7;
		} else {
			return ExtendedPermission.PERM5;
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
	 * Lògica onChange pel camp organGestor. Si l'usuari te permís "comunicacions sense procediment" sobre l'òrgan
	 * gestor i la notificació és una comunicació s'ha de posar el camp procedimentRequired a false.
	 */
	class OrganGestorOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {
		@Override
		public void onChange(
			Serializable id,
			NotificacioResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			NotificacioResource target) {
			ResourceReference<OrganGestorResource, Long> organGestor = (ResourceReference)fieldValue;
			boolean isComunicacio = previous.getEnviamentTipus() != null &&
				(EnviamentTipus.COMUNICACIO.equals(previous.getEnviamentTipus()) || EnviamentTipus.SIR.equals(previous.getEnviamentTipus()));
			if (organGestor != null && isComunicacio) {
				List<Long> organGestorIdsWithPermission = notibPermissionHelper.organGestorIdsWithPermissionRecursive(
					ExtendedPermission.PERM7);
				boolean hasComunicacionsSenseProcedimentPermission = organGestorIdsWithPermission.contains(
					organGestor.getId());
				target.setProcedimentRequired(!hasComunicacionsSenseProcedimentPermission);
			} else {
				target.setProcedimentRequired(true);
			}
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
