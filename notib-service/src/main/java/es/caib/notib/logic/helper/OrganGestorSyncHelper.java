package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;

/**
 * Helper per a convertir entities a dto
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorSyncHelper {

	private final PluginHelper pluginHelper;
	private final OrganGestorResourceRepository organGestorResourceRepository;

	public OrganGestorDir3Sync sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular) {
		Date dataActualitzacio = entitat.getDataActualitzacio() != null ? Date.from(
			entitat.getDataActualitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		Date dataSincronitzacio = entitat.getDataSincronitzacio() != null ? Date.from(
			entitat.getDataSincronitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		List<NodeDir3> dir3SyncNodes = pluginHelper.unitatsOrganitzativesFindByPare(
			entitat.getCodi(),
			entitat.getDir3Codi(),
			dataActualitzacio,
			dataSincronitzacio);
		List<OrganGestorResourceEntity> organsGestors = organGestorResourceRepository.findByEntitat(entitat);
		/*List<OrganGestorResourceEntity> organsGestorsVigents = organGestorResourceRepository.findByEntitatAndEstat(
			entitat,
			OrganGestorEstatEnum.V);*/

		NodeDir3[] extincionsDarreraVersio = getExtincionsDarreraVersio(dir3SyncNodes, organsGestors);
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio> extincions = new ArrayList<>();
		MultiValuedMap<NodeDir3, NodeDir3> divisionsMap = new ArrayListValuedHashMap<>();
		MultiValuedMap<NodeDir3, NodeDir3> fusionsOSubstitucionsMap = new ArrayListValuedHashMap<>();
		for (NodeDir3 extincioDarreraVersio: extincionsDarreraVersio) {
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
				for (NodeDir3 historicoUo: historicosUo) {
					divisionsMap.put(extincioDarreraVersio, historicoUo);
				}
			}
		}
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
				toArbreItems(fusionsMap.get(key)))).
			toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[]::new);
		return new OrganGestorDir3Sync(
			null,
			getCreacions(dir3SyncNodes, organsGestors, substitucionsMap, fusionsMap, divisionsMap),
			getModificacions(dir3SyncNodes, organsGestors),
			substitucions,
			extincions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio[]::new),
			fusions,
			divisions,
			simular);
	}

	/*private OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[] getCreacions(
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestorsVigents) {
		// Retorna una llista dels nodes DIR3 provinents de la sincronització que compleixen els següents punts:
		// - El node DIR3 no te cap historicoUO.
		// - NO existeix un òrgan gestor en estat vigent (V) a la base de dades amb el mateix codi.
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio> creacions = new ArrayList<>();
		for (NodeDir3 dir3SyncNode: dir3SyncNodes) {
			if (dir3SyncNode.getHistoricosUO() == null || dir3SyncNode.getHistoricosUO().isEmpty()) {
				Optional<OrganGestorResourceEntity> organGestor = organsGestorsVigents.stream().
					filter(o -> o.getCodi().equals(dir3SyncNode.getCodi())).
					findFirst();
				if (organGestor.isEmpty()) {
					creacions.add(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio(
						toArbreItem(dir3SyncNode)));
				}
			}
		}
		return creacions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[]::new);
	}

	private OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] getSubstitucions(
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestorsVigents) {
		// Retorna una llista dels nodes DIR3 provinents de la sincronització que compleixen els següents punts:
		// - El node DIR3 te un únic historicoUO.
		// - Existeix un òrgan gestor en estat vigent (V) a la base de dades amb el mateix codi.
		// - El valor de historicoUO no es repeteix a altres nodes DIR3.
		List<OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio> substitucions = new ArrayList<>();
		for (NodeDir3 dir3SyncNode: dir3SyncNodes) {
			if (dir3SyncNode.getHistoricosUO() != null && dir3SyncNode.getHistoricosUO().size() == 1) {
				Optional<OrganGestorResourceEntity> organGestor = organsGestorsVigents.stream().
					filter(o -> o.getCodi().equals(dir3SyncNode.getCodi())).
					findFirst();
				if (organGestor.isPresent()) {
					String historicoUo = dir3SyncNode.getHistoricosUO().get(0);
					Optional<NodeDir3> repetit = dir3SyncNodes.stream().
						filter(n -> {
							if (dir3SyncNode.getHistoricosUO() != null && !dir3SyncNode.getHistoricosUO().isEmpty()) {
								return dir3SyncNode.getHistoricosUO().contains(historicoUo);
							} else {
								return false;
							}
						}).findFirst();
					if (repetit.isEmpty()) {
						Optional<NodeDir3> substituit = dir3SyncNodes.stream().
							filter(n -> n.getCodi().equals(historicoUo)).
							findFirst();
						if (substituit.isPresent()) {
							substitucions.add(
								new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio(
									toArbreItem(organGestor.get()),
									toArbreItem(substituit.get())));
						} else {
							// WARN el codi DIR3 de l'historicoUO no s'ha trobat als nodes DIR3 provinents de la
							// sincronització
						}
					}
				}
			}
		}
		return substitucions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[]::new);
	}*/

	private NodeDir3[] getExtincionsDarreraVersio(
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors) {
		// Obté un mapa dels nodes DIR3 a on cada element és una llista del mateix codi DIR3 ordenada per versió
		Map<String, List<NodeDir3>> dir3SyncNodesMapSorted = getDir3SyncNodesMapSortedByVersionAsc(dir3SyncNodes);
		List<NodeDir3> extincions = new ArrayList<>();
		for (Map.Entry<String, List<NodeDir3>> entry: dir3SyncNodesMapSorted.entrySet()) {
			// Obté la darrera versió de cada codi DIR3 diferent de la llista retornada per la sincronització
			NodeDir3 lastNode = entry.getValue().get(entry.getValue().size() - 1);
			Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
				filter(o -> o.getCodi().equals(entry.getKey())).
				findFirst();
			// Si el codi és a la base de dades i l'estat de la darrera versió és E (extingit) l'afegeix a la llista
			if (organGestor.isPresent() && "E".equals(lastNode.getEstat())) {
				//lastDir3SyncNode.setLastHistoricosUnitats(getLastHistoricos(entitat, node, unitatsWS));
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
		for (Map.Entry<String, List<NodeDir3>> entry: dir3SyncNodesMapSorted.entrySet()) {
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
		for (NodeDir3 dir3SyncNode: dir3SyncNodes) {
			if (dir3SyncNode.getHistoricosUO() == null || dir3SyncNode.getHistoricosUO().isEmpty()) {
				Optional<OrganGestorResourceEntity> organGestor = organsGestors.stream().
					filter(o -> o.getCodi().equals(dir3SyncNode.getCodi())).
					findFirst();
				organGestor.ifPresent(o -> modificacions.add(
					new OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio(
						toArbreItem(o),
						toArbreItem(dir3SyncNode))));
			}
		}
		return modificacions.toArray(OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[]::new);
	}

	private Map<String, List<NodeDir3>> getDir3SyncNodesMapSortedByVersionAsc(List<NodeDir3> dir3SyncNodes) {
		// Crea un mapa dels nodes DIR3 a on cada clau del mapa és el codi DIR3 i el valor del mapa és una llista dels
		// nodes amb el mateix codi ordenats per versió creixent. Si un node te una versió null queda al principi de la
		// llista.
		Map<String, List<NodeDir3>> dir3SyncNodesMap = new HashMap<>();
		for (NodeDir3 dir3SyncNode: dir3SyncNodes) {
			if (!dir3SyncNodesMap.containsKey(dir3SyncNode.getCodi())) {
				dir3SyncNodesMap.put(dir3SyncNode.getCodi(), new ArrayList<>());
			}
			dir3SyncNodesMap.get(dir3SyncNode.getCodi()).add(dir3SyncNode);
		}
		for (Map.Entry<String, List<NodeDir3>> entry: dir3SyncNodesMap.entrySet()) {
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
		// codis DIR3 als nodes provinents de la sincronizació obté la informació de la base de dades.
		List<NodeDir3> historicosUo = new ArrayList<>();
		if (node.getHistoricosUO() != null && !node.getHistoricosUO().isEmpty()) {
			for (String historicoUo: node.getHistoricosUO()) {
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
			toArray(OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem[]:: new);
	}

	private NodeDir3 toNodeDir3(OrganGestorResourceEntity entity) {
		NodeDir3 nodeDir3 = new NodeDir3();
		nodeDir3.setCodi(entity.getCodi());
		nodeDir3.setDenominacio(entity.getNomEs());
		nodeDir3.setDenominacionCooficial(entity.getNom());
		nodeDir3.setEstat(entity.getEstat().name());
		return nodeDir3;
	}

	@SafeVarargs
	private boolean isCodiInAnyMap(
		String codi,
		MultiValuedMap<NodeDir3, NodeDir3>... maps) {
		for (MultiValuedMap<NodeDir3, NodeDir3> map: maps) {
			if (map.keySet().stream().anyMatch(n -> n.getCodi().equals(codi))) {
				return true;
			}
		}
		return false;
	}

}
