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
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
	private final NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;
	private final DocumentResourceRepository documentResourceRepository;
	private final PersonaResourceRepository personaResourceRepository;
	private final LegacyHelper legacyHelper;

	private final EnviamentSmService enviamentSmService;
	private final NotificacioTableHelper notificacioTableHelper;
	private final EnviamentTableHelper enviamentTableHelper;
	private final AuditHelper auditHelper;
	private final NotificacioRepository notificacioRepository;
	private final NotificacioEnviamentRepository notificacioEnviamentRepository;

	@PostConstruct
	public void init() {
		register(null, new NotificacioResourceServiceImpl.InitOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitat, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitatDiesNaturals, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
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
		if (resource.getEnviamentsInfo() != null) {
			saveEnviaments(entity, resource.getEnviamentsInfo());
		}
		notificacioLegacy(entity);
	}

	private void saveEnviaments(
		NotificacioResourceEntity notificacio,
		List<NotificacioEnviamentResource> enviaments) {
		// Crea els enviaments associats amb la notificació a la base de dades.
		enviaments.forEach(e -> {
			NotificacioEnviamentResourceEntity enviamentNou = NotificacioEnviamentResourceEntity.builder().
				resource(e).
				notificacio(notificacio).
				build();
			enviamentNou.setNotificaEstat(EnviamentEstat.PENDENT);
			NotificacioEnviamentResourceEntity enviamentCreat = notificacioEnviamentResourceRepository.save(enviamentNou);
			PersonaResourceEntity titular = saveDestinatari(enviamentCreat, e.getTitularInfo());
			enviamentCreat.setTitular(titular);
			enviamentCreat.setNotificaReferencia(UUID.randomUUID().toString());
			if (e.getRepresentantsInfo() != null) {
				e.getRepresentantsInfo().forEach(r -> saveDestinatari(enviamentCreat, r));
			}
			notificacioEnviamentLegacy(enviamentCreat);
		});
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

	private void notificacioLegacy(NotificacioResourceEntity entity) {
		// Lògica antiga per a les notificacions
		Optional<NotificacioEntity> notificacioEntity = notificacioRepository.findById(entity.getId());
		if (notificacioEntity.isPresent()) {
			// Registra la notificació
			notificacioTableHelper.crearRegistre(notificacioEntity.get());
			// Crea la informació d'auditoria
			auditHelper.auditaNotificacio(
				notificacioEntity.get(),
				AuditService.TipusOperacio.CREATE,
				"NotificacioResourceServiceImpl.afterCreateSave");
			// Dona d'alta els enviaments a la màqina d'estats al finalitzar la transacció
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					if (TransactionSynchronizationManager.isActualTransactionActive()) {
						notificacioEntity.get().getEnviaments().forEach(e -> {
							enviamentSmService.altaEnviament(e.getNotificaReferencia());
						});
					}
				}
			});
		}
	}

	private void notificacioEnviamentLegacy(NotificacioEnviamentResourceEntity entity) {
		// Lògica antiga pels enviaments
		Optional<NotificacioEnviamentEntity> notificacioEnviamentEntity = notificacioEnviamentRepository.findById(
			entity.getId());
		if (notificacioEnviamentEntity.isPresent()) {
			// Registra l'enviament
			enviamentTableHelper.crearRegistre(notificacioEnviamentEntity.get());
			// Crea la informació d'auditoria
			auditHelper.auditaEnviament(
				notificacioEnviamentEntity.get(),
				AuditService.TipusOperacio.CREATE,
				"NotificacioResourceServiceImpl.saveEnviaments");
		}
	}

	/*
	 * Lògica onChange que s'executa al carregar el formulari.
	 */
	private static class InitOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {
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
	private static class CaducitatOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {
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
				Date date = (Date)fieldValue;
				caducitatOnChange(date, previous, target);
			} else if (NotificacioResource.Fields.caducitatDiesNaturals.equals(fieldName)) {
				Integer caducitatDiesNaturals = (Integer)fieldValue;
				caducitatOnChange(caducitatDiesNaturals, previous, target);
			}
		}
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
		// Només feim el canvi si la caducitat és diferent a la que ja hi havia per a evitar bucle infinit d'onChange.
		if (!Objects.equals(caducitat, previous.getCaducitat())) {
			target.setCaducitat(caducitat);
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
		// Només feim el canvi si el nombre de dies naturals és diferent a la que ja hi havia per a evitar bucle
		// infinit d'onChange.
		if (!Objects.equals(numDiesNaturals, previous.getCaducitatDiesNaturals())) {
			target.setCaducitatDiesNaturals(numDiesNaturals);
		}
	}

}
