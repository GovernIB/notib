package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.organisme.TipusTransicioEnumDto;
import es.caib.notib.logic.cacheable.PermisosCacheable;
import es.caib.notib.persist.entity.AvisEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.AvisRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class OrganGestorHelper {
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private OrganigramaHelper organigramaHelper;
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
	@Autowired
	private PermisosCacheable permisosCacheable;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MessageHelper messageHelper;

	public static final String ORGAN_NO_SYNC = "Hi ha canvis pendents de sincronitzar a l'organigrama";

	@Cacheable(value = "organsEntitiesPermis", key="#entitat.getId().toString().concat('-').concat(#usuariCodi).concat('-').concat(#permisos[0].getPattern())")
	public List<OrganGestorEntity> getOrgansGestorsWithPermis(String usuariCodi, Authentication auth, EntitatEntity entitat, Permission[] permisos) {

		// 1. Obtenim els òrgans gestors amb permisos
		List<OrganGestorEntity> organsDisponibles = findOrganismesEntitatAmbPermis(entitat, permisos);
		if (organsDisponibles == null || organsDisponibles.isEmpty()) {
			return organsDisponibles;
		}
		Set<OrganGestorEntity> organsGestorsAmbPermis = new HashSet<>(organsDisponibles);
		// 2. Obtenim els òrgans gestors fills dels organs gestors amb permisos
		for (OrganGestorEntity organGestorEntity : organsDisponibles) {
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi());
			if (organsFills == null) {
				continue;
			}
			for(String organCodi: organsFills) {
				organsGestorsAmbPermis.add(organGestorRepository.findByCodi(organCodi));
			}
		}
		organsDisponibles = new ArrayList<>(organsGestorsAmbPermis);
		return organsDisponibles;
	}

	public List<String> findCodiOrgansGestorsWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {

		List<OrganGestorEntity> organs = permisosCacheable.findOrgansGestorsWithPermis(entitat, auth, permisos);
		List<String> codis = new ArrayList<>();
		for (OrganGestorEntity organGestorDto : organs) {
			codis.add(organGestorDto.getCodi());
		}
		return codis;
	}

	public List<OrganGestorEntity> findOrgansGestorsWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {

		List<OrganGestorEntity> organs = permisosCacheable.findOrgansGestorsWithPermis(entitat, auth, permisos);
		if (organs.isEmpty()) {
			return new ArrayList<>();
		}
		Set<String> codisOrgansAmbDescendents = new HashSet<>();
		for (OrganGestorEntity organGestorEntity : organs) {
			codisOrgansAmbDescendents.addAll(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestorEntity.getCodi()));
		}
		return organGestorRepository.findByCodiIn(new ArrayList<>(codisOrgansAmbDescendents));
	}

	public List<OrganGestorEntity> findOrganismesEntitatAmbPermis(EntitatEntity entitat, Permission[] permisos) {

		List<Long> objectsIds = permisosHelper.getObjectsIdsWithPermission(OrganGestorEntity.class, permisos);
		if (objectsIds.isEmpty()) {
			return new ArrayList<>();
		}
		return organGestorRepository.findByEntitatAndIds(entitat, objectsIds);
	}

//	public OrganGestorEntity createOrganGestorFromNotificacio(
//			NotificacioDatabaseDto notificacio,
//			EntitatEntity entitat
//	) {
//		String codiOrgan = notificacio.getOrganGestorCodi();
//		OrganGestorEntity organGestor = organGestorRepository.findByCodi(codiOrgan);
//		if (organGestor == null) {
//			Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
//			if (!organigramaEntitat.containsKey(codiOrgan)) {
//				throw new NotFoundException(
//						codiOrgan,
//						OrganGestorEntity.class,
//						"L'òrgan gestor especificat no es correspon a cap Òrgan Gestor de l'entitat especificada");
//			}
//			crearOrganGestor(entitat, codiOrgan);
//		}
//
//		return organGestor;
//	}

//	/**
//	 * Registra un nou òrgan gestor a la base de dades amb les dades del òrgan amb aquest codi
//	 * proporcionades per la API de DIR3.
//	 *
//	 * @param entitat L'entitat actual
//	 * @param codiOrgan Codi dir3 de l'òrgan que es vol agregar a la base de dades
//	 *
//	 * @return L'òrgan gestor creat
//	 */
//	public OrganGestorEntity crearOrganGestor(EntitatEntity entitat, String codiOrgan) {
//		LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
//				entitat.getDir3Codi(),
//				codiOrgan);
//		Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
//		List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
//				arbreUnitats,
//				codiOrgan);
//		NodeDir3 nodeOrgan = arbreUnitats.get(codiOrgan);
//		String codiPare = nodeOrgan != null ? nodeOrgan.getSuperior().split("-")[0].trim() : null;
//		OrganGestorEntity organGestor = OrganGestorEntity.builder(
//				codiOrgan,
//				findDenominacioOrganisme(nodeOrgan, codiOrgan),
//				codiPare,
//				entitat,
//				llibreOrgan.getCodi(),
//				llibreOrgan.getNomLlarg(),
//				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getCodi() : null),
//				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getNom() : null),
//				getEstatOrgan(nodeOrgan)
//		).build();
//		organGestorRepository.save(organGestor);
//		return organGestor;
//	}

//	/**
//	 * Obté l'estat de l'organ gestor d'un node de dir3 en el format utilitzat per NOTIB
//	 *
//	 * @param nodeOrgan Node d'un òrgan gestor obtingut de l'API de DIR3
//	 *
//	 * @return L'estat de l'òrgan
//	 */
//	public OrganGestorEstatEnum getEstatOrgan(NodeDir3 nodeOrgan) {
//		if (nodeOrgan == null){
//			logger.info("getEstatOrgan - nodeOrgan null");
//			return OrganGestorEstatEnum.ALTRES;
//		}
//
//		logger.info("getEstatOrgan - nodeOrgan: " + nodeOrgan.getEstat());
//
//		if (nodeOrgan.getEstat().toUpperCase().startsWith("VIGENT")) {
//			return OrganGestorEstatEnum.VIGENT;
//
//		} else {
//			return OrganGestorEstatEnum.ALTRES;
//		}
//	}

//	private String findDenominacioOrganisme(NodeDir3 nodeOrgan, String codiDir3) {
//		if (nodeOrgan != null){
//			return nodeOrgan.getDenominacio();
//		}
//		String denominacio = null;
//		try {
//			denominacio = cacheHelper.findDenominacioOrganisme(codiDir3);
//		} catch (Exception e) {
//			String errorMessage = "No s'ha pogut recuperar la denominació de l'organismes: " + codiDir3;
//			log.error(
//					errorMessage,
//					e.getMessage());
//		}
//		return denominacio;
//	}
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

	public void consultaCanvisOrganigrama(EntitatEntity entitat) {

		Date ara = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ara);
		calendar.add(Calendar.YEAR, 1);

		List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(conversioTipusHelper.convertir(entitat, EntitatDto.class),
										entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}

		if (unitatsWs != null && !unitatsWs.isEmpty()) {
			String msg = "Realitzi el procés de sincronització d'òrgans gestors per a disposar dels òrgans gestors actuals.";
			AvisEntity avis = AvisEntity.getBuilder(ORGAN_NO_SYNC, msg, ara, calendar.getTime(), AvisNivellEnumDto.ERROR, true, entitat.getId()).build();
			avisRepository.save(avis);
		}
	}

	@Transactional
	public void sincronitzarOrgans(Long entitatId, List<NodeDir3> unitatsWs, List<OrganGestorEntity> obsoleteUnitats, List<OrganGestorEntity> organsDividits,
								   List<OrganGestorEntity> organsFusionats, List<OrganGestorEntity> organsSubstituits, ProgresActualitzacioDto progres) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
		int nombreUnitatsTotal = unitatsWs.size();
		int nombreUnitatsProcessades = 0;

		String prefix = "[SYNC-ORGANS] ";
		log.debug(prefix + "Sincronitzant òrgans gestors");
		// Agafa totes les unitats del WS i les guarda a BBDD. Si la unitat no existeix la crea, i si existeix la sobreescriu.
		for (NodeDir3 unitatWS: unitatsWs) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.unitat", new Object[] {unitatWS.getCodi() + " - " + unitatWS.getDenominacio()}));
			sincronizarUnitat(unitatWS, entitat);
			progres.setProgres(2 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
		}
		progres.setProgres(12);

		// Històrics
		log.debug(prefix + "Sincronitzant històric unitats");
		nombreUnitatsProcessades = 0;
		for (NodeDir3 unitatWS : unitatsWs) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.historic", new Object[] {unitatWS.getCodi() + " - " + unitatWS.getDenominacio()}));
			OrganGestorEntity unitat = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodi());
			sincronizarHistoricsUnitat(unitat, unitatWS, entitat);
			progres.setProgres(12 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
		}
		progres.setProgres(22);
//		obsoleteUnitats.addAll(organGestorRepository.findByEntitatNoVigent(entitat));
		// Definint tipus de transició
		log.debug(prefix + "Sincronitzant unitats obsoletes");
		nombreUnitatsProcessades = 0;
		nombreUnitatsTotal = obsoleteUnitats.size();
		for (OrganGestorEntity obsoleteUnitat : obsoleteUnitats) {
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.definir.transicio", new Object[] {obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom()}));
			if (obsoleteUnitat.getNous() == null || obsoleteUnitat.getNous().isEmpty()) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.EXTINCIO);
			} else if (obsoleteUnitat.getNous().size() > 1) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.DIVISIO);
				organsDividits.add(obsoleteUnitat);
			} else {
				if (obsoleteUnitat.getNous().size() == 1) {
					if (obsoleteUnitat.getNous().get(0).getAntics().size() > 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.FUSIO);
						organsFusionats.add(obsoleteUnitat);
					} else if (obsoleteUnitat.getNous().get(0).getAntics().size() == 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);
						organsSubstituits.add(obsoleteUnitat);
					}
				}
			}
			log.debug(prefix + "Unitat extingida " + obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom());
			obsoleteUnitat.setEstat(OrganGestorEstatEnum.E);
			progres.setProgres(22 + (nombreUnitatsProcessades++ * 5 / nombreUnitatsTotal));
		}

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
		log.debug(prefix + "Esborrant avisos ");
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		progres.setProgres(27);
		Date ara = new Date();
		log.debug(prefix + "Data de sincronització " + ara);
		// Si és la primera sincronització
		if (entitat.getDataSincronitzacio() == null) {
			entitat.setDataSincronitzacio(ara);
		}
		entitat.setDataActualitzacio(ara);
	}

	private OrganGestorEntity sincronizarUnitat(NodeDir3 unitatWS, EntitatEntity entitat) {

		OrganGestorEntity unitat = null;
		String prefix = "[SYNC-ORGANS] ";
		if (unitatWS == null) {
			return unitat;
		}
		// checks if unitat already exists in database
		unitat = organGestorRepository.findByCodi(unitatWS.getCodi());
		// if not it creates a new one
		if (unitat != null) {
			unitat.update(unitatWS.getDenominacio(), unitatWS.getEstat(), unitatWS.getSuperior());
			updateLlibreAndOficina(unitat, entitat.getDir3Codi());
			log.debug(prefix + "guardant nova unitat amb codi " + unitat.getCodi() + " - " + unitat.getNom());
			return unitat;
		}
		// Venen les unitats ordenades, primer el pare i després els fills?
		unitat = OrganGestorEntity.builder().codi(unitatWS.getCodi()).entitat(entitat).nom(unitatWS.getDenominacio()).codiPare(unitatWS.getSuperior())
				.estat(unitatWS.getEstat()).build();
		updateLlibreAndOficina(unitat, entitat.getDir3Codi());
		log.debug(prefix + "guardant nova unitat amb codi " + unitat.getCodi() + " - " + unitat.getNom());
		organGestorRepository.save(unitat);
		return unitat;

	}

	private void updateLlibreAndOficina(OrganGestorEntity organ, String entitatDir3Codi) {

		var llibre = pluginHelper.llistarLlibreOrganisme(entitatDir3Codi, organ.getCodi());
		if (llibre != null) {
			organ.updateLlibre(llibre.getCodi(), llibre.getNomLlarg());
		}
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Actualització d'oficines SIR per l'entitat " + entitatDir3Codi,
				IntegracioAccioTipusEnumDto.PROCESSAR);
		var arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitatDir3Codi);
		procesarOficinaOrgan(info, arbreUnitats, organ);
	}

	private void sincronizarHistoricsUnitat(OrganGestorEntity unitat, NodeDir3 unidadWS, EntitatEntity entitat) {

		if (unidadWS.getHistoricosUO() == null || unidadWS.getHistoricosUO().isEmpty()) {
			return;
		}
		for (String historicoCodi : unidadWS.getHistoricosUO()) {
			OrganGestorEntity nova = organGestorRepository.findByEntitatAndCodi(entitat, historicoCodi);
			unitat.addNou(nova);
			nova.addAntic(unitat);
		}
	}

	@Transactional
	public void deleteExtingitsNoUtilitzats(List<OrganGestorEntity> obsoleteUnitats, ProgresActualitzacioDto progres) {

		// Eliminar organs no vigents no utilitzats??
		int nombreUnitatsTotal = obsoleteUnitats.size();
		int nombreUnitatsProcessades = 0;

		Iterator<OrganGestorEntity> it = obsoleteUnitats.iterator();
		while (it.hasNext()) {
			OrganGestorEntity organObsolet = it.next();
			progres.setProgres(81 + (nombreUnitatsProcessades++ * 18)/nombreUnitatsTotal);
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.check", new Object[] {organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			Integer nombreProcediments = procSerOrganRepository.countByOrganGestor(organObsolet);
			if (nombreProcediments > 0) {
				continue;
			}
			Integer nombreExpedients = notificacioRepository.countByOrganGestor(organObsolet);
			if (nombreExpedients > 0) {
				continue;
			}
			try {
				permisosHelper.eliminarPermisosOrgan(organObsolet);
				organGestorRepository.delete(organObsolet);
				progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.borrat", new Object[] {organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			} catch (Exception ex) {
				logger.error("No ha estat possible esborrar l'òrgan gestor.", ex);
				progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.error", new Object[] {organObsolet.getCodi() + " - " + organObsolet.getNom()}));
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void procesarOficinaOrgan(IntegracioInfo info, Map<String, OrganismeDto> arbreUnitats, OrganGestorEntity organ) {

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

	@Transactional
	public void actualitzarOficinaOrgan(String organCodi, OficinaDto oficina) {

		OrganGestorEntity organ = organGestorRepository.findByCodi(organCodi);
		organ.setOficina(oficina.getCodi());
		organ.setOficinaNom(oficina.getNom());
		organGestorRepository.save(organ);
	}
}
