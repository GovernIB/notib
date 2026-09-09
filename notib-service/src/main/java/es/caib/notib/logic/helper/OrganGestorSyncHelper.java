package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Helper per a sincronitzar les unitats organitzatives d'una entitat amb DIR3.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorSyncHelper {

	private final PluginHelper pluginHelper;
	private final OrganGestorLlibreOficinaUpdateHelper organGestorLlibreOficinaHelper;
	private final OrganGestorResourceRepository organGestorResourceRepository;

	private final SseEventService progressEventService;

	private final OrganGestorRepository organGestorRepository;

	/**
	 * Sincronitza els òrgans gestors d'una entitat amb la informació actualitzada de DIR3.
	 *
	 * @param entitat l'entitat de la qual es volen actualitzar els òrgans.
	 * @param simular indica si s'han de guardar o no els canvis a la base de dades.
	 * @return la llista de canvis a realitzar als òrgans de la base de dades.
	 */
	public OrganGestorDir3Sync sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular) {
		return sincronitzar(entitat, simular, SseEvent.SseEventName.DIR3_SYNC);
	}

	/**
	 * Sincronitza els òrgans gestors d'una entitat amb la informació actualitzada de DIR3.
	 *
	 * @param entitat l'entitat de la qual es volen actualitzar els òrgans.
	 * @param simular indica si s'han de guardar o no els canvis a la base de dades.
	 * @param eventName el nom de l'event SSE sota el qual s'han de publicar els events de progrés.
	 * @return la llista de canvis a realitzar als òrgans de la base de dades.
	 */
	public OrganGestorDir3Sync sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular,
		SseEvent.SseEventName eventName) {
		publishProgressEvent(
			eventName,
			SseEvent.SseEventStatus.RUNNING,
			0,
			"Consultant canvis a DIR3CAIB");
		// Consulta els canvis des de la darrera sincronització DIR3
		Date dataActualitzacio = entitat.getDataActualitzacio() != null ? Date.from(
			entitat.getDataActualitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		Date dataSincronitzacio = entitat.getDataSincronitzacio() != null ? Date.from(
			entitat.getDataSincronitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		List<NodeDir3> dir3SyncNodes = pluginHelper.unitatsOrganitzativesFindByPare(
			entitat.getCodi(),
			entitat.getDir3Codi(),
			dataActualitzacio,
			dataSincronitzacio);
		publishProgressEvent(
			eventName,
			SseEvent.SseEventStatus.RUNNING,
			simular ? 70 : 5,
			"Processant canvis rebuts de DIR3CAIB");
		// Obté els òrgans gestors de l'entitat
		List<OrganGestorResourceEntity> organsGestors = organGestorResourceRepository.findByEntitat(entitat);
		// Obté una llista dels nodes de la sincronització DIR3 que existeixen a la base de dades i que acaben en una
		// extinció (estat 'E').
		NodeDir3[] extincionsDarreraVersio = getDir3SyncNodesExistentsDarreraVersioExtincio(dir3SyncNodes, organsGestors);
		// Itera els nodes obtinguts i emplena la llista d'extincions i els mapes de divisions i de fusions/substitucions.
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio> extincions = new ArrayList<>();
		MultiValuedMap<NodeDir3, NodeDir3> divisionsMap = new ArrayListValuedHashMap<>();
		MultiValuedMap<NodeDir3, NodeDir3> fusionsOSubstitucionsMap = new ArrayListValuedHashMap<>();
		for (NodeDir3 extincioDarreraVersio : extincionsDarreraVersio) {
			List<NodeDir3> historicosUo = getHistoricosUo(extincioDarreraVersio, dir3SyncNodes, organsGestors);
			long numHistoricosUoVigents = historicosUo.stream().
				filter(uo -> OrganGestorEstatEnum.V.name().equals(uo.getEstat())).
				count();
			if (numHistoricosUoVigents == 0) {
				extincions.add(
					new OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio(
						toArbreItem(extincioDarreraVersio)));
			} else if (numHistoricosUoVigents == 1) {
				NodeDir3 primerHistoricoUo = historicosUo.get(0);
				Optional<NodeDir3> fusionsOSubstitucionsKeyWithSameCodi = fusionsOSubstitucionsMap.keySet().stream().
					filter(uo -> uo.getCodi().equals(primerHistoricoUo.getCodi())).
					findFirst();
				if (fusionsOSubstitucionsKeyWithSameCodi.isPresent()) {
					fusionsOSubstitucionsMap.put(fusionsOSubstitucionsKeyWithSameCodi.get(), extincioDarreraVersio);
				} else {
					fusionsOSubstitucionsMap.put(primerHistoricoUo, extincioDarreraVersio);
				}
			} else { // numHistoricosUoVigents > 1
				for (NodeDir3 historicoUo : historicosUo) {
					divisionsMap.put(extincioDarreraVersio, historicoUo);
				}
			}
		}
		// Separa les fusions de les substitucions
		MultiValuedMap<NodeDir3, NodeDir3> fusionsMap = new ArrayListValuedHashMap<>();
		MultiValuedMap<NodeDir3, NodeDir3> substitucionsMap = new ArrayListValuedHashMap<>();
		fusionsOSubstitucionsMap.asMap().forEach((key, values) -> {
			if (values.size() > 1) {
				values.forEach(value -> {
					boolean existsInFusionsMap = fusionsMap.get(key).stream().
						anyMatch(uo -> uo.getCodi().equals(value.getCodi()));
					if (!existsInFusionsMap) {
						fusionsMap.put(key, value);
					} else {
						log.warn(
							"Detected duplication of organs in prediction of fusion. Unitat {} already added to fusion into {}. Probably caused by error in DIR3",
							value.getCodi(),
							key.getCodi());
					}
				});
			} else if (values.size() == 1) {
				substitucionsMap.put(key, values.iterator().next());
			}
		});
		// Emplena la resposta
		OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] substitucions = substitucionsMap.keySet().stream()
			.flatMap(key -> substitucionsMap.get(key).stream().
				map(value -> new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio(
					toArbreItem(key),
					toArbreItem(value))))
			.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[]::new);
		OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio[] fusions = fusionsMap.keySet().stream().
			map(key -> new OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio(
				toArbreItems(fusionsMap.get(key)),
				toArbreItem(key))).
			toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio[]::new);
		OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[] divisions = divisionsMap.keySet().stream().
			map(key -> new OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio(
				toArbreItem(key),
				toArbreItems(divisionsMap.get(key)))).
			toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[]::new);
		OrganGestorDir3Sync resposta = new OrganGestorDir3Sync(
			null,
			getCreacions(dir3SyncNodes, organsGestors, substitucionsMap, fusionsMap, divisionsMap),
			getModificacions(dir3SyncNodes, organsGestors),
			substitucions,
			extincions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio[]::new),
			fusions,
			divisions,
			dir3SyncNodes.isEmpty(),
			simular);
		if (!simular) {
			publishProgressEvent(
				eventName,
				SseEvent.SseEventStatus.RUNNING,
				10,
				"Actualitzant informació dels òrgans gestors");
			actualitzarOrgansGestors(entitat, dir3SyncNodes, organsGestors, eventName);
			persistirTransicions(substitucionsMap, fusionsMap, divisionsMap);
			LocalDate now = LocalDate.now();
			if (entitat.getDataSincronitzacio() == null) {
				entitat.setDataSincronitzacio(now);
			}
			entitat.setDataActualitzacio(now);
			publishProgressEvent(
				eventName,
				SseEvent.SseEventStatus.DONE,
				100,
				null);
		} else {
			publishProgressEvent(
				eventName,
				SseEvent.SseEventStatus.DONE,
				100,
				null);
		}
		return resposta;
	}

	private void actualitzarOrgansGestors(
		EntitatResourceEntity entitat,
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors,
		SseEvent.SseEventName eventName) {
		int numDir3SyncNodes = dir3SyncNodes.size();
		int nextPublishableProgress = 0;
		for (int i = 0; i < numDir3SyncNodes; i++) {
			NodeDir3 dir3SyncNode = dir3SyncNodes.get(i);
			Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
				filter(o -> o.getCodi().equals(dir3SyncNode.getCodi())).
				findFirst();
			actualitzarOrganGestor(entitat, dir3SyncNode, organGestor.orElse(null));
			int percentProcessed = (i + 1) * 100 / numDir3SyncNodes;
			if (percentProcessed >= nextPublishableProgress) {
				int percent = 10 + 90 * percentProcessed / 100;
				publishProgressEvent(
					eventName,
					SseEvent.SseEventStatus.RUNNING,
					percent,
					"Actualitzant informació dels òrgans gestors");
				nextPublishableProgress += 10;
			}
		}
	}

	private void actualitzarOrganGestor(EntitatResourceEntity entitat, NodeDir3 dir3SyncNode, OrganGestorResourceEntity organGestor) {

		// Actualitza l'òrgan gestor si ja existeix a la BD o el crea si no existeix
		OrganGestorResourceEntity updated;
		var msg = "òrgan gestor (entitatId=" + entitat.getId()
					+ ", organCodiDir3=" + dir3SyncNode.getCodi()
					+ ", organNom=" + getOrganGestorNomFromDir3Node(dir3SyncNode)
					+ ", organEstat=" + OrganGestorEstatEnum.valueOf(dir3SyncNode.getEstat()) + ")";
		if (organGestor != null) {
			msg = "Actualitzant " + msg;
			NotibLogger.getInstance().info(msg, log, LoggingTipus.UNITATS);
			organGestor.setNom(getOrganGestorNomFromDir3Node(dir3SyncNode));
			organGestor.setNomEs(dir3SyncNode.getDenominacio());
			organGestor.setCodiPare(dir3SyncNode.getSuperior());
			organGestor.setEstat(OrganGestorEstatEnum.valueOf(dir3SyncNode.getEstat()));
			updated = organGestor;
		} else {
			msg = "Creant " + msg;
			NotibLogger.getInstance().info(msg, log, LoggingTipus.UNITATS);
			var organGestorResource = new OrganGestorResource();
			organGestorResource.setCodi(dir3SyncNode.getCodi());
			organGestorResource.setNom(getOrganGestorNomFromDir3Node(dir3SyncNode));
			organGestorResource.setNomEs(dir3SyncNode.getDenominacio());
			organGestorResource.setCodiPare(dir3SyncNode.getSuperior());
			organGestorResource.setEstat(OrganGestorEstatEnum.valueOf(dir3SyncNode.getEstat()));
			var organResourceEntity = OrganGestorResourceEntity.builder().resource(organGestorResource).entitat(entitat).build();
			updated = organGestorResourceRepository.save(organResourceEntity);
		}
		if (updated.getCodiPare() != null) {
			var organResourceEntity = organGestorResourceRepository.findByCodiAndEntitatAndEstat(updated.getCodiPare(), entitat, OrganGestorEstatEnum.V).orElse(null);
			updated.setPare(organResourceEntity);
		}
		organGestorLlibreOficinaHelper.updateLlibre(updated);
		organGestorLlibreOficinaHelper.updateOficina(updated, null);
	}

	private NodeDir3[] getDir3SyncNodesExistentsDarreraVersioExtincio(List<NodeDir3> dir3SyncNodes, List<OrganGestorResourceEntity> organsGestors) {

		// Aquest mètode retorna una llista dels nodes DIR3 de la sicronització que ja existeixen a la base de dades
		// i acaben en una extinció.
		// Primer obté un mapa de llistes de nodes DIR3 amb el mateix codi ordenats per versió ascendent.
		Map<String, List<NodeDir3>> dir3SyncNodesMapSorted = getDir3SyncNodesMapSortedByVersionAsc(dir3SyncNodes);
		List<NodeDir3> extincions = new ArrayList<>();
		// Fa una iteració dels codis DIR3 per anar emplentat la llista amb el resultat.
		for (Map.Entry<String, List<NodeDir3>> entry : dir3SyncNodesMapSorted.entrySet()) {
			// Obté la darrera versió de cada codi DIR3 diferent de la llista retornada per la sincronització
			NodeDir3 lastNode = entry.getValue().get(entry.getValue().size() - 1);
			// Mira si el codi DIR3 existeix als òrganis obtinguts de la base de dades.
			Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
				filter(o -> o.getCodi().equals(entry.getKey())).
				findFirst();
			// Si el codi és a la base de dades i l'estat de la darrera versió és E (extingit) l'afegeix a la llista
			if (organGestor.isPresent() && "E".equals(lastNode.getEstat())) {
				extincions.add(lastNode);
			}
		}
		return extincions.toArray(NodeDir3[]::new);
	}

	@SafeVarargs
	private OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[] getCreacions(
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors,
		MultiValuedMap<NodeDir3, NodeDir3>... multiValuedMaps) {
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio> creacions = new ArrayList<>();
		Map<String, List<NodeDir3>> dir3SyncNodesMapSorted = getDir3SyncNodesMapSortedByVersionAsc(dir3SyncNodes);
		for (Map.Entry<String, List<NodeDir3>> entry : dir3SyncNodesMapSorted.entrySet()) {
			NodeDir3 lastNode = entry.getValue().get(entry.getValue().size() - 1);
			Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
				filter(o -> o.getCodi().equals(entry.getKey())).
				findFirst();
			// Si el codi NO és a la base de dades i l'estat de la darrera versió NO és E (extingit) i el codi DIR3 no
			// es troba a dins cap dels multiValuedMaps l'afegeix a la llista
			if (organGestor.isEmpty() && !"E".equals(lastNode.getEstat()) && !isCodiInAnyMap(entry.getKey(), multiValuedMaps)) {
				creacions.add(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio(toArbreItem(lastNode)));
			}
		}
		return creacions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[]::new);
	}

	private OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[] getModificacions(
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors) {
		// Retorna una llista dels nodes DIR3 provinents de la sincronització que compleixen els següents punts:
		// - El node DIR3 no te cap historicoUO.
		// - Existeix un òrgan gestor en estat vigent (V) a la base de dades amb el mateix codi.
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio> modificacions = new ArrayList<>();
		for (NodeDir3 dir3SyncNode : dir3SyncNodes) {
			if (dir3SyncNode.getHistoricosUO() == null || dir3SyncNode.getHistoricosUO().isEmpty()) {
				Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
					filter(o -> o.getCodi().equals(dir3SyncNode.getCodi())).
					findFirst();
				if (organGestor.isPresent()) {
					boolean nomChanged = !getOrganGestorNomFromDir3Node(dir3SyncNode).equals(organGestor.get().getNom());
					boolean nomEsChanged = !dir3SyncNode.getDenominacio().equals(organGestor.get().getNomEs());
					boolean codiPareChanged = !dir3SyncNode.getSuperior().equals(organGestor.get().getCodiPare());
					boolean estatChanged = !dir3SyncNode.getEstat().equals(organGestor.get().getEstat().name());
					if (nomChanged || nomEsChanged || codiPareChanged || estatChanged) {
						modificacions.add(
							new OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio(
								toArbreItem(organGestor.get()),
								toArbreItem(dir3SyncNode)));
					}
				}
			}
		}
		return modificacions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[]::new);
	}

	private Map<String, List<NodeDir3>> getDir3SyncNodesMapSortedByVersionAsc(List<NodeDir3> dir3SyncNodes) {
		// Crea un mapa dels nodes DIR3 a on cada clau del mapa és el codi DIR3 i el valor del mapa és una llista dels
		// nodes amb el mateix codi ordenats per versió creixent. Si un node te una versió null queda al principi de la
		// llista.
		Map<String, List<NodeDir3>> dir3SyncNodesMap = new HashMap<>();
		for (NodeDir3 dir3SyncNode : dir3SyncNodes) {
			if (!dir3SyncNodesMap.containsKey(dir3SyncNode.getCodi())) {
				dir3SyncNodesMap.put(dir3SyncNode.getCodi(), new ArrayList<>());
			}
			dir3SyncNodesMap.get(dir3SyncNode.getCodi()).add(dir3SyncNode);
		}
		for (Map.Entry<String, List<NodeDir3>> entry : dir3SyncNodesMap.entrySet()) {
			entry.getValue().sort((o1, o2) -> {
				if (o1.getVersio() == null) {
					// Fa que els nodes sense versió (valor null) es posin abans que els altres.
					return -1;
				}
				return o1.getVersio().compareTo(o2.getVersio());
			});
		}
		return dir3SyncNodesMap;
	}

	private List<NodeDir3> getHistoricosUo(
		NodeDir3 node,
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors) {
		// Retorna la llista dels historicosUO del node DIR3 emplenada de forma recursiva. Si no es troba algun dels
		// codis DIR3 als nodes provinents de la sincronizació intenta obtenir la informació de la base de dades.
		List<NodeDir3> historicosUo = new ArrayList<>();
		if (node.getHistoricosUO() != null && !node.getHistoricosUO().isEmpty()) {
			for (String historicoUo : node.getHistoricosUO()) {
				Optional<NodeDir3> historicoUoNode = dir3SyncNodes.stream().
					filter(n -> n.getCodi().equals(historicoUo)).
					findFirst();
				if (historicoUoNode.isPresent()) {
					if (historicoUo.equals(node.getCodi())) {
						log.warn("Detected organ division with transitioning to itself : " + historicoUo + ". Probably caused by error in DIR3");
					} else if (!historicoUoNode.get().equals(node)) {
						historicosUo.addAll(getHistoricosUo(
							historicoUoNode.get(),
							dir3SyncNodes,
							organsGestors));
					} else {
						historicosUo.add(node);
					}
				} else {
					Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
						filter(o -> o.getCodi().equals(historicoUo)).
						findFirst();
					if (organGestor.isPresent()) {
						historicosUo.add(toNodeDir3(organGestor.get()));
					} else {
						String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + node.getCodi()
							+ ") té l'estat (" + node.getEstat() + ") i l'històrica (" + historicoUo
							+ ") però no s'ha retornat la unitat orgánica (" + historicoUo
							+ ") en el resultat de la consulta del WS ni en la BBDD.";
						throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorMissatge);
					}
				}
			}
		} else {
			historicosUo.add(node);
		}
		return historicosUo;
	}

	private OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem toArbreItem(OrganGestorResourceEntity organGestor) {
		return new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem(
			organGestor.getCodi(),
			organGestor.getNomEs(),
			organGestor.getNom(),
			organGestor.getEstat());
	}

	private OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem toArbreItem(NodeDir3 dir3SyncNode) {
		return new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem(
			dir3SyncNode.getCodi(),
			dir3SyncNode.getDenominacio(),
			dir3SyncNode.getDenominacionCooficial(),
			dir3SyncNode.getEstat() != null ? OrganGestorEstatEnum.valueOf(dir3SyncNode.getEstat()) : null);
	}

	private OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem[] toArbreItems(Collection<NodeDir3> dir3SyncNodes) {
		return dir3SyncNodes.stream().map(this::toArbreItem).
			toArray(OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem[]::new);
	}

	private NodeDir3 toNodeDir3(OrganGestorResourceEntity entity) {
		NodeDir3 nodeDir3 = new NodeDir3();
		nodeDir3.setCodi(entity.getCodi());
		nodeDir3.setDenominacio(entity.getNomEs());
		nodeDir3.setDenominacionCooficial(entity.getNom());
		nodeDir3.setEstat(entity.getEstat().name());
		return nodeDir3;
	}

	private String getOrganGestorNomFromDir3Node(NodeDir3 node) {
		return !StringUtils.isEmpty(node.getDenominacionCooficial()) ?
			node.getDenominacionCooficial() :
			node.getDenominacio();
	}

	@SafeVarargs
	private boolean isCodiInAnyMap(
		String codi,
		MultiValuedMap<NodeDir3, NodeDir3>... maps) {
		for (MultiValuedMap<NodeDir3, NodeDir3> map : maps) {
			if (map.keySet().stream().anyMatch(n -> n.getCodi().equals(codi))) {
				return true;
			}
			// Els valors del mapa també han de quedar exclosos: per a divisionsMap la clau és
			// l'òrgan extint i els valors són els seus successors vigents; per a fusionsMap i
			// substitucionsMap la clau és l'òrgan vigent i els valors són els orígens extints.
			// En qualsevol dels dos casos el codi ja està classificat i no ha d'aparèixer també
			// com a creació.
			if (map.values().stream().anyMatch(n -> n.getCodi().equals(codi))) {
				return true;
			}
		}
		return false;
	}

	private void publishProgressEvent(
		SseEvent.SseEventName eventName,
		SseEvent.SseEventStatus status,
		int percent,
		String message) {
		progressEventService.publishEvent(
			SseEventService.SseQueue.PROGRESS,
			new SseEvent(
				eventName,
				percent,
				status,
				message));
	}

	private void persistirTransicions(
		MultiValuedMap<NodeDir3, NodeDir3> substitucionsMap,
		MultiValuedMap<NodeDir3, NodeDir3> fusionsMap,
		MultiValuedMap<NodeDir3, NodeDir3> divisionsMap) {

		List<String> totsElsCodis = new ArrayList<>();
		substitucionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		fusionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		divisionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		if (totsElsCodis.isEmpty()) {
			return;
		}
		Map<String, OrganGestorEntity> entitatsPerCodi = new HashMap<>();
		organGestorRepository.findByCodiIn(totsElsCodis).forEach(e -> entitatsPerCodi.put(e.getCodi(), e));
		List<OrganGestorEntity> aGuardar = new ArrayList<>();
		// Substitucions: la clau (vell al DTO) és el supervivent, el valor (nou al DTO) és l'extint.
		substitucionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getValue().getCodi(), entry.getKey().getCodi(), aGuardar));
		// Fusions: la clau és el supervivent (nou al DTO), els valors són els extints (vells al DTO).
		fusionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getValue().getCodi(), entry.getKey().getCodi(), aGuardar));
		// Divisions: la clau és l'extint (vell al DTO), els valors són els supervivents (nous al DTO).
		divisionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getKey().getCodi(), entry.getValue().getCodi(), aGuardar));
		if (!aGuardar.isEmpty()) {
			organGestorRepository.saveAll(aGuardar);
		}
	}

	private void afegeixTransicio(Map<String, OrganGestorEntity> entitatsPerCodi, String codiOrigen, String codiDesti, List<OrganGestorEntity> aGuardar) {
		var origen = entitatsPerCodi.get(codiOrigen);
		var desti = entitatsPerCodi.get(codiDesti);
		if (origen != null && desti != null) {
			origen.addNou(desti);
			aGuardar.add(origen);
		}
	}

}
