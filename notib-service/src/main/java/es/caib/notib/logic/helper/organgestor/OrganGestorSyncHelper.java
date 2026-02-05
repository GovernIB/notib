package es.caib.notib.logic.helper.organgestor;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.logic.intf.dto.organisme.UnitatOrganitzativaDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorSyncHelper {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private OrganGestorResourceRepository organGestorResourceRepository;

	@Transactional(readOnly = true)
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) {

		var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false);
		var isFirstSincronization = entitat.getDataSincronitzacio() == null;
		List<UnitatOrganitzativaDto> unitatsVigents;
		if (isFirstSincronization) {
			return predictFirstSynchronization(entitat);
		}
		try {
			// Obtenir lista de canvis del servei web
			var unitatsWS = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
			// Obtenir els òrgans vigents a la BBDD
			List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganGestorEstatEnum.V);
			// Obtenir unitats actualment vigents en BBDD, però marcades com a obsoletes en la sincronització
			Map<String, List<NodeDir3>> mapVersionsUnitats = getMapVersionsUnitats(unitatsWS);
			var unitatsVigentObsoleteDto = getObsoletesFromWS(entitat, unitatsWS, mapVersionsUnitats, organsVigents);
			List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();
			// Distinció entre divisió i (substitució o fusió)
			MultiValuedMap splitMap = new ArrayListValuedHashMap();
			MultiValuedMap mergeOrSubstMap = new ArrayListValuedHashMap();
			int transicionsVigents;
			for (var vigentObsolete : unitatsVigentObsoleteDto) {
				// Comprovam que no estigui extingida
				transicionsVigents = 0;
				if (!vigentObsolete.getLastHistoricosUnitats().isEmpty()) {
					for (var hist: vigentObsolete.getLastHistoricosUnitats()) {
						if (OrganGestorEstatEnum.V.name().equals(hist.getEstat())) {
							transicionsVigents++;
						}
					}
				}
				// En cas de no estar extingida comprovam el tipus de operació
				if (transicionsVigents > 1) {
					for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
						splitMap.put(vigentObsolete, hist);
					}
				} else if (transicionsVigents == 1) {
					// check if the map already contains key with this codi
					var mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
					UnitatOrganitzativaDto keyWithTheSameCodi = null;
					Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
					for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
						if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
							keyWithTheSameCodi = mergeOrSubstKeyMap;
						}
					}
					// if it contains already key with the same codi, assign found key
					if (keyWithTheSameCodi != null) {
						mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
					} else {
						mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
					}
				} else if (transicionsVigents == 0) {
					unitatsExtingides.add(vigentObsolete);
				}
			}

			// Distinció entre substitució i fusió
			Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
			MultiValuedMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto> mergeMap = new ArrayListValuedHashMap<>();
			MultiValuedMap substMap = new ArrayListValuedHashMap();
			List<UnitatOrganitzativaDto> values;
			for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
				values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);
				if (values.size() <= 1) {
					substMap.put(mergeOrSubstKey, values.get(0));
					continue;
				}
				for (var value : values) {
					if (isAlreadyAddedToMap(mergeMap, mergeOrSubstKey, value)) {
						//normally this shoudn't duplicate, it is added to deal with the result of call to WS DIR3 PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
						log.info("Detected duplication of organs in prediction of fusion. Unitat" + value.getCodi() + "already added to fusion into " + mergeOrSubstKey.getCodi() + ". Probably caused by error in DIR3");
						continue;
					}
					mergeMap.put(mergeOrSubstKey, value);
				}
			}
			// Obtenir llistat d'unitats que ara estan vigents en BBDD, i després de la sincronització continuen vigents, però amb les propietats canviades
			// ====================  CANVIS EN ATRIBUTS ===================
			unitatsVigents = getVigentsFromWebService(entitat, unitatsWS, organsVigents);

			// Obtenir el llistat d'unitats que son totalment noves (no existeixen en BBDD): Creació
			// ====================  NOUS ===================
			List<UnitatOrganitzativaDto> unitatsNew = getNewFromWS(entitat, mapVersionsUnitats, splitMap, substMap, mergeMap);

			afegirDenominacioSiNull(entitat, mergeMap.keySet());

			return PrediccioSincronitzacio.builder()
				.unitatsVigents(unitatsVigents)
				.unitatsNew(unitatsNew)
				.unitatsExtingides(unitatsExtingides)
				.splitMap(splitMap)
				.substMap(substMap)
				.mergeMap(mergeMap).build();

		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioCodi.UNITATS.name(), "No ha estat possible obtenir la predicció de canvis de unitats organitzatives", ex);
		}
	}

	private PrediccioSincronitzacio predictFirstSynchronization(EntitatEntity entitat) throws SistemaExternException {

		var unitatsVigentsWS = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
		var vigents = conversioTipusHelper.convertirList(unitatsVigentsWS, UnitatOrganitzativaDto.class);
		List<String> codis = new ArrayList<>();
		List<UnitatOrganitzativaDto> noves = new ArrayList<>();
		OrganGestorEntity o;
		for (UnitatOrganitzativaDto u : vigents) {
			o = organGestorRepository.findByEntitatAndCodi(entitat, u.getCodi());
			if (o == null) {
				noves.add(u);
				continue;
			}
			codis.add(u.getCodi());
		}
		var extingides = calcularExtingides(entitat.getCodi(), codis, true);
		var ex = conversioTipusHelper.convertirList(extingides, UnitatOrganitzativaDto.class);
		var n = conversioTipusHelper.convertirList(noves, UnitatOrganitzativaDto.class);
		return PrediccioSincronitzacio.builder().isFirstSincronization(true).unitatsVigents(vigents).unitatsNew(n).unitatsExtingides(ex).build();
	}

	private List<OrganGestorEntity> calcularExtingides(String entitatCodi, List<String> codis, boolean firstSincronization) {

		List<OrganGestorEntity> extingides = new ArrayList<>();
		var organsAExtingir = firstSincronization ? organGestorRepository.findCodiActiusByEntitat(entitatCodi) : organGestorRepository.findCodiInactiusByEntitat(entitatCodi);
		if (organsAExtingir.isEmpty()) {
			return extingides;
		}
		for (var codi: codis) {
			organsAExtingir.remove(codi);
		}
		if (organsAExtingir.isEmpty())
			return extingides;

		return getExtingits(entitatCodi, organsAExtingir);
	}

	private List<OrganGestorEntity> getExtingits(String entitatCodi, List<String> organsAExtingir) {
		List<OrganGestorEntity> extingides = new ArrayList<>();
		var maxInSize = 1000;
		var nParts = (organsAExtingir.size() / maxInSize) + 1;
		var inici = 0;
		var fi = organsAExtingir.size() - maxInSize > 0 ? maxInSize : organsAExtingir.size();
		List<String>  subList;
		for (var foo= 0; foo < nParts; foo++) {
			subList = organsAExtingir.subList(inici, fi);
			if (!subList.isEmpty()) {
				extingides.addAll(organGestorRepository.findByEntitatCodiAndCodiIn(entitatCodi, subList));
			}
			inici = fi + 1 ;
			fi = organsAExtingir.size() - inici > maxInSize ? maxInSize : organsAExtingir.size();
		}
		return extingides;
	}

	private Map<String, List<NodeDir3>> getMapVersionsUnitats(List<NodeDir3> unitatsWs) {
		Map<String, List<NodeDir3>> unitats = new HashMap<>();
		for (NodeDir3 unitat: unitatsWs) {
			if (!unitats.containsKey(unitat.getCodi())) {
				unitats.put(unitat.getCodi(), new ArrayList<NodeDir3>());
			}
			unitats.get(unitat.getCodi()).add(unitat);
		}
		for (Map.Entry<String, List<NodeDir3>> entry: unitats.entrySet()) {
			Collections.sort(entry.getValue(), new Comparator<NodeDir3>() {
				@Override
				public int compare(NodeDir3 o1, NodeDir3 o2) {
					if (o1.getVersio() == null) {
						return -1;
					}
					return o1.getVersio().compareTo(o2.getVersio());
				}
			});
		}
		return unitats;
	}

	private List<UnitatOrganitzativaDto> getObsoletesFromWS(EntitatEntity entitat, List<NodeDir3> unitatsWS, Map<String,List<NodeDir3>> unitats, List<OrganGestorEntity> organsVigents) {

		List<UnitatOrganitzativaDto> extingides = new ArrayList<>();
		List<NodeDir3> nodes;
		NodeDir3 node;
		for (var entry : unitats.entrySet()){
			nodes = entry.getValue();
			node = nodes.get(nodes.size()-1);
			if (organGestorRepository.findByEntitatAndCodi(entitat, entry.getKey()) != null && "E".equals(node.getEstat())) {
				node.setLastHistoricosUnitats(getLastHistoricos(entitat, node, unitatsWS));
				extingides.add(conversioTipusHelper.convertir(node, UnitatOrganitzativaDto.class));
			}
		}
		return extingides;

	}

	private boolean isAlreadyAddedToMap(MultiValuedMap mergeMap, UnitatOrganitzativaDto key, UnitatOrganitzativaDto value) {

		List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeMap.get(key);
		if (values == null) {
			return false;
		}
		boolean contains = false;
		for (UnitatOrganitzativaDto unitat : values) {
			if (unitat.getCodi().equals(value.getCodi())) {
				contains = true;
			}
		}
		return contains;
	}

	// Retorna la/les unitat/s a la que un organ obsolet ha fet la transició
	// Inici de mètode recursiu
	private List<NodeDir3> getLastHistoricos(EntitatEntity entitat, NodeDir3 unitat, List<NodeDir3> unitatsFromWebService){

		List<NodeDir3> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(entitat, unitat, unitatsFromWebService, lastHistorcos);
		return lastHistorcos;
	}

	private void getLastHistoricosRecursive(EntitatEntity entitat, NodeDir3 unitat, List<NodeDir3> unitatsFromWebService, List<NodeDir3> lastHistorics) {

		log.info("Coloca historics recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");

		if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
			lastHistorics.add(unitat);
			return;
		}
		for (String historicCodi : unitat.getHistoricosUO()) {
			NodeDir3 unitatFromCodi = getUnitatFromCodi(historicCodi, unitatsFromWebService);
			if (unitatFromCodi == null) {
				// Looks for historico in database
				OrganGestorEntity entity = organGestorRepository.findByEntitatAndCodi(entitat, historicCodi);
				if (entity != null) {
					NodeDir3 uo = conversioTipusHelper.convertir(entity, NodeDir3.class);
					lastHistorics.add(uo);
				} else {
					String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + unitat.getCodi()
						+ ") té l'estat (" + unitat.getEstat() + ") i l'històrica (" + historicCodi
						+ ") però no s'ha retornat la unitat orgánica (" + historicCodi
						+ ") en el resultat de la consulta del WS ni en la BBDD.";
					throw new SistemaExternException(IntegracioCodi.UNITATS.name(), errorMissatge);
				}
			} else if (historicCodi.equals(unitat.getCodi())) {
				// EXAMPLE:
				//A04032359
				//-A04032359
				//-A04068486
				// if it is transitioning to itself don't add it as last historic
				//this probably shoudn't happen, it is added to deal with the result of call to WS made in PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
				log.info("Detected organ division with transitioning to itself : " + historicCodi + ". Probably caused by error in DIR3");
			} else {
				if (!unitatFromCodi.equals(unitat)) {
					getLastHistoricosRecursive(entitat, unitatFromCodi, unitatsFromWebService, lastHistorics);
				} else {
					lastHistorics.add(unitat);
				}
			}
		}

	}

	private NodeDir3 getUnitatFromCodi(String codi, List<NodeDir3> allUnitats){

		for (var unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				return unitatWS;
			}
		}
		return null;
	}

	// Obtenir unitats que no fan cap transició a cap altre unitat, però a la que se'ls canvia alguna propietat
	private List<UnitatOrganitzativaDto> getVigentsFromWebService(EntitatEntity entitat, List<NodeDir3> unitatsWS, List<OrganGestorEntity> organsVigents){

		// list of vigent unitats from webservice
		List<NodeDir3> unitatsVigentsWithChangedAttributes = new ArrayList<>();
		for (var unitatWS : unitatsWS) {
			unitatsVigentsWithChangedAttributes.add(unitatWS);
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		UnitatOrganitzativaDto unitatOrganitzativaDto;
		OrganGestorEntity org;
		for(var vigent : unitatsVigentsWithChangedAttributes){
			unitatOrganitzativaDto = conversioTipusHelper.convertir(vigent, UnitatOrganitzativaDto.class);
			org = organGestorRepository.findByEntitatAndCodi(entitat, unitatOrganitzativaDto.getCodi());
			if (org == null) {
				continue;
			}
			unitatOrganitzativaDto.setOldDenominacio(org.getNom());
			unitatsVigentsWithChangedAttributesDto.add(unitatOrganitzativaDto);
		}
		return unitatsVigentsWithChangedAttributesDto;
	}

	// Obtenir unitats organitzatives noves (No provenen de cap transició d'una altre unitat)
	private List<UnitatOrganitzativaDto> getNewFromWS(EntitatEntity entitat, Map<String, List<NodeDir3>> unitats, MultiValuedMap splitMap,  MultiValuedMap substMap, MultiValuedMap mergeMap){

//		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<>();
		List<NodeDir3> nodes;
		NodeDir3 node;
		UnitatOrganitzativaDto unitat;
		for (Map.Entry<String, List<NodeDir3>> entry : unitats.entrySet()){
			nodes = entry.getValue();
			node = nodes.get(nodes.size()-1);
			if (organGestorRepository.findByEntitatAndCodi(entitat, entry.getKey()) != null || "E".equals(node.getEstat())) {
				continue;
			}
			unitat = conversioTipusHelper.convertir(node, UnitatOrganitzativaDto.class);
			if(contains(entry.getKey(), splitMap) || contains(entry.getKey(), substMap) || contains(entry.getKey(), mergeMap)) {
				continue;
			}
			newUnitatsDto.add(unitat);
		}
		return newUnitatsDto;
	}

	private boolean contains(String key, MultiValuedMap map) {

		Set<UnitatOrganitzativaDto> keys = map.keySet();
		for (UnitatOrganitzativaDto u : keys) {
			if (key.equals(u.getCodi())) {
				return true;
			}
		}
		return false;
	}


	private void afegirDenominacioSiNull(EntitatEntity entitat, Set<UnitatOrganitzativaDto> organs) {

		for (var key : organs) {
			if (!Strings.isNullOrEmpty(key.getDenominacio()) || !Strings.isNullOrEmpty(key.getDenominacioCooficial())) {
				continue;
			}
			var organ = organGestorRepository.findByEntitatAndCodi(entitat, key.getCodi());
			if (organ != null) {
				key.setDenominacio(organ.getNom());
				key.setDenominacioCooficial(organ.getNomEs());
				continue;
			}
			var unitat = pluginHelper.unitatOrganitzativaFindByCodi(entitat.getCodi(), key.getCodi(), null, null);
			key.setDenominacio(unitat.getDenominacio());
			key.setDenominacioCooficial(unitat.getDenominacionCooficial());
		}
	}


}
