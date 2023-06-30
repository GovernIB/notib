package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.organisme.TipusTransicioEnumDto;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.AvisEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.AvisRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class OrganGestorHelper {

	@Autowired
	private PermisosService permisosService;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private AvisRepository avisRepository;
	@Autowired
	private ProcSerOrganRepository procSerOrganRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MessageHelper messageHelper;

	public static final String ORGAN_NO_SYNC = "Hi ha canvis pendents de sincronitzar a l'organigrama";


	public List<String> findCodiOrgansGestorsWithPermis(Authentication auth, EntitatEntity entitat, PermisEnum permis) {

		var organs = permisosService.getOrgansAmbPermis(entitat.getId(), auth.getName(), permis);
		List<String> codis = new ArrayList<>();
		for (var organ : organs) {
			codis.add(organ.getCodi());
		}
		return codis;
	}

	public List<OrganGestorEntity> findOrganismesEntitatAmbPermis(EntitatEntity entitat, Permission[] permisos) {

		List<Long> objectsIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
		return !objectsIds.isEmpty() ? organGestorRepository.findByEntitatAndIds(entitat, objectsIds) : new ArrayList<>();
	}

	public void consultaCanvisOrganigrama(EntitatEntity entitat) {

		var ara = new Date();
		var calendar = Calendar.getInstance();
		calendar.setTime(ara);
		calendar.add(Calendar.YEAR, 1);
		var unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
		var avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		if (unitatsWs == null || unitatsWs.isEmpty()) {
			return;
		}
		var msg = "Realitzi el procés de sincronització d'òrgans gestors per a disposar dels òrgans gestors actuals.";
		var avis = AvisEntity.getBuilder(ORGAN_NO_SYNC, msg, ara, calendar.getTime(), AvisNivellEnumDto.ERROR, true, entitat.getId()).build();
		avisRepository.save(avis);
	}

	@Transactional
	public void sincronitzarOrgans(Long entitatId, List<NodeDir3> unitatsWs, List<OrganGestorEntity> obsoleteUnitats, List<OrganGestorEntity> organsDividits,
								   List<OrganGestorEntity> organsFusionats, List<OrganGestorEntity> organsSubstituits, ProgresActualitzacioDto progres) {

		var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
		var nombreUnitatsTotal = unitatsWs.size();
		var nombreUnitatsProcessades = 0;
		// Agafa totes les unitats del WS i les guarda a BBDD. Si la unitat no existeix la crea, i si existeix la sobreescriu.
		var prefix = "[SYNC-ORGANS] ";
		log.debug(prefix + "Sincronitzant òrgans gestors");
		for (var unitatWS: unitatsWs) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.unitat", new Object[] {unitatWS.getCodi() + " - " + unitatWS.getDenominacio()}));
			sincronizarUnitat(unitatWS, entitat);
			progres.setProgres(2 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
		}
		progres.setProgres(12);
		// Històrics
		log.debug(prefix + "Sincronitzant històric unitats");
		nombreUnitatsProcessades = 0;
		for (var unitatWS : unitatsWs) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.historic", new Object[] {unitatWS.getCodi() + " - " + unitatWS.getDenominacio()}));
			var unitat = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodi());
			sincronizarHistoricsUnitat(unitat, unitatWS, entitat);
			progres.setProgres(12 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
			if (unitat != null && !OrganGestorEstatEnum.V.equals(unitat.getEstat())) {
				unitat.setNoVigent(true);
			}
		}
		progres.setProgres(22);
		obsoleteUnitats.addAll(organGestorRepository.findByEntitatNoVigent(entitat));
		// Definint tipus de transició
		log.debug(prefix + "Sincronitzant unitats obsoletes");
		nombreUnitatsProcessades = 0;
		nombreUnitatsTotal = obsoleteUnitats.size();
		for (var obsoleteUnitat : obsoleteUnitats) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.definir.transicio", new Object[] {obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom()}));
			if (obsoleteUnitat.getNous() == null || obsoleteUnitat.getNous().isEmpty()) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.EXTINCIO);
			} else if (obsoleteUnitat.getNous().size() > 1) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.DIVISIO);
				organsDividits.add(obsoleteUnitat);
				if (obsoleteUnitat.getNous().contains(obsoleteUnitat)) {
					obsoleteUnitat.setEstat(OrganGestorEstatEnum.V);
				}
			} else if (obsoleteUnitat.getNous().size() == 1) {
				if (obsoleteUnitat.getNous().get(0).getAntics().size() > 1) {
					obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.FUSIO);
					organsFusionats.add(obsoleteUnitat);
				} else if (obsoleteUnitat.getNous().get(0).getAntics().size() == 1) {
					obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);
					organsSubstituits.add(obsoleteUnitat);
				}
			}
			List<OrganGestorEntity> nous = obsoleteUnitat.getNous();
			if (nous != null && !nous.contains(obsoleteUnitat)) {
				log.debug(prefix + "Unitat extingida " + obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom());
				obsoleteUnitat.setEstat(OrganGestorEstatEnum.E);
			}
			progres.setProgres(22 + (nombreUnitatsProcessades++ * 5 / nombreUnitatsTotal));
		}
		var avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
		log.debug(prefix + "Esborrant avisos ");
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		progres.setProgres(27);
		var ara = new Date();
		log.debug(prefix + "Data de sincronització " + ara);
		// Si és la primera sincronització
		if (entitat.getDataSincronitzacio() == null) {
			entitat.setDataSincronitzacio(ara);
		}
		entitat.setDataActualitzacio(ara);
	}

	private OrganGestorEntity sincronizarUnitat(NodeDir3 unitatWS, EntitatEntity entitat) {

		if (unitatWS == null) {
			return null;
		}
		var prefix = "[SYNC-ORGANS] ";
		// checks if unitat already exists in database
		var unitat = organGestorRepository.findByCodi(unitatWS.getCodi());
		// if not it creates a new one
		var nom = !Strings.isNullOrEmpty(unitatWS.getDenominacionCooficial()) ? unitatWS.getDenominacionCooficial() : unitatWS.getDenominacio();
		if (unitat != null) {
			unitat.update(nom, unitatWS.getDenominacio(), unitatWS.getEstat(), unitatWS.getSuperior());
			updateLlibreAndOficina(unitat, entitat.getDir3Codi());
			log.debug(prefix + "guardant nova unitat amb codi " + unitat.getCodi() + " - " + unitat.getNom());
			organGestorRepository.save(unitat);
			return unitat;
		}
		// Venen les unitats ordenades, primer el pare i després els fills?
		unitat = OrganGestorEntity.builder().codi(unitatWS.getCodi()).entitat(entitat).nom(nom).nomEs(unitatWS.getDenominacio())
				.codiPare(unitatWS.getSuperior()).estat(unitatWS.getEstat()).build();
		updateLlibreAndOficina(unitat, entitat.getDir3Codi());
		log.debug(prefix + "guardant nova unitat amb codi " + unitat.getCodi() + " - " + unitat.getNom());
		organGestorRepository.save(unitat);
		return unitat;
	}

	private void updateLlibreAndOficina(OrganGestorEntity organ, String entitatDir3Codi) {

		LlibreDto llibre = pluginHelper.llistarLlibreOrganisme(entitatDir3Codi, organ.getCodi());
		if (llibre != null) {
			organ.updateLlibre(llibre.getCodi(), llibre.getNomLlarg());
		}
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Actualització d'oficines SIR per l'entitat " + entitatDir3Codi,
				IntegracioAccioTipusEnumDto.PROCESSAR);
		var arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitatDir3Codi);
		processarOficinaOrgan(info, arbreUnitats, organ);
	}

	private void sincronizarHistoricsUnitat(OrganGestorEntity unitat, NodeDir3 unidadWS, EntitatEntity entitat) {

		if (unidadWS.getHistoricosUO() == null || unidadWS.getHistoricosUO().isEmpty()) {
			return;
		}
		OrganGestorEntity nova;
		for (var historicoCodi : unidadWS.getHistoricosUO()) {
			nova = organGestorRepository.findByEntitatAndCodi(entitat, historicoCodi);
			if (unitat.getNous() != null && isAlreadyAddedToList(unitat.getNous(), nova)) {
				//normally this shoudn't duplicate, it is added to deal with the result of call to WS DIR3 PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
				log.info("Detected duplication of transtition in DB. Unitat" + unitat.getCodi() + "already transitioned into " + nova.getCodi() + ". Probably caused by error in DIR3");
				continue;
			}
			unitat.addNou(nova);
			nova.addAntic(unitat);
		}
	}

	private boolean isAlreadyAddedToList(List<OrganGestorEntity> organs, OrganGestorEntity organ) {

		boolean contains = false;
		for (OrganGestorEntity organGestorEntity : organs) {
			if (organGestorEntity.getId().equals(organ.getId())) {
				contains = true;
			}
		}
		return contains;
	}

	@Transactional
	public void deleteExtingitsNoUtilitzats(List<OrganGestorEntity> obsoleteUnitats, ProgresActualitzacioDto progres) {

		// Eliminar organs no vigents no utilitzats??
		var nombreUnitatsTotal = obsoleteUnitats.size();
		var nombreUnitatsProcessades = 0;
		for (OrganGestorEntity organObsolet : obsoleteUnitats) {
			progres.setProgres(81 + (nombreUnitatsProcessades++ * 18) / nombreUnitatsTotal);
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.check", new Object[]{organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			var nombreProcediments = procSerOrganRepository.countByOrganGestor(organObsolet);
			if (nombreProcediments > 0) {
				continue;
			}
			var nombreExpedients = notificacioRepository.countByOrganGestor(organObsolet);
			if (nombreExpedients > 0) {
				continue;
			}
			try {
				permisosHelper.eliminarPermisosOrgan(organObsolet);
				organGestorRepository.delete(organObsolet);
				progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.borrat", new Object[]{organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			} catch (Exception ex) {
				log.error("No ha estat possible esborrar l'òrgan gestor.", ex);
				progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.error", new Object[]{organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processarOficinaOrgan(IntegracioInfo info, Map<String, OrganismeDto> arbreUnitats, OrganGestorEntity organ) {

		List<OficinaDto> oficines;
		try {
			log.info("OFISYNC - Obtenint oficines de l'òrgan {} - {}", organ.getCodi(), organ.getNom());
			oficines = cacheHelper.getOficinesSIRUnitat(arbreUnitats, organ.getCodi());
			log.info("OFISYNC - Obtingudes {} oficines", oficines == null ? 0 : oficines.size());
		} catch (Exception ex) {
			String msg = "S'ha produit un error obtenint les oficines de l'òrgan " + organ.getCodi() + " - " + organ.getNom();
			log.error(msg);
			info.addParam(organ.getCodi(), msg);
			return;
		}
		if (oficines == null || oficines.isEmpty()) {
			info.addParam(organ.getCodi(), "No s'han obtingut oficines oficines");
			return;
		}
		if (Strings.isNullOrEmpty(organ.getOficina()) || !Strings.isNullOrEmpty(organ.getOficina()) && !oficines.toString().contains(organ.getOficina())) {
			log.info("OFISYNC - Actualitzant oficina. Antiga: {} - {} , Nova: {} - {}", new Object[] {organ.getOficina(), organ.getOficinaNom(), oficines.get(0).getCodi(), oficines.get(0).getNom()});
			info.addParam(organ.getCodi(), "Actualitzant la oficina. Antiga: " + organ.getOficina() + " - Nova: " + oficines.get(0).getCodi());
			actualitzarOficinaOrgan(organ.getCodi(), oficines.get(0));
			log.info("OFISYNC - Oficina actualitzada");
		} else {
			log.info("OFISYNC - L'oficina no s'ha d'actualitzar");
		}
	}

	public void actualitzarOficinaOrgan(String organCodi, OficinaDto oficina) {

		var organ = organGestorRepository.findByCodi(organCodi);
		organ.setOficina(oficina.getCodi());
		organ.setOficinaNom(oficina.getNom());
		organGestorRepository.save(organ);
	}

	public void setServicesForSynctest(PluginHelper pluginHelper) {
		this.pluginHelper = pluginHelper;
	}
}
