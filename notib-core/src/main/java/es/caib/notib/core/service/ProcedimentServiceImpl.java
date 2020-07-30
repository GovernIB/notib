package es.caib.notib.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.ActualitzacioInfo;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.dto.TipusAssumpteDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentFormEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcedimentHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentFormRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.TipusAssumpte;

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
	private GrupRepository grupRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private GrupProcedimentRepository grupProcedimentRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	MessageHelper messageHelper;
	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<String, ProgresActualitzacioDto>();
	
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
				organGestor = OrganGestorEntity.getBuilder(
						procediment.getOrganGestor(),
						findDenominacioOrganisme(procediment.getOrganGestor()),
						entitat).build();
				organGestorRepository.save(organGestor);
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
							procediment.getLlibre(),
							procediment.getLlibreNom(),
							procediment.getOficina(),
							procediment.getOficinaNom(),
							organGestor,
							procediment.getTipusAssumpte(),
							procediment.getTipusAssumpteNom(),
							procediment.getCodiAssumpte(),
							procediment.getCodiAssumpteNom()).build());
			
			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public ProcedimentDto update(
			Long entitatId,
			ProcedimentDto procediment,
			boolean isAdmin) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant procediment ("
					+ "procediment=" + procediment + ")");
			
			
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
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor()); 
			if (organGestor == null) {
				organGestor = OrganGestorEntity.getBuilder(
						procediment.getOrganGestor(),
						findDenominacioOrganisme(procediment.getOrganGestor()),
						entitat).build();
				organGestorRepository.save(organGestor);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
			OrganGestorEntity organGestorAntic = null;
			if (!procedimentEntity.getOrganGestor().getCodi().equals(procediment.getOrganGestor())) {
				organGestorAntic = procedimentEntity.getOrganGestor();
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
						procediment.getLlibre(),
						procediment.getLlibreNom(),
						procediment.getOficina(),
						procediment.getOficinaNom(),
						organGestor,
						procediment.getTipusAssumpte(),
						procediment.getTipusAssumpteNom(),
						procediment.getCodiAssumpte(),
						procediment.getCodiAssumpteNom());
		
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
	
	@Override
	@Transactional(timeout = 600)
	public void actualitzaProcediments(EntitatDto entitatDto) {
		
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
//			logger.debug(">>>> Inici actualitzar procediments");
//			logger.debug(">>>> ==========================================================================");
			if (progres != null && progres.getProgres() < 100 && !progres.isError()) {
//				logger.debug(">>>> Ja existeix un altre procés que està executant l'actualització");
//				logger.debug(">>>> ==========================================================================");
				
				return;	// Ja existeix un altre procés que està executant l'actualització.
			} else {
				progres = new ProgresActualitzacioDto();
				progresActualitzacio.put(entitatDto.getDir3Codi(), progres);
			}
			progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("procediment.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));
		
			Long t1 = System.currentTimeMillis();
//			logger.debug(">>>> Obtenir procediments de Rolsac de l'entitat...");
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments"));
		
			Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(entitatDto.getDir3Codi());
			List<ProcedimentDto> procedimentsGda = getProcedimentsGdaByEntitat(entitatDto.getDir3Codi());
			
			Long t2 = System.currentTimeMillis();
//			logger.debug(">>>> obtinguts " + procedimentsGda.size() + " procediments (" + (t2 - t1) + "ms)");
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.result", new Object[] {procedimentsGda.size()}));
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
			progres.setNumProcediments(procedimentsGda.size());
			
			List<OrganGestorEntity> organsGestorsModificats = new ArrayList<OrganGestorEntity>();
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatDto.getId(),
					false,
					false,
					false);
			
//			logger.debug(">>>> ==========================================================================");
//			logger.debug(">>>> Processar procediments");
//			logger.debug(">>>> ==========================================================================");
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediments"));
			
			int i = 1;
			for (ProcedimentDto procedimentGda: procedimentsGda) {
				t1 = System.currentTimeMillis();
//				logger.debug(">>>> " + i + ". Processant procediment: " + procedimentGda.getNom());
//				logger.debug(">>>> ..........................................................................");
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment", new Object[] {i++, procedimentGda.getNom()}));
				
				if (procedimentGda.getCodi() == null || procedimentGda.getCodi().isEmpty()) {
//					logger.debug(">>>> Procediment DESCARTAT: No disposa de Codi SIA");
//					logger.debug(">>>> ..........................................................................");
//					logger.debug(">>>> ..........................................................................");
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat"));
					progres.addSeparador();
					progres.incrementProcedimentsActualitzats();
					continue;
				}
				// Organ gestor
//				logger.debug(">>>> >> Comprovant Organ gestor. Codi: " + procedimentGda.getOrganGestor() +  "...");
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ", new Object[] {procedimentGda.getOrganGestor()}));
				
				OrganGestorEntity organGestor = organGestorRepository.findByCodi(procedimentGda.getOrganGestor());
//				logger.debug(">>>> >> organ gestor " + (organGestor == null ? "NOU" : "EXISTENT"));
				
				if (organGestor == null) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result.no"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.crear", new Object[] {procedimentGda.getOrganGestor()}));

					organGestor = OrganGestorEntity.getBuilder(
							procedimentGda.getOrganGestor(),
							organigramaEntitat.get(procedimentGda.getOrganGestor()).getNom(),
							entitat).build();
					organGestorRepository.save(organGestor);
					
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.creat"));
				} else {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result.si"));
				}
	
//				logger.debug(">>>> >> Comprovant Procediment. Codi SIA: " + procedimentGda.getCodi() +  "...");
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment", new Object[] {procedimentGda.getCodi()}));
				
				ProcedimentEntity procediment = procedimentRepository.findByCodi(procedimentGda.getCodi());
				
				if (procediment == null) {
//					logger.debug(">>>> >> procediment NOU ...");
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.no"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.crear", new Object[] {procedimentGda.getCodi()}));
					
					// CREATE
					procedimentRepository.save(
							ProcedimentEntity.getBuilder(
									procedimentGda.getCodi(),
									procedimentGda.getNom(),
									Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "0")),
									Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "10")),
									entitat,
									null,
									null,
									false,
									null,
									null,
									null,
									null,
									organGestor,
									null,
									null,
									null,
									null).build());
					
//					logger.debug(">>>> >> Creat.");
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.creat"));
					
				} else { 
//					logger.debug(">>>> >> Procediment EXISTENT ...");
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.si"));
					
					if (modificar) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat"));
	
						if (!entitat.equals(procediment.getEntitat())) {
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.no", new Object[] {procediment.getEntitat().getNom()}));
							progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.revisar", new Object[] {procedimentGda.getCodi(), procediment.getEntitat().getNom()}));
							throw new ValidationException(
									procedimentGda.getId(),
									ProcedimentEntity.class,
									"El procediment '" + procediment.getNom() + "'  no pertany a la entitat actual (id=" + entitat.getId() + ") ");
						}
						
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.si"));
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update"));
						
						// UPDATE
						if (!procediment.getOrganGestor().getCodi().equals(procedimentGda.getOrganGestor()) ||
							!procediment.getNom().equals(procedimentGda.getNom())) {
							// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
							if (!procediment.getOrganGestor().getCodi().equals(procedimentGda.getOrganGestor())) {
								progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.organ"));
								organsGestorsModificats.add(procediment.getOrganGestor());
							}
							if (!procediment.getNom().equals(procedimentGda.getNom())) {
								progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.nom"));
							}
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.si"));
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updating", new Object[] {procedimentGda.getCodi()}));
							
							procediment.update(
									procedimentGda.getNom(),
									organGestor);
							
							t2 = System.currentTimeMillis();
//							logger.debug(">>>> >> Modificat (" + (t2 - t1) + "ms)");
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updated"));
							
						} else {
//							logger.debug(">>>> >> NO es necessari realitzar cap modificació.");
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.no"));
						}
					} else {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.modificar.inactiu"));
					}
				}
				t2 = System.currentTimeMillis();
//				logger.debug(">>>> ..........................................................................");
//				logger.debug(">>>> ..........................................................................");
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.result", new Object[] {procedimentGda.getNom()}));
				progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
				progres.addSeparador();
				progres.incrementProcedimentsActualitzats();
			}
			
			if (eliminarOrgans) {
				i = 1;
//				logger.debug(">>>> Processant organs gestors modificats (" + organsGestorsModificats.size() + ")");
//				logger.debug(">>>> ==========================================================================");
				progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs"));
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.check"));
				if (organsGestorsModificats.isEmpty()) {
	//				logger.debug(">>>> No hi ha organs gestors a modificar");
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.no"));
				} else {
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.si", new Object[] {organsGestorsModificats.size()}));
				}
				
				for (OrganGestorEntity organGestorAntic: organsGestorsModificats) {
//					logger.debug(">>>> Processant organ gestor " + organGestorAntic.getCodi() + "...   ");
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ", new Object[] {organGestorAntic.getCodi()}));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us"));
					// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (2)
					List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestorAntic.getId());
					if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.no"));
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis"));
						 if (!permisosHelper.hasAnyPermis(
								 organGestorAntic.getId(),
								 OrganGestorEntity.class)) {
							 progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.no"));
							 progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.borrar"));
							 progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrar", new Object[] {organGestorAntic.getCodi()}));
							 
							 organGestorRepository.delete(organGestorAntic);
							 
							 progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrat"));
//							 logger.debug(">>>> ELIMINAT: No té cap procediment ni permís assignat.");
						 } else {
							 progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.si"));
							 progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.permis"));
//							 logger.debug(">>>> NO ELIMINAT: Té permisos configurats.");
						 }
					} else {
//						logger.debug(">>>> NO ELIMINAT: Té altres procediments assignats.");
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.si"));
						progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.procediments"));
					}
//					logger.debug(">>>> ..........................................................................");
					progres.addSeparador();
				}
			} else {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.inactiu"));
			}
			
//			logger.debug(">>>> ==========================================================================");
//			logger.debug(">>>> Fi actualitzar procediments");
//			logger.debug(">>>> ==========================================================================");
			Long tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
			
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
	}
	
	private List<ProcedimentDto> getProcedimentsGdaByEntitat(String codiDir3) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(codiDir3);
		
//		logger.debug(">>>> >> Obtenir tots els procediments de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();
		
		List<ProcedimentDto> procedimentsGda = pluginHelper.getProcedimentsGda();
		
		Long t2 = System.currentTimeMillis();
//		logger.debug(">>>> >> obtinguts" + procedimentsGda.size() + " procediments (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm.result", new Object[] {procedimentsGda.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		
//		logger.debug(">>>> >> Obtenir totes els organs gestors de l'entitat...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.organs"));
		
		List<String> unitatsEntitat = getUnitatsSuccessores(codiDir3);
		
		t1 = System.currentTimeMillis();
//		logger.debug(">>>> >> obtinguts" + unitatsEntitat.size() + " organs (" + (t1 - t2) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.organs.result", new Object[] {unitatsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t1 - t2)}));
		
		List<ProcedimentDto> procedimentsEntitat = new ArrayList<ProcedimentDto>();
		
//		logger.debug(">>>> >> Filtrar procediments per entitat...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.filtre.procediments"));
		
		for (ProcedimentDto procediment: procedimentsGda) {
			if (unitatsEntitat.contains(procediment.getOrganGestor())) {
				procedimentsEntitat.add(procediment);
//				logger.debug(">>>> >> >> Procediment " + procediment.getCodi() + ". Organ gestor: " + procediment.getOrganGestor() + " - Pertany a l'entitat actual.");
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.filtre.procediment.si", new Object[] {procediment.getCodi(), procediment.getOrganGestor()}));
			} else {
//				logger.debug(">>>> >> >> Procediment " + procediment.getCodi() + ". Organ gestor: " + procediment.getOrganGestor() + " - NO pertany a l'entitat actual.");
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.filtre.procediment.no", new Object[] {procediment.getCodi(), procediment.getOrganGestor()}));
			}
		}
		
		t2 = System.currentTimeMillis();
//		logger.debug(">>>> >>  Obtinguts " + procedimentsEntitat.size() + " procediments pertanyents a l'entitat (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.filtre.procediments.result", new Object[] {procedimentsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		return procedimentsEntitat;
	}
	
	private List<String> getUnitatsSuccessores(String codiDir3) {
		Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiDir3);
		
		List<String> unitatsEntitat = new ArrayList<String>();
		unitatsEntitat.addAll(getUnitatsFilles(organigramaEntitat, codiDir3));
		return unitatsEntitat;
	}

	private List<String> getUnitatsFilles(
			Map<String, OrganismeDto> organigrama,
			String codiDir3) {
		List<String> unitats = new ArrayList<String>();
		unitats.add(codiDir3);
		OrganismeDto organisme = organigrama.get(codiDir3);
		if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
			for (String fill: organisme.getFills()) {
				unitats.addAll(getUnitatsFilles(organigrama, fill));
			}
		}
		return unitats;
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
			return true;
		}
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(dir3Codi);
		if (progres.getProgres() != null &&  progres.getProgres() >= 100) {
			progresActualitzacio.remove(dir3Codi);
		}
		return progres;
	}
	
	@Override
	@Transactional
	public ProcedimentDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
					entitat, 
					id);
			OrganGestorEntity organGestor = procedimentEntity.getOrganGestor();
		
			//Eliminar procediment
			procedimentRepository.delete(procedimentEntity);
			
			if (organGestor != null) {
				List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestor.getId());
				if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
					organGestorRepository.delete(organGestor);
				}
			}

			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcedimentDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "id=" + id + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null && !isAdministrador)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
	
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitat, 
					id, 
					false, 
					false, 
					false,
					false);
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
	@Transactional
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
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
			
			if (filtre == null) {
				
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatActual(
							entitatActual.getId(),
							paginacioHelper.toSpringDataPageable(paginacioParams));
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				} else if (isAdministrador) {
					procediments = procedimentFormRepository.findAmbEntitatActiva(
							entitatsActivaId,
							paginacioHelper.toSpringDataPageable(paginacioParams));
					procedimentsPage =  paginacioHelper.toPaginaDto(
							procediments,
							ProcedimentFormDto.class);
				}
			} else {
				Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
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
	public List<ProcedimentDto> findProcedimentsSenseGrupsWithPermis(Long entitatId, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsSenseGrupsByEntitat(entitat);
			return entityComprovarHelper.findPermisProcediments(
							procediments, 
							getPermissionFromName(permis));
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
	public List<ProcedimentDto> findProcedimentsAmbGrupsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsAmbGrupsByEntitatAndGrup(entitat, grups);
			return entityComprovarHelper.findPermisProcediments(
							procediments, 
							getPermissionFromName(permis));
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
	public List<ProcedimentDto> findProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups);
			return entityComprovarHelper.findPermisProcediments(
							procediments, 
							getPermissionFromName(permis));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
					getPermissionFromName(permis),
					SecurityContextHolder.getContext().getAuthentication());
			return !procediments.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private Permission[] getPermissionFromName(PermisEnum permis) {
		switch (permis) {
		case CONSULTA: return new Permission[] {ExtendedPermission.READ};
		case PROCESSAR: return new Permission[] {ExtendedPermission.PROCESSAR};
		case NOTIFICACIO: return new Permission[] {ExtendedPermission.NOTIFICACIO};
		case GESTIO: return new Permission[] {ExtendedPermission.ADMINISTRATION};
		default: return null;
		}
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos del meta-expedient ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id +  ")"); 
			EntitatEntity entitat = null;
			
			if (entitatId != null && !isAdministrador)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
			entityComprovarHelper.comprovarProcediment(
					entitat, 
					id, 
					false, 
					false, 
					false, 
					false);
			
			return permisosHelper.findPermisos(
					id,
					ProcedimentEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis,
			boolean isAdministrador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permis + ")");
			
			if (entitatId != null && !isAdministrador)
				entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			entityComprovarHelper.comprovarProcediment(
					null,
					id,
					false,
					false,
					false,
					false);
			permisosHelper.updatePermis(
					id,
					ProcedimentEntity.class,
					permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void grupCreate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + procedimentGrup + ")");
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					null,
					id,
					false,
					false,
					false,
					false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			
			GrupProcedimentEntity grupProcedimentEntity = GrupProcedimentEntity.getBuilder(
					procediment, 
					grup).build();
			
			grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + procedimentGrup + ")");
			ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					null,
					id,
					false,
					false,
					false,
					false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			
			GrupProcedimentEntity grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(
					procedimentGrup.getId());
			
			grupProcedimentEntity.update(procediment, grup);
			
			grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void grupDelete(
			Long entitatId, 
			Long procedimentGrupId) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment ("
					+ "entitatId=" + entitatId +  ", "
					+ "procedimentGrupID=" + procedimentGrupId + ")");
			
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			GrupProcedimentEntity grupProcedimentEntity = grupProcedimentRepository.findOne(procedimentGrupId);
			
			grupProcedimentRepository.delete(grupProcedimentEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	
	@Override
	@Transactional
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId,
			boolean isAdministrador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació del permis del meta-expedient ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permisId=" + permisId + ")");
			EntitatEntity entitat = null;
			
			if (entitatId != null && !isAdministrador)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
			entityComprovarHelper.comprovarProcediment(
					entitat,
					id,
					false,
					false,
					false,
					false);
			permisosHelper.deletePermis(
					id,
					ProcedimentEntity.class,
					permisId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisConsultaProcediment(EntitatDto entitat) {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
					new Permission[] {
							ExtendedPermission.READ},
					entitat.getId()
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisGestioProcediment(
			Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
			ProcedimentEntity procediment = procedimentRepository.findById(procedimentId);
			
			procediments.add(procediment);
			
			List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcediments(
					procediments,
					new Permission[] {
							ExtendedPermission.ADMINISTRATION}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisProcessarProcediment(
			String procedimentCodi,
			Long procedimentId,
			boolean isAdministrador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
			ProcedimentEntity procediment;
			
			if (!isAdministrador) {
					procediment = procedimentRepository.findById(procedimentId);
			} else {
				procediment = procedimentRepository.findOne(procedimentId);
			}
			if (procediment != null)
				procediments.add(procediment);
			
			List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcediments(
					procediments,
					new Permission[] {
							ExtendedPermission.PROCESSAR}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisNotificacioProcediment(EntitatDto entitat) {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
					new Permission[] {
						ExtendedPermission.NOTIFICACIO},
					entitat.getId()
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasGrupPermisConsultaProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
					procediments,
					entitat.getId(),
					new Permission[] {
							ExtendedPermission.READ}
					);
			
			return (resposta.isEmpty()) ? false : true;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasGrupPermisNotificacioProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
					procediments,
					entitat.getId(),
					new Permission[] {
							ExtendedPermission.NOTIFICACIO}
					);
			
			return (resposta.isEmpty()) ? false : true;
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
	

	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = cacheHelper.findOrganismesByEntitat(entitat.getDir3Codi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public String findDenominacioOrganisme(String codiDir3) {
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

	@Override
	@Transactional(readOnly = true)
	public List<OficinaDto> findOficines(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OficinaDto> oficines = new ArrayList<OficinaDto>();
			try {
				List<Oficina> oficinesRegistre = pluginHelper.llistarOficines(
						entitat.getDir3Codi(), 
						AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
				for (Oficina oficinaRegistre : oficinesRegistre) {
					OficinaDto oficina = new OficinaDto();
					oficina.setCodi(oficinaRegistre.getCodi());
					oficina.setNom(oficinaRegistre.getNom());
					oficines.add(oficina);
				}
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar les oficines de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return oficines;	
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<LlibreDto> findLlibres(
			EntitatDto entitat, 
			String oficina) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<LlibreDto> llibres = new ArrayList<LlibreDto>();
			try {
				List<Llibre> llibresRegistre = pluginHelper.llistarLlibres(
						entitat.getDir3Codi(), 
						oficina, 
						AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
				for (Llibre llibreRegistre : llibresRegistre) {
					LlibreDto llibre = new LlibreDto();
					llibre.setCodi(llibreRegistre.getCodi());
					llibre.setNomCurt(llibreRegistre.getNomCurt());
					llibre.setNomLlarg(llibreRegistre.getNomLlarg());
					llibre.setOrganismeCodi(llibreRegistre.getOrganisme());
					llibres.add(llibre);
				}
	 		} catch (Exception e) {
	 			String errorMessage = "No s'han pogut recuperar els llibres de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return llibres;
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
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, organGestorCodi);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByOrganGestorAndGrup(entitat, organGestor.getId(), grups);
			return entityComprovarHelper.findPermisProcediments(
							procediments, 
							getPermissionFromName(permis));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	
	// ORGANS GESTORS
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findOrgansGestorsAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganGestorEntity> organs = organGestorRepository.findAll();
			return conversioTipusHelper.convertirList(
					organs, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findOrgansGestorsByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(
					organs, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CodiValorDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorDto> organsGestors = new ArrayList<CodiValorDto>();
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			for (OrganGestorEntity organ: organs) {
				organsGestors.add(new CodiValorDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom()));
			}
			return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findOrganGestorByProcedimentIds(List<Long> procedimentIds) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByProcedimentIds(procedimentIds), 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(
			Long entitatId, 
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					false);
			
			Page<OrganGestorEntity> organs = null;
			if (filtre == null) {
				organs = organGestorRepository.findByEntitat(
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			} else {
				organs = organGestorRepository.findByEntitatAndFiltre(
						entitat,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
			
			PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(
					organs,
					OrganGestorDto.class);
			
			for (OrganGestorDto organ: paginaOrgans.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(
						organ.getId(),
						OrganGestorEntity.class);
				organ.setPermisos(permisos);
			}
			return paginaOrgans;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void updateOrganGestorNom(Long entitatId, String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			String denominacio = findDenominacioOrganisme(organGestorCodi);
			if (denominacio != null && !denominacio.isEmpty())
				organGestor.update(denominacio);
			else
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS, 
						"No s'ha pogut obtenir la denominació de l'organ gestor");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void updateOrgansGestorsNom(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					false);
			List<OrganGestorEntity> organsGestors = organGestorRepository.findByEntitat(entitat);
			for(OrganGestorEntity organGestor: organsGestors) {
				String denominacio = findDenominacioOrganisme(organGestor.getCodi());
				if (denominacio != null && !denominacio.isEmpty())
					organGestor.update(denominacio);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findOrganGestorById(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "id=" + id + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						true, 
						false);
	
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					id);
			OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findOrganGestorByCodi(
			Long entitatId,
			String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codi + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						true, 
						false);
	
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					codi);
			OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisOrganGestorFind(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id +  ")"); 
			EntitatEntity entitat = null;
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
			entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					id);
			
			return permisosHelper.findPermisos(
					id,
					OrganGestorEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void permisOrganGestorUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permis + ")");
			EntitatEntity entitat = null;
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public void permisOrganGestorDelete(
			Long entitatId,
			Long id,
			Long permisId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permisId=" + permisId + ")");
			EntitatEntity entitat = null;
			
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId,
						false,
						true,
						false);
			
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(
					id,
					OrganGestorEntity.class,
					permisId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Transactional(readOnly = true)
	@Override
	public void refrescarCache(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Preparant per buidar la informació en cache dels procediments...");
			
			cacheHelper.evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
			cacheHelper.evictFindByPermisProcedimentsUsuariActual(entitat.getId());
			cacheHelper.evictFindPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
			cacheHelper.evictFindOrganismesByEntitat(entitat.getDir3Codi());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
