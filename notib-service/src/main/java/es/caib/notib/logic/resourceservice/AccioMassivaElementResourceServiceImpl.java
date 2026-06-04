package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.model.AccioMassivaElementResource;
import es.caib.notib.logic.intf.model.AccioMassivaResource;
import es.caib.notib.logic.intf.resourceservice.AccioMassivaElementResourceService;
import es.caib.notib.persist.resourceentity.AccioMassivaElementResourceEntity;
import es.caib.notib.persist.resourceentity.AccioMassivaResourceEntity;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei d'elements d'accions massives.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccioMassivaElementResourceServiceImpl extends BaseMutableResourceService<AccioMassivaElementResource, Long, AccioMassivaElementResourceEntity> implements AccioMassivaElementResourceService {


	private final NotificacioResourceRepository notificacioRepository;
	private final NotificacioEnviamentResourceRepository enviamentRepository;

	@PostConstruct
	public void init() {

	}

	@Override
	protected void afterConversion(AccioMassivaElementResourceEntity entity, AccioMassivaElementResource resource) {

		var tipusNotificacio = SeleccioTipus.NOTIFICACIO.equals(entity.getAccioMassiva().getTipusElementSeleccionat());
		var referencia = tipusNotificacio ? notificacioRepository.findById(entity.getElementId()).orElseThrow().getReferencia()
			: enviamentRepository.findById(entity.getElementId()).orElseThrow().getReferenciaEnviament();
		resource.setReferencia(referencia);
	}

}
