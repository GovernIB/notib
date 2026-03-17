package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.logic.intf.resourceservice.CacheResourceService;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de consulta de caches de l'aplciació
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheResourceServiceImpl extends BaseNoDatabaseReadonlyResourceService<CacheResource, String> implements CacheResourceService {

	@Override
	protected Page<NoDatabaseResourceEntity<CacheResource, String>> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		Page<CacheResource> page = Page.empty();
		return page.map(this::toResourceEntity);
	}

	private NoDatabaseResourceEntity<CacheResource, String> toResourceEntity(CacheResource resource) {
		return new NoDatabaseResourceEntity<>(resource.getCodi(), resource);
	}
}
