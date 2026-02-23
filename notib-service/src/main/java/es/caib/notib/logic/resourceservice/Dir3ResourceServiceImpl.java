package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.notib.logic.helper.LegacyHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.FieldOption;
import es.caib.notib.logic.intf.model.Dir3Resource;
import es.caib.notib.logic.intf.resourceservice.Dir3ResourceService;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementació del servei de consulta d'unitats organitzatives directament a DIR3.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Dir3ResourceServiceImpl extends BaseNoDatabaseReadonlyResourceService<Dir3Resource, String> implements Dir3ResourceService {

	private final LegacyHelper legacyHelper;

	@PostConstruct
	public void init() {
		register(Dir3Resource.FILTER_CODE, new Dir3ResourceServiceImpl.Dir3FilterProcessor());
	}

	@Override
	protected Optional<NoDatabaseResourceEntity<Dir3Resource, String>> entityRepositoryFindOne(String id) {
		Optional<Dir3Resource> dir3 = legacyHelper.dir3FindOne(id);
		return dir3.map(this::toResourceEntity);
	}

	@Override
	protected Page<NoDatabaseResourceEntity<Dir3Resource, String>> entityRepositoryFindEntities(
		String quickFilter,
		String filter,
		String[] namedQueries,
		Pageable pageable) {
		return legacyHelper.dir3FindMultiple(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			pageable).
			map(this::toResourceEntity);
	}

	private NoDatabaseResourceEntity<Dir3Resource, String> toResourceEntity(Dir3Resource resource) {
		return new NoDatabaseResourceEntity<>(resource.getCodi(), resource);
	}

	public static class Dir3FilterProcessor implements FilterProcessor<Dir3Resource.Dir3ResourceFilter> {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			if (Dir3Resource.Dir3ResourceFilter.Fields.comunitatAutonoma.equals(fieldName)) {
				return new ArrayList<>();
			} else if (Dir3Resource.Dir3ResourceFilter.Fields.provincia.equals(fieldName)) {
				return new ArrayList<>();
			} else if (Dir3Resource.Dir3ResourceFilter.Fields.municipi.equals(fieldName)) {
				return new ArrayList<>();
			} else {
				return new ArrayList<>();
			}
		}
		@Override
		public void onChange(
			Serializable id,
			Dir3Resource.Dir3ResourceFilter previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			Dir3Resource.Dir3ResourceFilter target) {
		}
	}

}
