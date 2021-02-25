package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.ActualitzacioInfo;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.repository.*;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ProcedimentServiceImpl implements ProcedimentService{

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private ProcedimentFormRepository procedimentFormRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ProcedimentHelper procedimentHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private GrupService grupService;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private GrupProcedimentRepository grupProcedimentRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	MessageHelper messageHelper;
	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private ProcedimentOrganRepository procedimentOrganRepository;
	
	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<String, ProgresActualitzacioDto>();
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcedimentDto create(
			Long entitatId,
			ProcedimentDto procediment) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou procediment ("
					+ "procediment=" + procediment + ")");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			PagadorPostalEntity pagadorPostal = null;
			PagadorCieEntity pagadorCie = null;
			if (procediment.getPagadorpostal() != null) {
				pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
						procediment.getPagadorpostal().getId());
			}
			if (procediment.getPagadorcie() != null) {
				pagadorCie = entityComprovarHelper.comprovarPagadorCie(
						procediment.getPagadorcie().getId());
			}
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor()); 
			if (organGestor == null) {
				organGestor = createOrganGestor(procediment.getOrganGestor(), entitat);
			}
			
			ProcedimentEntity procedimentEntity = procedimentRepository.save(
					ProcedimentEntity.getBuilder(
							procediment.getCodi(),
							procediment.getNom(),
							procediment.getRetard(),
							procediment.getCaducitat(),
							entitat,
							pagadorPostal,
							pagadorCie,
							procediment.isAgrupar(),
							organGestor,
							procediment.getTipusAssumpte(),
							procediment.getTipusAssumpteNom(),
							procediment.getCodiAssumpte(),
							procediment.getCodiAssumpteNom(),
							procediment.isComu()).build());
			
			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private OrganGestorEntity createOrganGestor(String organGestorCodi, EntitatEntity entitat) {
		OrganGestorEntity organGestor;
		LlibreDto llibre = pluginHelper.llistarLlibreOrganisme(
				entitat.getCodi(),
				organGestorCodi);
		Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
		List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
				arbreUnitats, 
				organGestorCodi);
		organGestor = OrganGestorEntity.getBuilder(
				organGestorCodi,
				findDenominacioOrganisme(organGestorCodi),
				entitat,
				llibre.getCodi(),
				llibre.getNomLlarg(),
				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getCodi() : null),
				(oficinesSIR != null && !oficinesSIR.isEmpty() ? oficinesSIR.get(0).getNom() : null)).build();
		organGestorRepository.save(organGestor);
		return organGestor;
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcedimentDto update(
			Long entitatId,
			ProcedimentDto procediment,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant procediment ("
					+ "procediment=" + procediment + ")");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!isAdminEntitat && procediment.isComu()) {
				throw new PermissionDeniedException(
						procediment.getId(),
						ProcedimentEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			ProcedimentEntity procedimentEntity = null;
			PagadorPostalEntity pagadorPostal = null;
			PagadorCieEntity pagadorCie = null;
			if(!isAdmin) {
				procedimentEntity = entityComprovarHelper.comprovarProcediment(
						entitat,
						procediment.getId());
			} else {
				procedimentEntity = procedimentRepository.findOne(procediment.getId());
			}
			
			if (procediment.getPagadorpostal() != null) {
				pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
						procediment.getPagadorpostal().getId());
			}
			if (procediment.getPagadorcie() != null) {
				pagadorCie = entityComprovarHelper.comprovarPagadorCie(
						procediment.getPagadorcie().getId());
			}
			
			List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepository.findByProcediment(procedimentEntity);
			
			if (!procediment.isAgrupar()) {
				grupProcedimentRepository.delete(grupsProcediment);
			}
			
			//#271 Check canvi codi SIA, si es modifica s'han de modificar tots els enviaments pendents
			if (!procediment.getCodi().equals(procedimentEntity.getCodi())) {
				//Obtenir notificacions pendents.
				List<NotificacioEntity> notificacionsPendentsNotificar = notificacioRepository.findNotificacionsPendentsDeNotificarByProcediment(procedimentEntity);
				for (NotificacioEntity notificacioEntity : notificacionsPendentsNotificar) {
					//modificar el codi SIA i activar per tal que scheduled ho torni a agafar
					notificacioEntity.updateCodiSia(procediment.getCodi());
					notificacioEntity.resetIntentsNotificacio();
					notificacioRepository.save(notificacioEntity);
				}
			}
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor()); 
			if (organGestor == null) {
				organGestor = createOrganGestor(procediment.getOrganGestor(), entitat);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
			OrganGestorEntity organGestorAntic = null;
			if (procedimentEntity.getOrganGestor() != null && !procedimentEntity.getOrganGestor().getCodi().equals(procediment.getOrganGestor())) {
				organGestorAntic = procedimentEntity.getOrganGestor();
			}
			// Si hi ha hagut qualque canvi a un d'aquests camps
			if ((procediment.isComu() != procedimentEntity.isComu()) || (procediment.isAgrupar() != procedimentEntity.isAgrupar())) {
				cacheHelper.evictFindProcedimentsWithPermis();
				cacheHelper.evictFindProcedimentsOrganWithPermis();
			}
			procedimentEntity.update(
						procediment.getCodi(),
						procediment.getNom(),
						entitat,
						pagadorPostal,
						pagadorCie,
						procediment.getRetard(),
						procediment.getCaducitat(),
						procediment.isAgrupar(),
						organGestor,
						procediment.getTipusAssumpte(),
						procediment.getTipusAssumpteNom(),
						procediment.getCodiAssumpte(),
						procediment.getCodiAssumpteNom(),
						procediment.isComu());
		
			procedimentRepository.save(procedimentEntity);
			
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (2)
			if (organGestorAntic != null) {
				List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestorAntic.getId());
				if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
					organGestorRepository.delete(organGestorAntic);
				}
			}

			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcedimentDto delete(
			Long entitatId,
			Long id,
			boolean isAdminEntitat) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
					entitat, 
					id);
			if (!isAdminEntitat && procedimentEntity.isComu()) {
				throw new PermissionDeniedException(
						procedimentEntity.getId(),
						ProcedimentEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			//Eliminar grups del procediment
			List<GrupProcedimentEntity> grupsDelProcediment = grupProcedimentRepository.findByProcediment(procedimentEntity);
			for (GrupProcedimentEntity grupProcedimentEntity : grupsDelProcediment) {
				grupProcedimentRepository.delete(grupProcedimentEntity);
			}
			//Eliminar procediment
			procedimentRepository.delete(procedimentEntity);
			permisosHelper.revocarPermisosEntity(id,ProcedimentEntity.class);
			
			//TODO: Decidir si mantenir l'Organ Gestor encara que no hi hagi procediments o no
			//		Recordar que ara l'Organ té més coses assignades: Administrador, grups, pagadors ...
			//		Es pot mirar si esta en ús amb la funció organGestorService.organGestorEnUs(organId);
//			OrganGestorEntity organGestor = procedimentEntity.getOrganGestor();
//			if (organGestor != null) {
//				List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestor.getId());
//				if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
//					organGestorRepository.delete(organGestor);
//				}
//			}
			
			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentEnUs(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			boolean procedimentEnUs=false;
				//1) Si té notificacions
				List<NotificacioEntity> notificacionsByProcediment = notificacioRepository.findByProcedimentId(procedimentId);
				procedimentEnUs=notificacionsByProcediment != null && !notificacionsByProcediment.isEmpty();
			
			return procedimentEnUs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentAmbGrups(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			ProcedimentEntity procediment = procedimentRepository.findById(procedimentId);
			return procediment.isAgrupar();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private String findDenominacioOrganisme(String codiDir3) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String denominacio = null;
			try {
				denominacio = cacheHelper.findDenominacioOrganisme(codiDir3);
			} catch (Exception e) {
				String errorMessage = "No s'ha pogut recuperar la denominació de l'organismes: " + codiDir3;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return denominacio;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingProcediments(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	//@Transactional(timeout = 300)
	public void actualitzaProcediments(EntitatDto entitatDto) {
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			long startTime;
			double elapsedTime;
			ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getDir3Codi());
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_PROCEDIMENT, 
					"Actualització de procediments", 
					IntegracioAccioTipusEnumDto.PROCESSAR, 
					new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
			
			try {
				boolean modificar = isActualitzacioProcedimentsModificarProperty();
				boolean eliminarOrgans = isActualitzacioProcedimentsEliminarOrgansProperty();
				Long ti = System.currentTimeMillis();
//				logger.debug(">>>> Inici actualitzar procediments");
//				logger.debug(">>>> ==========================================================================");
				if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
//					logger.debug(">>>> Ja existeix un altre procés que està executant l'actualització");
//					logger.debug(">>>> ==========================================================================");
					
					return;	// Ja existeix un altre procés que està executant l'actualització.
				} else {
					progres = new ProgresActualitzacioDto();
					progresActualitzacio.put(entitatDto.getDir3Codi(), progres);
				}
				progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("procediment.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));
			
				List<OrganGestorEntity> organsGestorsModificats = new ArrayList<OrganGestorEntity>();
				EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
						entitatDto.getId(),
						false,
						false,
						false);

				startTime = System.nanoTime();
				Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(entitatDto.getDir3Codi());
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-PRO] Obtenir organigrama de l'entitat: " + elapsedTime + " ms");
//				OficinaDto oficinaVirtual = pluginHelper.llistarOficinaVirtual(
//						entitatDto.getDir3Codi(), 
//						entitat.getNomOficinaVirtual(),
//						TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);

//				logger.debug(">>>> Obtenir de 30 en 30 els procediments de Rolsac de l'entitat...");
				progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments"));
				List<ProcedimentDto> procedimentsGda  = new ArrayList<ProcedimentDto>();
//				List<ProcedimentDto> totalProcedimentsGda  = new ArrayList<ProcedimentDto>();
				startTime = System.nanoTime();
				int totalElements = getTotalProcediments(entitatDto.getDir3Codi());
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-PRO] Obtenir nombre de procediments de l'entitat: " + elapsedTime + " ms");
				int totalElementsCons = totalElements;
				Long t1 = System.currentTimeMillis();
				int numPagina = 1;
				int reintents = 0;
				int i = 1;
				boolean errorConsultantLlista = false;
				boolean darreraLlista = false;
				startTime = System.nanoTime();
				do {
					try {
						procedimentsGda = getProcedimentsGdaByEntitat(
								entitatDto.getDir3Codi(),
								numPagina);
						
						if (procedimentsGda.size() < 30) 
							darreraLlista = true;
						errorConsultantLlista = false;
						reintents = 0;
					} catch (Exception e) {
						errorConsultantLlista = true;
						procedimentsGda = new ArrayList<ProcedimentDto>();
						progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.error"));
						numPagina++;
						reintents++;
//						Elements no actualitzats
						totalElements -= ((totalElements - progres.getNumProcedimentsActualitzats()) < 30) ? ((totalElements - progres.getNumProcedimentsActualitzats())) : (30);
						continue;
					}
					
					Long t2 = System.currentTimeMillis();
//					logger.debug(">>>> obtinguts " + procedimentsGda.size() + " procediments (" + (t2 - t1) + "ms)");
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.result", new Object[] {procedimentsGda.size()}));
					progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
					progres.setNumProcediments(totalElements);

//					logger.debug(">>>> ==========================================================================");
//					logger.debug(">>>> Processar procediments");
//					logger.debug(">>>> ==========================================================================");
					progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediments", new Object[] {procedimentsGda.size()}));
					
					for (ProcedimentDto procedimentGda: procedimentsGda) {
						//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava, 
						//i també d'aquesta forma els que s'han carregat ja es guardan.
						procedimentHelper.actualitzarProcedimentFromGda(
								progres, 
								t1, 
								t2, 
								procedimentGda, 
								entitatDto, 
								entitat,
								null, //oficinaVirtual  
								organigramaEntitat,  
								modificar,
								organsGestorsModificats,  
								i);
						i++;
					}
					
//					totalProcedimentsGda.addAll(procedimentsGda);
					numPagina++;
				} while (!darreraLlista || (errorConsultantLlista && reintents < 3));
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-PRO] Recorregut procediments i actualització: " + elapsedTime + " ms");
				
				if (eliminarOrgans) {
					startTime = System.nanoTime();
//					int i = 1;
//					logger.debug(">>>> Processant organs gestors modificats (" + organsGestorsModificats.size() + ")");
//					logger.debug(">>>> ==========================================================================");
					progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs"));
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.check"));
					if (organsGestorsModificats.isEmpty()) {
//						logger.debug(">>>> No hi ha organs gestors a modificar");
						progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.no"));
					} else {
						progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.si", new Object[] {organsGestorsModificats.size()}));
					}
					
					for (OrganGestorEntity organGestorAntic: organsGestorsModificats) {
						//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava
						procedimentHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
					}
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					logger.info(" [TIMER-PRO] Eliminar organs: " + elapsedTime + " ms");
				} else {
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.inactiu"));
				}
				
//				logger.debug(">>>> ==========================================================================");
//				logger.debug(">>>> Fi actualitzar procediments");
//				logger.debug(">>>> ==========================================================================");
				Long tf = System.currentTimeMillis();
				progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.fi.resultat", new Object[] {progres.getNumProcedimentsActualitzats(), totalElementsCons}));
				progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.fi", new Object[] {entitatDto.getNom()}));
				for (ActualitzacioInfo inf: progres.getInfo()) {
					if (inf.getText() != null)
						info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
				}
				integracioHelper.addAccioOk(info);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				progresActualitzacio.get(entitatDto.getDir3Codi()).setError(true);
				progresActualitzacio.get(entitatDto.getDir3Codi()).setErrorMsg(sw.toString());
				progresActualitzacio.get(entitatDto.getDir3Codi()).setProgres(100);
				
				for (ActualitzacioInfo inf: progres.getInfo()) {
					if (inf.getText() != null)
						info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
				}
				integracioHelper.addAccioError(info, "Error actualitzant procediments: ", e);
				throw e;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private int getTotalProcediments(String codiDir3) {		
		logger.debug(">>>> >> Obtenir total procediments Rolsac...");
		Long t1 = System.currentTimeMillis();
		int totalElements = pluginHelper.getTotalProcediments(codiDir3);
		Long t2 = System.currentTimeMillis();
		logger.debug(">>>> >> resultat"  + totalElements + " procediments (" + (t2 - t1) + "ms)");		
		return totalElements;
	}
	
	private List<ProcedimentDto> getProcedimentsGdaByEntitat(
			String codiDir3,
			int numPagina) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(codiDir3);
		
		logger.debug(">>>> >> Obtenir tots els procediments de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();
		
		List<ProcedimentDto> procedimentsEntitat = pluginHelper.getProcedimentsGdaByEntitat(
				codiDir3,
				numPagina);
		
		Long t2 = System.currentTimeMillis();
		logger.debug(">>>> >> obtinguts" + procedimentsEntitat.size() + " procediments (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm.result", new Object[] {procedimentsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		
		return procedimentsEntitat;
	}
	
	private boolean isActualitzacioProcedimentsModificarProperty() {
		String modificar = PropertiesHelper.getProperties().getProperty("es.caib.notib.actualitzacio.procediments.modificar");
		if (modificar != null) {
			return new Boolean(modificar).booleanValue();
		} else {
			return true;
		}
	}
	
	private boolean isActualitzacioProcedimentsEliminarOrgansProperty() {
		String eliminar = PropertiesHelper.getProperties().getProperty("es.caib.notib.actualitzacio.procediments.eliminar.organs");
		if (eliminar != null) {
			return new Boolean(eliminar).booleanValue();
		} else {
			return false;
		}
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			ProgresActualitzacioDto progres = progresActualitzacio.get(dir3Codi);
			if (progres != null && progres.getProgres() != null &&  progres.getProgres() >= 100) {
				progresActualitzacio.remove(dir3Codi);
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "procedimentId=" + procedimentId + ")");
				
			if (entitatId != null && !isAdministrador)
				entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
	
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitatId, 
					procedimentId);
			ProcedimentDto resposta = conversioTipusHelper.convertir(
					procediment,
					ProcedimentDto.class);
			
			if (resposta != null) {
				procedimentHelper.omplirPermisos(resposta, false);
			}
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findByCodi(
			Long entitatId,
			String codiProcediment) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codiProcediment + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
			
			ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(codiProcediment, entitat);
			
			return conversioTipusHelper.convertir(
					procediment, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			List<ProcedimentEntity> procediment = procedimentRepository.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					procediment,
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
				entitat.getDir3Codi(), 
				organGestor.getCodi());
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByOrganGestorCodiIn(organsFills),
				ProcedimentDto.class);
	}

	@Override
	public List<ProcedimentDto> findByOrganGestorIDescendentsAndComu(Long entitatId, OrganGestorDto organGestor) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
				entitat.getDir3Codi(),
				organGestor.getCodi());
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat),
				ProcedimentDto.class);
	}

	@Override
	@Transactional
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			OrganGestorDto organGestorActual,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
			List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
			List<Long> entitatsActivaId = new ArrayList<Long>();
			
			for (EntitatEntity entitatActiva : entitatsActiva) {
				entitatsActivaId.add(entitatActiva.getId());
			}
			Page<ProcedimentFormEntity> procediments = null;
			PaginaDto<ProcedimentFormDto> procedimentsPage = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organGestor.codi"});
//			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organGestorNom"}); //{"organGestor", "organGestorNom"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			List<String> organsFills = new ArrayList<String>();
			if (organGestorActual != null) { // Administrador d'òrgan
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitatActual.getDir3Codi(), 
						organGestorActual.getCodi());
			}

			if (filtre == null) {
				
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatActual(
							entitatActual.getId(),
							pageable);
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				} else if (isAdministrador) {
					procediments = procedimentFormRepository.findAmbEntitatActiva(
							entitatsActivaId,
							pageable);
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				} else if (organGestorActual != null) { // Administrador d'òrgan
					procediments = procedimentFormRepository.findAmbOrganGestorActualOrComu(
							entitatActual.getId(),
							organsFills,
							pageable);
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				}
			} else {
			
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.getComu()== null,		
							filtre.getComu()== null ? false : filtre.getComu(),
							pageable);
					
					procedimentsPage = paginacioHelper.toPaginaDto(
							procediments, 
							ProcedimentFormDto.class);
					
				} else if (isAdministrador) {
					procediments = procedimentFormRepository.findAmbFiltre(
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.getComu()== null,
							filtre.getComu()== null ? false : filtre.getComu(),
							pageable);
					
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				} else if (organGestorActual != null) { // Administrador d'òrgan
					procediments = procedimentFormRepository.findAmbOrganGestorOrComuAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							organsFills,
							filtre.getComu()== null,
							filtre.getComu()== null ? false : filtre.getComu(),
							pageable);
					
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				}
			}
			for (ProcedimentFormDto procediment: procedimentsPage.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(
						procediment.getId(),
						ProcedimentEntity.class);

				if (procediment.isComu()) {
					String organActual = null;
					if (organGestorActual != null) {
						organActual = organGestorActual.getCodi();
//					} else {
//						organActual = entitatActual.getDir3Codi();
					}
					permisos.addAll(findPermisProcedimentOrganByProcediment(procediment.getId(), organActual));
				}

				List<GrupDto> grups = grupService.findGrupsByProcediment(procediment.getId());
				procediment.setGrups(grups);
				procediment.setPermisos(permisos);
			}
			return procedimentsPage;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els procediments");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			return conversioTipusHelper.convertirList(
						procedimentRepository.findAll(),
						ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentGrupDto> findAllGrups() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els procediments");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			
			List<GrupProcedimentEntity> grupsProcediments = grupProcedimentRepository.findAll();
			return conversioTipusHelper.convertirList(
						grupsProcediments,
						ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentGrupDto> findGrupsByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els procediments d'una entitat");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			List<GrupProcedimentEntity> grupsProcediments = grupProcedimentRepository.findByProcedimentEntitat(entitat);
			return conversioTipusHelper.convertirList(
						grupsProcediments,
						ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findProcediments(Long entitatId, List<String> grups) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {	
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups),
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsAmbGrupsByEntitatAndGrup(entitat, grups),
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findProcedimentsSenseGrups(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsSenseGrupsByEntitat(entitat),
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "procedimentsPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			List<ProcedimentEntity> procediments = procedimentHelper.getProcedimentsWithPermis(
					usuariCodi,
					auth,
					entitat,
					permisos);

			// 6. Ordenam els procediments
			Collections.sort(procediments, new Comparator<ProcedimentEntity>() {
				@Override
				public int compare(ProcedimentEntity p1, ProcedimentEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});
			
			// 7. Convertim els procediments a dto
			return conversioTipusHelper.convertirList(
					procediments,
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findProcedimentsByOrganGestor(String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			if (organGestor == null) {
				throw new NotFoundException(
						organGestorCodi,
						OrganGestorEntity.class);
			}
			return conversioTipusHelper.convertirList(
					procedimentRepository.findByOrganGestorId(organGestor.getId()),
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findProcedimentsByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups,
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, organGestorCodi);
			// 1. Obtenim tots els procediments de l'òrgan gestor
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByOrganGestorAndGrup(entitat, organGestor.getId(), grups);
			
			// 2. Si tenim permis a sobre de l'òrgan o un dels pares, llavors tenim permís a sobre tots els procediments de l'òrgan
			List<OrganGestorEntity> organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(entitat.getDir3Codi(), organGestorCodi);
			permisosHelper.filterGrantedAny(
					organsGestors,
					new ObjectIdentifierExtractor<OrganGestorEntity>() {
						public Long getObjectIdentifier(OrganGestorEntity organGestor) {
							return organGestor.getId();
						}
					},
					OrganGestorEntity.class,
					permisos,
					auth);
			if (organsGestors.isEmpty()) {
				// 3. Si no tenim permis sobre òrgan, llavors miram els permisos sobre el procediment
				permisosHelper.filterGrantedAny(
						procediments,
						new ObjectIdentifierExtractor<ProcedimentEntity>() {
							public Long getObjectIdentifier(ProcedimentEntity procediment) {
								return procediment.getId();
							}
						},
						ProcedimentEntity.class,
						permisos,
						auth);
			}
			
			// 4. Procediments comuns
			List<ProcedimentEntity> procedimentsComuns = procedimentRepository.findByComuTrue();
			permisosHelper.filterGrantedAny(
					procedimentsComuns,
					new ObjectIdentifierExtractor<ProcedimentEntity>() {
						public Long getObjectIdentifier(ProcedimentEntity procediment) {
							return procediment.getId();
						}
					},
					ProcedimentEntity.class,
					permisos,
					auth);
			procedimentsComuns.removeAll(procediments);
			procediments.addAll(procedimentsComuns);
			
			return conversioTipusHelper.convertirList(
					procediments,
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "procedimentsOrgan", key="{#entitatId, #organCodi, #organFiltre, #rol.ordinal(), #permis.ordinal()}")
	public List<CodiValorComuDto> getProcedimentsOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorComuDto> procedimentsOrgan = new ArrayList<>();
			List<ProcedimentEntity> procediments = new ArrayList<>();
			String organGestor = null;

			if (organFiltre != null) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organFiltre);
				if (organGestorEntity != null)
					organGestor = organGestorEntity.getCodi();
			}

			if (RolEnumDto.tothom.equals(rol)) {
				procediments = recuperarProcedimentAmbPermis(entitat, permis, organGestor);
			} else {

				if (organGestor != null) {
					List<ProcedimentEntity> procedimentsDisponibles = procedimentRepository.findByEntitat(entitat);
					if (procedimentsDisponibles != null) {
						for (ProcedimentEntity proc : procedimentsDisponibles) {
							if (proc.isComu() || (proc.getOrganGestor() != null && organGestor.equalsIgnoreCase(proc.getOrganGestor().getCodi()))) {
								procediments.add(proc);
							}
						}
					}
				} else {
					if (RolEnumDto.NOT_SUPER.equals(rol)) {
						procediments = procedimentRepository.findAll();
					} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
						procediments = procedimentRepository.findByEntitat(entitat);
					} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
						if (organCodi != null) {
							List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
									entitat.getDir3Codi(),
									organCodi);
							procediments = procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
						}
					}
				}
			}

			Collections.sort(procediments, new Comparator<ProcedimentEntity>() {
				@Override
				public int compare(ProcedimentEntity p1, ProcedimentEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});

			for (ProcedimentEntity procediment : procediments) {
				String nom = procediment.getCodi();
				if (procediment.getNom() != null && !procediment.getNom().isEmpty()) {
					nom += " - " + procediment.getNom();
				}
				procedimentsOrgan.add(new CodiValorComuDto(procediment.getId().toString(), nom, procediment.isComu()));
			}

			return procedimentsOrgan;

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<ProcedimentEntity> procediments;
		if (RolEnumDto.NOT_ADMIN.equals(rol)) {

			if (organCodi == null) {
				procediments = procedimentRepository.findByEntitat(entitat);
			}else {
				List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(),
						organCodi);
				procediments = procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
			}
		} else {
			procediments = recuperarProcedimentAmbPermis(entitat, PermisEnum.NOTIFICACIO, organCodi);
		}

		return procedimentsToCodiValorOrganGestorComuDto(procediments);
	}

	private List<CodiValorOrganGestorComuDto> procedimentsToCodiValorOrganGestorComuDto(List<ProcedimentEntity> procediments) {
		List<CodiValorOrganGestorComuDto> response = new ArrayList<>();
		for (ProcedimentEntity procediment : procediments) {
			String nom = procediment.getCodi();
			if (procediment.getNom() != null && !procediment.getNom().isEmpty()) {
				nom += " - " + procediment.getNom();
			}
			response.add(new CodiValorOrganGestorComuDto(procediment.getId().toString(), nom, procediment.getCodi(),
					procediment.isComu()));
		}
		return response;
	}

	private List<ProcedimentEntity> recuperarProcedimentAmbPermis(
			EntitatEntity entitat,
			PermisEnum permis,
			String organFiltre) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
		List<ProcedimentEntity> procediments = procedimentHelper.getProcedimentsWithPermis(
				auth.getName(),
				auth,
				entitat,
				permisos);
		List<ProcedimentOrganEntity> procedimentsOrgans = procedimentHelper.getProcedimentOrganWithPermis(
				auth.getName(),
				auth,
				entitat,
				permisos);
		List<ProcedimentEntity> results;
		if (organFiltre != null) {
			List<ProcedimentOrganEntity> procedimentsOrgansAmbPermis = new ArrayList<>();
			if(procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {

				List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organFiltre);
				for (ProcedimentOrganEntity procedimentOrgan: procedimentsOrgans) {
					if (organsFills.contains(procedimentOrgan.getOrganGestor().getCodi()))
						procedimentsOrgansAmbPermis.add(procedimentOrgan);
				}
			}

			results = addProcedimentsOrgan(procediments, procedimentsOrgansAmbPermis, organFiltre);

			boolean hasComunsPermission = permisosHelper.haPermission(
					organGestorRepository.findByCodi(organFiltre).getId(),
					OrganGestorEntity.class,
					new Permission[] {ExtendedPermission.COMUNS});
			if (hasComunsPermission && PermisEnum.NOTIFICACIO.equals(permis)) {
				List<ProcedimentEntity> procedimentsComuns = procedimentRepository.findByEntitatAndComuTrue(entitat);
				for (ProcedimentEntity procediment: procedimentsComuns) {
					if (!results.contains(procediment))
						results.add(procediment);
				}
			}
		} else {
			results = addProcedimentsOrgan(procediments, procedimentsOrgans, organFiltre);
		}

		return results;
	}

	private List<ProcedimentEntity> addProcedimentsOrgan(
			List<ProcedimentEntity> procediments,
			List<ProcedimentOrganEntity> procedimentsOrgans,
			String organFiltre) {

		Set<ProcedimentEntity> setProcediments = new HashSet<>();

		if (organFiltre != null) {
			if (procediments != null) {
				for (ProcedimentEntity proc : procediments) {
					if (proc.isComu() || (proc.getOrganGestor() != null && organFiltre.equalsIgnoreCase(proc.getOrganGestor().getCodi()))) {
						setProcediments.add(proc);
					}
				}
			}
			if (procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {
				for (ProcedimentOrganEntity procedimentOrgan : procedimentsOrgans) {
					setProcediments.add(procedimentOrgan.getProcediment());
				}
			}
		} else {
			if (procediments != null)
				setProcediments = new HashSet<>(procediments);
			if (procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {
				for (ProcedimentOrganEntity procedimentOrgan : procedimentsOrgans) {
					setProcediments.add(procedimentOrgan.getProcediment());
				}
			}
		}
		return new ArrayList<ProcedimentEntity>(setProcediments);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups);
			if (procediments == null || procediments.isEmpty())
				return false;
			
			permisosHelper.filterGrantedAny(
					procediments,
					new ObjectIdentifierExtractor<ProcedimentEntity>() {
						public Long getObjectIdentifier(ProcedimentEntity procediment) {
							return procediment.getId();
						}
					},
					ProcedimentEntity.class,
					entityComprovarHelper.getPermissionsFromName(permis),
					SecurityContextHolder.getContext().getAuthentication());
			return !procediments.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisProcediment(
			Long procedimentId,
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
//			ProcedimentEntity procediment = procedimentRepository.findById(procedimentId);
//			procediments.add(procediment);
//			EntitatEntity entitat = procediment.getEntitat();
//			
//			// 1. Comprovam si el procediment té assignat el permís d'administration
//			Permission[] permisos = getPermissionsFromName(permis);
//			permisosHelper.filterGrantedAny(
//					procediments,
//					new ObjectIdentifierExtractor<ProcedimentEntity>() {
//						public Long getObjectIdentifier(ProcedimentEntity procediment) {
//							return procediment.getId();
//						}
//					},
//					ProcedimentEntity.class,
//					permisos,
//					auth);
//			if (!procediments.isEmpty())
//				return true;
//			
//			// 2. Comprovam si algun organ pare del procediment té permis d'administration
//			List<OrganGestorEntity> organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(
//					entitat.getDir3Codi(), 
//					procediment.getOrganGestor().getCodi());
//			permisosHelper.filterGrantedAny(
//					organsGestors,
//					new ObjectIdentifierExtractor<OrganGestorEntity>() {
//						public Long getObjectIdentifier(OrganGestorEntity organGestor) {
//							return organGestor.getId();
//						}
//					},
//					OrganGestorEntity.class,
//					permisos,
//					auth);
//			if (!organsGestors.isEmpty())
//				return true;
//			
//			return false;
			return entityComprovarHelper.hasPermisProcediment(procedimentId, permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
//	private Permission[] getPermissionsFromName(PermisEnum permis) {
//		Permission perm = getPermissionFromName(permis);
//		if (perm == null)
//			return null;
//		else
//			return new Permission[] {perm};
//	}
//	
//	private Permission getPermissionFromName(PermisEnum permis) {
//		switch (permis) {
//		case CONSULTA: return ExtendedPermission.READ;
//		case PROCESSAR: return ExtendedPermission.PROCESSAR;
//		case NOTIFICACIO: return ExtendedPermission.NOTIFICACIO;
//		case GESTIO: return ExtendedPermission.ADMINISTRATION;
//		default: return null;
//		}
//	}
	
	@Transactional(readOnly = true)
	@Cacheable(value = "procedimentsOrganPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	@Override
	public List<ProcedimentOrganDto> findProcedimentsOrganWithPermis(
			Long entitatId, 
			String usuariCodi, 
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			List<ProcedimentOrganEntity> procedimentOrgansAmbPermis = procedimentHelper.getProcedimentOrganWithPermis(
					usuariCodi,
					auth,
					entitat,
					permisos);

			// 2. Convertim els procediments a dto
			return conversioTipusHelper.convertirList(
					procedimentOrgansAmbPermis,
					ProcedimentOrganDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentOrganDto> findProcedimentsOrganWithPermisByOrgan(
			String organGestor, 
			String entitatCodi,
			List<ProcedimentOrganDto> procedimentsOrgans) {
		
		List<ProcedimentOrganDto> procedimentsOrgansAmbPermis = new ArrayList<ProcedimentOrganDto>();
		if(procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {

			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitatCodi, organGestor);
			for (ProcedimentOrganDto procedimentOrgan: procedimentsOrgans) {
				if (organsFills.contains(procedimentOrgan.getOrganGestor().getCodi()))
					procedimentsOrgansAmbPermis.add(procedimentOrgan);
			}
		}
		return procedimentsOrgansAmbPermis;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(
			ProcedimentDto procediment, 
			String entitatCodi,
			List<ProcedimentOrganDto> procedimentsOrgans) {
		
		Set<String> organsDisponibles = new HashSet<String>();
		if(!procedimentsOrgans.isEmpty()) {
			for (ProcedimentOrganDto procedimentOrgan: procedimentsOrgans) {
				if (procedimentOrgan.getProcediment().equals(procediment)) {
					String organ = procedimentOrgan.getOrganGestor().getCodi(); 
					if (!organsDisponibles.contains(organ))
						organsDisponibles.addAll(organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
								entitatCodi, 
								organ));
				}
			}
		}
		return new ArrayList<String>(organsDisponibles);
	}
	
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long procedimentId,
			String organ,
			String organActual,
			TipusPermis tipus) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "procedimentId=" + procedimentId + ", " 
					+ "tipus=" + tipus + ")"); 

			List<PermisDto> permisos = new ArrayList<PermisDto>();
			
			EntitatEntity entitat = null;
			if (entitatId != null && !isAdministrador)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						false,
						false);
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitat, 
					procedimentId);
			boolean adminOrgan = organActual != null;

			if (tipus == null) {
				permisos = findPermisProcediment(procediment, adminOrgan, organActual);
				permisos.addAll(findPermisProcedimentOrganByProcediment(procedimentId, organActual));
			} else if (TipusPermis.PROCEDIMENT.equals(tipus)) {
				permisos = findPermisProcediment(procediment, adminOrgan, organActual);
			} else {
				if (organ == null)
					permisos = findPermisProcedimentOrganByProcediment(procedimentId, organActual);
				else 
					permisos = findPermisProcedimentOrgan(procedimentId, organ, organActual);
			}
			Collections.sort(permisos, new Comparator<PermisDto>() {
				@Override
				public int compare(PermisDto p1, PermisDto p2) {
					int comp = p1.getNomSencerAmbCodi().compareTo(p2.getNomSencerAmbCodi());
					if (comp == 0) {
						if (p1.getOrgan() == null && p2.getOrgan() != null)
							return 1;
						if (p1.getOrgan() != null && p2.getOrgan() == null)
							return -1;
						return p1.getOrgan().compareTo(p2.getOrgan());
					}
					return comp;
				}
			});
			return permisos;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<PermisDto> findPermisProcediment(
			ProcedimentEntity procediment,
			boolean adminOrgan,
			String organ) {
		List<PermisDto> permisos = permisosHelper.findPermisos(
				procediment.getId(),
				ProcedimentEntity.class);
		boolean isAdministradorOrganAndNoComuOrAdminEntitat = (adminOrgan && !procediment.isComu()) || //administrador òrgan i procediment no comú
				(adminOrgan && procediment.isComu() && (procediment.getOrganGestor().getCodi().equals(organ))) ||  //administrador òrgan, procediment comú però del mateix òrgan
				!adminOrgan; //administrador entitat
		for (PermisDto permis: permisos)
			permis.setPermetEdicio(isAdministradorOrganAndNoComuOrAdminEntitat);
		return permisos;
	}
	
	private List<PermisDto> findPermisProcedimentOrganByProcediment(
			Long procedimentId,
			String organGestor) {
		List<ProcedimentOrganEntity> procedimentOrgans = procedimentOrganRepository.findByProcedimentId(procedimentId);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		List<String> organsAmbPermis = new ArrayList<String>();
		if (procedimentOrgans != null && !procedimentOrgans.isEmpty()) {
			
			if (organGestor != null) {
				organsAmbPermis = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
						procedimentOrgans.get(0).getProcediment().getEntitat().getDir3Codi(), 
						organGestor);
			}
			for (ProcedimentOrganEntity procedimentOrgan: procedimentOrgans) {
				List<PermisDto> permisosProcOrgan = permisosHelper.findPermisos(
						procedimentOrgan.getId(),
						ProcedimentOrganEntity.class);
				if (permisosProcOrgan != null && !permisosProcOrgan.isEmpty()) {
					String organ = procedimentOrgan.getOrganGestor().getCodi();
					String organNom = procedimentOrgan.getOrganGestor().getNom();
					boolean tePermis = true;

					if (organGestor != null)
						tePermis = organsAmbPermis.contains(organ);

					if (tePermis) {
						for (PermisDto permis : permisosProcOrgan) {
							permis.setOrgan(organ);
							permis.setOrganNom(organNom);
							permis.setPermetEdicio(tePermis);
						}
						permisos.addAll(permisosProcOrgan);
					}
				}
			}
		}
		return permisos;
	}
	
	private List<PermisDto> findPermisProcedimentOrgan(
			Long procedimentId, 
			String organ, 
			String organActual) {
		ProcedimentOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcedimentIdAndOrganGestorCodi(procedimentId, organ);
		List<PermisDto> permisos = permisosHelper.findPermisos(
				procedimentOrgan.getId(),
				ProcedimentOrganEntity.class);
		for (PermisDto permis: permisos) {
			permis.setOrgan(organ);
			permis.setOrganNom(procedimentOrgan.getOrganGestor().getNom());
		}
		return permisos;
	}

	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long organGestorId,
			Long id,
			PermisDto permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permis + ")");
			
			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(entitatId, id);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			// Permís a procediment comú no global
			if (procediment.isComu() && permis.getOrgan() != null && !permis.getOrgan().isEmpty() && !entitat.getDir3Codi().equals(permis.getOrgan())) {
				ProcedimentOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcedimentIdAndOrganGestorCodi(procediment.getId(), permis.getOrgan());
				if (procedimentOrgan == null) {
					OrganGestorEntity organGestor = organGestorRepository.findByCodi(permis.getOrgan());
					if (organGestor == null) {
						organGestor = createOrganGestor(permis.getOrgan(), entitat);
					}
					procedimentOrgan = procedimentOrganRepository.save(ProcedimentOrganEntity.getBuilder(procediment, organGestor).build());
				}
				permisosHelper.updatePermis(
						procedimentOrgan.getId(), 
						ProcedimentOrganEntity.class, 
						permis);
			} else {
				permisosHelper.updatePermis(
						id,
						ProcedimentEntity.class,
						permis);
			}
			cacheHelper.evictFindProcedimentsWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(
			Long entitatId,
			Long organGestorId,
			Long procedimentId,
			String organCodi,
			Long permisId,
			TipusPermis tipus) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació del permis del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "procedimentId=" + procedimentId + ", "
					+ "organCodi=" + organCodi + ", "
					+ "permisId=" + permisId + ")");
			
			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
			
			if (TipusPermis.PROCEDIMENT_ORGAN.equals(tipus)) {
				ProcedimentOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcedimentIdAndOrganGestorCodi(procedimentId, organCodi);
				entityComprovarHelper.comprovarProcediment(entitatId, procedimentOrgan.getProcediment().getId());
				permisosHelper.deletePermis(
						procedimentOrgan.getId(),
						ProcedimentOrganEntity.class,
						permisId);
			} else {
				entityComprovarHelper.comprovarProcediment(entitatId, procedimentId);
				permisosHelper.deletePermis(
						procedimentId,
						ProcedimentEntity.class,
						permisId);
			}
			cacheHelper.evictFindProcedimentsWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// PROCEDIMENT-GRUP
	// ==========================================================
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Transactional(readOnly = true)
	@Override
	public ProcedimentGrupDto grupCreate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + procedimentGrup + ")");
			
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitatId,
					id,
					false,
					false,
					false,
					false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			
			GrupProcedimentEntity grupProcedimentEntity = GrupProcedimentEntity.getBuilder(
					procediment, 
					grup).build();
			
			grupProcedimentEntity = grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentsWithPermis();
			return conversioTipusHelper.convertir(
					grupProcedimentEntity, 
					ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional(readOnly = true)
	@Override
	public ProcedimentGrupDto grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + procedimentGrup + ")");
			
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitatId,
					id,
					false,
					false,
					false,
					false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			
			GrupProcedimentEntity grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(
					procedimentGrup.getId());
			
			grupProcedimentEntity.update(procediment, grup);
			
			grupProcedimentEntity = grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentsWithPermis();
			return conversioTipusHelper.convertir(
					grupProcedimentEntity, 
					ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public ProcedimentGrupDto grupDelete(
			Long entitatId, 
			Long procedimentGrupId) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "procedimentGrupID=" + procedimentGrupId + ")");
			
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			GrupProcedimentEntity grupProcedimentEntity = grupProcedimentRepository.findOne(procedimentGrupId);
			
			grupProcedimentRepository.delete(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentsWithPermis();
			return conversioTipusHelper.convertir(
					grupProcedimentEntity, 
					ProcedimentGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<TipusAssumpteDto> tipusAssumpte = new ArrayList<TipusAssumpteDto>();
		
			try {
				List<TipusAssumpte> tipusAssumpteRegistre = pluginHelper.llistarTipusAssumpte(entitat.getDir3Codi());
				
				if (tipusAssumpteRegistre != null)
					for (TipusAssumpte assumpteRegistre : tipusAssumpteRegistre) {
						TipusAssumpteDto assumpte = new TipusAssumpteDto();
						assumpte.setCodi(assumpteRegistre.getCodi());
						assumpte.setNom(assumpteRegistre.getNom());
						
						tipusAssumpte.add(assumpte);
					}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return tipusAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat,
			String codiTipusAssumpte) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiAssumpteDto> codiAssumpte = new ArrayList<CodiAssumpteDto>();
			try {
				List<CodiAssumpte> tipusAssumpteRegistre = pluginHelper.llistarCodisAssumpte(
						entitat.getDir3Codi(),
						codiTipusAssumpte);
				
				if (tipusAssumpteRegistre != null)
					for (CodiAssumpte assumpteRegistre : tipusAssumpteRegistre) {
						CodiAssumpteDto assumpte = new CodiAssumpteDto();
						assumpte.setCodi(assumpteRegistre.getCodi());
						assumpte.setNom(assumpteRegistre.getNom());
						assumpte.setTipusAssumpte(assumpteRegistre.getTipusAssumpte());
						
						codiAssumpte.add(assumpte);
					}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte del tipus d'assumpte: " + codiTipusAssumpte;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return codiAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public void refrescarCache(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Preparant per buidar la informació en cache dels procediments...");
			
//			cacheHelper.evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
//			cacheHelper.evictFindByPermisProcedimentsUsuariActual(entitat.getId());
//			cacheHelper.evictFindPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
//			cacheHelper.evictFindOrganismesByEntitat(entitat.getDir3Codi());
//			cacheHelper.evictFindOrganigramaByEntitat(entitat.getDir3Codi());
			cacheHelper.evictFindProcedimentsWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
