package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.LegacyHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.PropertyConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.FieldOption;
import es.caib.notib.logic.intf.model.Dir3Resource;
import es.caib.notib.logic.intf.resourceservice.Dir3ResourceService;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	private final ConfigHelper configHelper;
	private final UserSessionHelper userSessionHelper;
	private final OrganGestorResourceRepository organGestorResourceRepository;

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
		List<String[]> filterTriplets = extractFilterTriplets(filter);
		String codi = getFieldValueFromFilterTriplets("codi", ":", filterTriplets);
		String denominacio = getFieldValueFromFilterTriplets("denominacio", ":", filterTriplets);
		String nivellAdministracio = getFieldValueFromFilterTriplets("nivellAdministracio", ":", filterTriplets);
		String comunitatAutonoma = getFieldValueFromFilterTriplets("comunitatAutonoma", ":", filterTriplets);
		String provincia = getFieldValueFromFilterTriplets("provincia", ":", filterTriplets);
		String municipi = getFieldValueFromFilterTriplets("municipi", ":", filterTriplets);
		Page<Dir3Resource> page = legacyHelper.dir3FindMultiple(
				codi,
				denominacio,
				nivellAdministracio != null ? Long.parseLong(nivellAdministracio) : null,
				comunitatAutonoma != null ? Long.parseLong(comunitatAutonoma) : null,
				provincia != null ? Long.parseLong(provincia) : null,
				municipi,
				null,
				null,
				pageable);
		calcularCampsRecurs(page);
		return page.map(this::toResourceEntity);
	}

	private static final Pattern TRIPLET_PATTERN = Pattern.compile(
		"([a-zA-Z_][a-zA-Z0-9_\\.]*)" +
			"\\s*" +
			"(:|=|!=|>=|<=|>|<|~|!~|\\bin\\b)" +
			"\\s*" +
			"(\\[[^\\]]*\\]|'[^']*'|\"[^\"]*\"|\\d+\\.?\\d*|[^\\s\\)]+)",
		Pattern.CASE_INSENSITIVE);
	private List<String[]> extractFilterTriplets(String filter) {
		Matcher matcher = TRIPLET_PATTERN.matcher(filter);
		List<String[]> triplets = new ArrayList<>();
		while (matcher.find()) {
			String field = matcher.group(1);
			String op = matcher.group(2);
			String value = matcher.group(3);
			if (value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length() - 1);
			}
			triplets.add(new String[]{field, op, value});
		}
		return triplets;
	}

	private String getFieldValueFromFilterTriplets(
		String field,
		String op,
		List<String[]> triplets) {
		return triplets.stream().
			filter(t -> t[0].equals(field) && t[1].equals(op)).
			findFirst().
			map(t -> t[2]).
			orElse(null);
	}

	private List<List<String>> partition(Set<String> coll, int size) {
		List<String> list = new ArrayList<>(coll);
		List<List<String>> parts = new ArrayList<>();
		for (int i = 0; i < list.size(); i += size) {
			parts.add(list.subList(i, Math.min(i + size, list.size())));
		}
		return parts;
	}
	private void calcularCampsRecurs(Page<Dir3Resource> page) {

		if (page.isEmpty()) {
			return;
		}
		// Calcula i emplena els camps que falten del recurs
		var isPermesComunicacionsSirPropiaEntitat = configHelper.getConfigAsBoolean(PropertyConfig.PROP_COMUNICACIONS_SIR_INTERNES);
		// Cerca els codis DIR3 locals que coincideixin amb algun dels de la pàgina de resultats
		var organCodis = page.stream().map(Dir3Resource::getCodi).collect(Collectors.toSet());
		List<String> codisLocals = new ArrayList<>();
		if (organCodis.size()  <= 1000) {
			codisLocals = organGestorResourceRepository.findCodisByEntitatAndCodiIn(userSessionHelper.getCurrentEntitat(), organCodis);
		} else {
			var parts = partition(organCodis, 1000);
			for (List<String> chunk : parts) {
				// Convertim a Set per si el mètode del repositori espera un Set<String>
				Set<String> chunkSet = new HashSet<>(chunk);
				var partial = organGestorResourceRepository.findCodisByEntitatAndCodiIn(userSessionHelper.getCurrentEntitat(), chunkSet);
				if (partial != null) {
					codisLocals.addAll(partial);
				}
			}
		}
		for (var r : page) {
			if (r.getCif() == null) {
				r.setNoCif(true);
				continue;
			}
			if (!r.isSir()) {
				r.setNoSir(true);
				continue;
			}
			if (!isPermesComunicacionsSirPropiaEntitat && codisLocals.contains(r.getCodi())) {
				r.setViaValib(true);
				continue;
			}
			r.setSelectable(true);
		}
	}

	private NoDatabaseResourceEntity<Dir3Resource, String> toResourceEntity(Dir3Resource resource) {
		return new NoDatabaseResourceEntity<>(resource.getCodi(), resource);
	}

	public class Dir3FilterProcessor implements FilterProcessor<Dir3Resource.Dir3ResourceFilter> {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			String comunitatAutonoma = null;
			if (requestParameterMap != null && requestParameterMap.get("comunitatAutonoma") != null) {
				comunitatAutonoma = requestParameterMap.get("comunitatAutonoma")[0];
			}
			String provincia = null;
			if (requestParameterMap != null && requestParameterMap.get("provincia") != null) {
				provincia = requestParameterMap.get("provincia")[0];
			}
			if (Dir3Resource.Dir3ResourceFilter.Fields.nivellAdministracio.equals(fieldName)) {
				return Arrays.asList(
					new FieldOption("1", "Administración del estado"),
					new FieldOption("2", "Administración autonómica"),
					new FieldOption("3", "Administración local"),
					new FieldOption("4", "Universidades"),
					new FieldOption("5", "Otras Instituciones"),
					new FieldOption("6", "Administración de justicia"),
					new FieldOption("7", "Históricos AGE"),
					new FieldOption("8", "Históricos CCAA"),
					new FieldOption("9", "Históricos EELL"));
			} else if (Dir3Resource.Dir3ResourceFilter.Fields.comunitatAutonoma.equals(fieldName)) {
				return legacyHelper.dir3ConsultaComunitatsAutonomes().stream().
					map(c -> new FieldOption(c.getId(), c.getDescripcio())).
					collect(Collectors.toList());
			} else if (Dir3Resource.Dir3ResourceFilter.Fields.provincia.equals(fieldName)) {
				return legacyHelper.dir3ConsultaProvincies(comunitatAutonoma).stream().
					map(c -> new FieldOption(c.getId(), c.getDescripcio())).
					collect(Collectors.toList());
			} else if (Dir3Resource.Dir3ResourceFilter.Fields.municipi.equals(fieldName) && provincia != null) {
				return legacyHelper.dir3ConsultaLocalitats(provincia).stream().
					map(c -> new FieldOption(c.getId(), c.getDescripcio())).
					collect(Collectors.toList());
			}
			return new ArrayList<>();
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
