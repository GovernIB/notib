package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.notib.logic.intf.model.MetriquesResource;
import es.caib.notib.logic.intf.resourceservice.MetriquesResourceService;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.metriques.MetriquesJsonReportGenerator;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import es.caib.notib.persist.resourceentity.MetriquesResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de consulta de metriques de l'aplciació
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetriquesResourceServiceImpl extends BaseMutableResourceService<MetriquesResource, String, MetriquesResourceEntity> implements MetriquesResourceService {

	private final AplicacioService aplicacioService;

	@PostConstruct
	public void init() {
		register(MetriquesResource.REPORT_DESCARREGAR_METRIQUES_JSON, new MetriquesJsonReportGenerator(aplicacioService));
	}

	@Override
	public Page<MetriquesResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {
		return null;
	}

	@Override
	public MetriquesResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {

		var metriques =  new MetriquesResource();
		metriques.setId("metriques");
		var metriquesString = "";
		try {
			metriquesString = aplicacioService.getMetrics();
		} catch (Exception ex) {
			log.error("[MetriquesResourceServiceImpl] Error obtinguent les metriques", ex);
		}
		metriques.setMetriques(metriquesString);
		return metriques;
	}

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}

	private NoDatabaseResourceEntity<MetriquesResource, String> toResourceEntity(MetriquesResource resource) {
		return new NoDatabaseResourceEntity<>(resource.getMetriques(), resource);
	}


}
