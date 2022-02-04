package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.util.*;

/**
 * Implementació del servei de gestió de òrgans gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class OrganGestorServiceImpl implements OrganGestorService{

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Resource
	private EnviamentTableRepository enviamentTableRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private GrupRepository grupReposity;
	@Resource
	private PagadorPostalRepository pagadorPostalReposity;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Resource
	private PermisosCacheable permisosCacheable;
	@Resource
	private ProcSerCacheable procedimentsCacheable;
	@Resource
	private ConfigHelper configHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;

	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<String, ProgresActualitzacioDto>();

	@Getter
	private List<OrganGestorDto> organsList;

	@Override
	@Transactional
	public OrganGestorDto create(OrganGestorDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i
			//		que l'Organ que crea es fill d'almenys un dels Organs que administra 
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					dto.getEntitatId());

			OrganGestorEstatEnum estat = dto.getEstat() != null ? dto.getEstat() :
					OrganGestorEstatEnum.VIGENT;
			OrganGestorEntity.OrganGestorEntityBuilder organGestorBuilder = OrganGestorEntity.builder(
					dto.getCodi(),
					dto.getNom(),
					entitat,
					dto.getLlibre(),
					dto.getLlibreNom(),
					dto.getOficina() != null ? dto.getOficina().getCodi() : null,
					dto.getOficina() != null ? dto.getOficina().getNom() : null,
					estat);
			if (dto.isEntregaCieActiva()) {
				EntregaCieEntity entregaCie = new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId());
				organGestorBuilder.entregaCie(entregaCieRepository.save(entregaCie));
			}
			return conversioTipusHelper.convertir(
					organGestorRepository.save(organGestorBuilder.build()),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public OrganGestorDto delete(
			Long entitatId,
			Long organId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i
			//		verificar que almenys un dels organs que administra es pare del que vol eliminar.
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId);
//					true,
//					false,
//					false);
			
			OrganGestorEntity organGestorEntity = entityComprovarHelper.comprovarOrganGestor(
					entitat, 
					organId);

			// Eliminar permisos de l'òrgan
			permisosHelper.deleteAcl(
					organId,
					OrganGestorEntity.class);
			// Eliminar organ
			organGestorRepository.delete(organGestorEntity);

			return conversioTipusHelper.convertir(
					organGestorEntity, 
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public OrganGestorDto update(OrganGestorDto dto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(
					dto.getEntitatId(), 
					false, 
					true, 
					false);
			
			OrganGestorEntity organGestor = organGestorRepository.findOne(dto.getId());
			organGestor.updateOficina(
					dto.getOficina().getCodi(),
					dto.getOficina().getNom());

			EntregaCieEntity entregaCie = organGestor.getEntregaCie();
			if (dto.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(
							new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId())
					);
				} else {
					entregaCie.update(dto.getCieId(), dto.getOperadorPostalId());
				}
			}

			organGestor.updateEntregaCie(dto.isEntregaCieActiva() ? entregaCie : null);
			if (!dto.isEntregaCieActiva() && entregaCie != null) {
				entregaCieRepository.delete(entregaCie);
			}

			return conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean organGestorEnUs(Long organId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			boolean organEnUs=false;
			//1) Si té permisos
			// #571 --> Si té permisos els esborram!
//			organEnUs=permisosHelper.hasAnyPermis(organId,OrganGestorEntity.class);
//			if (!organEnUs) {
				//2) Revisam si té procediments assignats
				List<ProcedimentEntity> procedimentsOrganGestor = procedimentRepository.findByOrganGestorId(organId);
				organEnUs=procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty();
				if (!organEnUs) {
					//3) revisar si té grups
					List<GrupEntity> grupsByOrganGestor = grupReposity.findByOrganGestorId(organId);
					organEnUs=grupsByOrganGestor != null && !grupsByOrganGestor.isEmpty();
					
					if (!organEnUs) {
						//4) revisar si té pagadors cie
						List<PagadorCieEntity> pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestorId(organId);
						organEnUs=pagCiesByOrganGestor != null && !pagCiesByOrganGestor.isEmpty();
						if (!organEnUs) {
							//5) revisar si té pagadors postals
							List<PagadorPostalEntity> pagPostalByOrganGestor = pagadorPostalReposity.findByOrganGestorId(organId);
							organEnUs=pagPostalByOrganGestor != null && !pagPostalByOrganGestor.isEmpty();
						}
					}
				
				}
//			}
			return organEnUs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
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
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
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
	public List<CodiValorEstatDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorEstatDto> organsGestors = new ArrayList<CodiValorEstatDto>();
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			for (OrganGestorEntity organ: organs) {
				organsGestors.add(new CodiValorEstatDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom(), organ.getEstat()));
			}
			return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
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
	public List<OrganGestorDto> findByCodisAndEstat(List<String> codisOrgans, OrganGestorEstatEnum estat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganGestorEntity> organs = new ArrayList<>();
			int chunkSize = 100;
			for (int foo = 0; foo < codisOrgans.size(); foo=foo+chunkSize) {
				int indexFinal = foo + chunkSize;
				indexFinal = indexFinal <= codisOrgans.size() ? indexFinal : codisOrgans.size();
				List<OrganGestorEntity> organsChunk = organGestorRepository.findByEstatAndCodiIn(codisOrgans.subList(foo, indexFinal), estat);
				organs.addAll(organsChunk);
			}
			return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findDescencentsByCodi(
			Long entitatId, 
			String organCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organCodi);
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByCodiIn(organs),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId, 
			String organActualCodiDir3,
			OrganGestorFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("llibreCodiNom", new String[] {"llibre"});
//			mapeigPropietatsOrdenacio.put("entregaCieActiva", new String[] {"entregaCie"}); // causa problemes al paginar
			mapeigPropietatsOrdenacio.put("oficinaNom", new String[] {"entitat.oficina"});
			mapeigPropietatsOrdenacio.put("oficinaCodiNom", new String[] {"oficina"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			Page<OrganGestorEntity> organs = null;
			
			
			//Cas d'Administrador d'Entitat
			//	Tots els organs fills de l'Entitat
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId);
			if (organActualCodiDir3 == null) {
				organs = findAmbFiltrePaginatByAdminEntitat(entitat, filtre, pageable);

			//Cas d'Administrador d'Organ
			//	Només el l'Organ de l'administrador, i els seus fills (tant de primer nivell com següents)
			}else{
				organs = findAmbFiltrePaginatByAdminOrgan(entitat, organActualCodiDir3, filtre, pageable);
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
	private Page<OrganGestorEntity> findAmbFiltrePaginatByAdminEntitat(EntitatEntity entitat, OrganGestorFiltreDto filtre,
																Pageable pageable) {
		logger.debug("Consulta taula òrgans gestors per administrador d'entitat");
		if (filtre == null) {
			return organGestorRepository.findByEntitat(
					entitat,
					pageable);
		} else {
			OrganGestorEstatEnum estat = filtre.getEstat();
			boolean isEstatNull = estat == null;
			return organGestorRepository.findByEntitatAndFiltre(
					entitat,
					filtre.getCodi() == null || filtre.getCodi().isEmpty(),
					filtre.getCodi() == null ? "" : filtre.getCodi(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom() == null ? "" : filtre.getNom(),
					filtre.getOficina() == null || filtre.getOficina().isEmpty(),
					filtre.getOficina() == null ? "" : filtre.getOficina(),
					isEstatNull,
					estat,
					filtre.isEntregaCieActiva(),
					pageable);
		}
	}
	private Page<OrganGestorEntity> findAmbFiltrePaginatByAdminOrgan(EntitatEntity entitat,
																	 String organActualCodiDir3,
																	 OrganGestorFiltreDto filtre,
																	   Pageable pageable) {
		logger.debug("Consulta taula òrgans gestors per administrador d'òrgan");
		//Comprovació permisos organ
		entityComprovarHelper.comprovarPermisosOrganGestor(organActualCodiDir3);
		//OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat,organActualId);
		List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
		if (filtre == null) {
			return organGestorRepository.findByEntitatAndOrganGestor(
					entitat,
					organGestorsListCodisDir3,
					pageable);
		} else {
			OrganGestorEstatEnum estat = filtre.getEstat();
			boolean isEstatNull = estat == null;
			return organGestorRepository.findByEntitatAndOrganGestorAndFiltre(
					entitat,
					organGestorsListCodisDir3,
					filtre.getCodi() == null || filtre.getCodi().isEmpty(),
					filtre.getCodi() == null ? "" : filtre.getCodi(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom() == null ? "" : filtre.getNom(),
					filtre.getOficina() == null || filtre.getOficina().isEmpty(),
					filtre.getOficina() == null ? "" : filtre.getOficina(),
					isEstatNull,
					estat,
					pageable);
		}
	}
	
	@Transactional
	@Override
	public void updateOne(Long entitatId, String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId); 
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			if (!updateNom(entitat, organGestor)) {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						"No s'ha pogut obtenir la denominació de l'organ gestor");
			}
			if (!updateLlibre(entitat, organGestor)) {
				logger.debug(String.format(
						"No s'ha pogut actualitzar el llibre de l'òrgan gestor %s, segurament l'òrgan no estigui donat d'alta al registre",
						organGestorCodi));
			}

			Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			if (!updateOficina(entitat, organGestor, arbreUnitats)) {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						"No s'ha pogut obtenir l'oficina de l'organ gestor");
			}
			if (!updateEstat(organGestor, arbreUnitats)) {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						"No s'ha pogut obtenir l'estat de l'organ gestor");
			}

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingOrgans(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			ProgresActualitzacioDto progres = progresActualitzacio.get(dir3Codi);
			if (progres != null && progres.isFinished()) {
				progresActualitzacio.remove(dir3Codi);
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void updateAll(Long entitatId, String organActualCodiDir3) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("Actualitzant noms dels òrgans gestors");

			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			// Comprova si hi ha una altre instància del procés en execució
			ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getDir3Codi());
			if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
				logger.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
				return;	// Ja existeix un altre procés que està executant l'actualització.
			}

			// inicialitza el seguiment del progrés d'actualització
			progres = new ProgresActualitzacioDto();
			progresActualitzacio.put(entitat.getDir3Codi(), progres);

			List<OrganGestorEntity> organsGestors;
			if (organActualCodiDir3 == null) {
				organsGestors = organGestorRepository.findByEntitat(entitat);
			} else {
				List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
				organsGestors = organGestorRepository.findByEntitatAndOrgansGestors(entitat, organGestorsListCodisDir3);
			}

			progres.setNumProcediments(organsGestors.size());
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzar.titol" ));

//			organGestorRepository.updateAllStatus(OrganGestorEstatEnum.ALTRES);
			Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			for(OrganGestorEntity organGestor: organsGestors) {

				progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO,
						messageHelper.getMessage("organgestor.actualitzacio.organ.actual") + " " + organGestor.getCodi());
				boolean status = updateNom(entitat, organGestor);
				logger.info("Fi - updateNom del òrgan gestor: " + organGestor);
				if (!status) {
					logger.error(String.format("Actualització òrgan (%s): Error actualitzant nom ", organGestor.getCodi()));
					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.nom"));
				}
				status = updateLlibre(entitat, organGestor);
				logger.info("Fi - updateLlibre del òrgan gestor: " + organGestor);
				if (!status) {
					logger.error(String.format("Actualització òrgan (%s): Error actualitzant llibre ", organGestor.getCodi()));
					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.llibre"));
				}

				status = updateOficina(entitat, organGestor, arbreUnitats);
				logger.info("Fi - updateOficina del òrgan gestor: " + organGestor);
				if (!status) {
					logger.error(String.format("Actualització òrgan (%s): Error actualitzant oficina ", organGestor.getCodi()));
					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.oficina"));
				}

				status = updateEstat(organGestor, arbreUnitats);
				logger.info("Fi - updateEstat del òrgan gestor: " + organGestor);
				if (!status) {
					logger.error(String.format("Actualització òrgan (%s): Error actualitzant estat ", organGestor.getCodi()));
					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.estat"));
				}
				organGestorRepository.saveAndFlush(organGestor);
				progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organs.ok"));
				progres.incrementProcedimentsActualitzats();
			}
			progres.setProgres(100);
			progres.setFinished(true);
			logger.info("Antes de Update de las tablas correspondientes a las datatables");
			// Update de las tablas correspondientes a las datatables de notificaciones y envíos
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.actualitzar.notificacions.enviaments"));
			notificacioTableViewRepository.updateOrganGestorEstat();
			enviamentTableRepository.updateOrganGestorEstat();
			progres.addInfo(ProgresActualitzacioDto.TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzacio.organ.proces.complet"));

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private boolean updateNom(EntitatEntity entitat, OrganGestorEntity organGestor)
	{
		try {
			String denominacio = cacheHelper.findDenominacioOrganisme(organGestor.getCodi());
			if (denominacio != null && !denominacio.isEmpty())
				organGestor.update(denominacio);
			else
				return false;
		} catch (Exception e) {
			logger.error(String.format("La denominacio de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateLlibre(EntitatEntity entitat, OrganGestorEntity organGestor)
	{
		try {
			LlibreDto llibreOrgan = cacheHelper.getLlibreOrganGestor(
					entitat.getDir3Codi(),
					organGestor.getCodi());
			if (llibreOrgan != null)
				organGestor.updateLlibre(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
			else
				return false;
		} catch (Exception e) {
			logger.error(String.format("El llibre de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateOficina(EntitatEntity entitat,
							   OrganGestorEntity organGestor,
							   Map<String, NodeDir3> arbreUnitats) {
		try {
			// Oficina SIR òrgan gestor
			List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
					arbreUnitats,
					organGestor.getCodi());
			if (oficinesSIR != null && !oficinesSIR.isEmpty())
				organGestor.updateOficina(oficinesSIR.get(0).getCodi(), oficinesSIR.get(0).getNom());
			else
				logger.debug(String.format("L'òrgan gestor %s no disposa de cap oficina", organGestor.getCodi()));
		} catch (Exception e) {
			logger.error(String.format("L'oficina de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateEstat(OrganGestorEntity organGestor,
								Map<String, NodeDir3> arbreUnitats) {
		logger.info("Ini - updateEstat del òrgan gestor: " + organGestor.getCodi() + "-" + organGestor.getNom());
		if (!arbreUnitats.containsKey(organGestor.getCodi())) {
			logger.trace(String.format("Organ Gestor (%s) no trobat a l'organigrama", organGestor.getCodi()));
			organGestor.updateEstat(OrganGestorEstatEnum.ALTRES);
			return true;
		}
		NodeDir3 nodeOrgan = arbreUnitats.get(organGestor.getCodi());
		organGestor.updateEstat(organGestorHelper.getEstatOrgan(nodeOrgan));

		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "id=" + id + ")");
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true, 
//						false, 
//						false);
	
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
	public OrganGestorDto findByCodi(
			Long entitatId,
			String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codi + ")");
//			EntitatEntity entitat = null;
				
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
//			if (entitatId != null)
//				entitat = entityComprovarHelper.comprovarEntitat(
//						entitatId); 
//						true, 
//						false, 
//						false);
	
//			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
//					entitat, 
//					codi);
			
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(codi);
			
			if (organGestor!=null) {
					OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
					return resposta;
			}else
				return null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosCacheable.findOrgansGestorsAccessiblesUsuari(auth);
	}

	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (entitatId == null || id == null) {
				return new ArrayList<>();
			}
			logger.debug("Consulta dels permisos de l'organ gestor (entitatId=" + entitatId +  ", id=" + id +  ")");
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			return  permisosHelper.findPermisos(id, OrganGestorEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id,  PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id +  ")"); 
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			List<PermisDto> permisos =  permisosHelper.findPermisos(id, OrganGestorEntity.class);
			permisosHelper.ordenarPermisos(paginacioParams, permisos);
			return permisos;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			boolean isAdminOrgan,
			PermisDto permisDto) throws ValidationException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permisDto + ")");
			
			if (TipusEnumDto.ROL.equals(permisDto.getTipus())) {
				if (permisDto.getPrincipal().equalsIgnoreCase("tothom")) {
					permisDto.setPrincipal(permisDto.getPrincipal().toLowerCase());					
				} else {
					permisDto.setPrincipal(permisDto.getPrincipal().toUpperCase());
				}
			} else {
				if (TipusEnumDto.USUARI.equals(permisDto.getTipus())) {
					permisDto.setPrincipal(permisDto.getPrincipal().toLowerCase());
				}
			}
			
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			
			PermisDto permis = permisosHelper.findPermis(
					id,
					OrganGestorEntity.class,
					permisDto.getId());	
			if (permis != null && isAdminOrgan && ((permis.getId() == null && permis.isAdministrador()) ||
					(permis.getId() != null && (permis.isAdministrador() != permisDto.isAdministrador())))) {
				throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
			}
			
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permisDto);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(
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
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(
					id,
					OrganGestorEntity.class,
					permisId);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
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
				organismes = organGestorCachable.findOrganismesByEntitat(entitat.getDir3Codi());
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
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = organigramaHelper.getOrganismesFillsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi() + " i òrgan gestor: " + organGestor.getCodi();
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
	@Transactional(readOnly = true)
	public LlibreDto getLlibreOrganisme(
			Long entitatId,
			String organGestorDir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			LlibreDto llibre = new LlibreDto();
			try {
				//Recupera el llibre de l'òrgan gestor especificat (organisme)
				llibre = cacheHelper.getLlibreOrganGestor(
						entitat.getDir3Codi(),
						organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'òrgan gestor: " + organGestorDir3Codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return llibre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findOrgansGestorsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true,
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			return conversioTipusHelper.convertirList(
					permisosCacheable.findOrgansGestorsWithPermis(entitat, auth, permisos),
					OrganGestorDto.class);

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
	@Transactional(readOnly = true)
    public List<CodiValorEstatDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValorEstatDto> organsGestors = new ArrayList<>();
			List<OrganGestorEntity> organsGestorsDisponibles = new ArrayList<>();

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			if (RolEnumDto.NOT_SUPER.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findAll();
			} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findByEntitat(entitat);
			} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
				List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organ);
				organsGestorsDisponibles = organGestorRepository.findByCodiIn(organs);
			} else if (RolEnumDto.tothom.equals(rol)) {
				organsGestorsDisponibles = recuperarOrgansPerProcedimentAmbPermis(
						usuari,
						entitat,
						PermisEnum.CONSULTA);
			}
			Collections.sort(organsGestorsDisponibles, new Comparator<OrganGestorEntity>() {
				@Override
				public int compare(OrganGestorEntity p1, OrganGestorEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});

			for (OrganGestorEntity organGestor : organsGestorsDisponibles) {
				String nom = organGestor.getCodi();
				if (organGestor.getNom() != null && !organGestor.getNom().isEmpty()) {
					nom += " - " + organGestor.getNom();
				}
				organsGestors.add(new CodiValorEstatDto(organGestor.getId().toString(), nom, organGestor.getEstat()));
			}
//		// Eliminam l'òrgan gestor entitat  --> Per ara el mantenim, ja que hi ha notificacions realitzades a l'entitat
//		OrganGestorDto organEntitat = organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi());
//		organsGestorsDisponibles.remove(organEntitat);
        	return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

	@Override
	public List<OrganGestorDto> getOrgansAsList() {
		return organsList;
	}

	@Override
	public Arbre<OrganGestorDto> generarArbreOrgans(String codiEntitat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Map<String, NodeDir3> organs = cacheHelper.findOrganigramaNodeByEntitat(codiEntitat);
			List<NodeDir3> nodes = new ArrayList<>(organs.values());
			organsList = new ArrayList<>();
//			for (NodeDir3 node : nodes) {
//				organsList.add(conversioTipusHelper.convertir(node, OrganGestorDto.class));
//			}
			Arbre<OrganGestorDto> arbre = new Arbre<>(true);
			ArbreNode<OrganGestorDto> arrel = new ArbreNode<>(null, conversioTipusHelper.convertir(organs.get(codiEntitat), OrganGestorDto.class));
			arbre.setArrel(arrel);
			arrel.setFills(generarFillsArbre(organs, arrel, codiEntitat));
			return arbre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<ArbreNode<OrganGestorDto>> generarFillsArbre(Map<String, NodeDir3> organs, ArbreNode<OrganGestorDto> pare, String codiEntitat) {

		NodeDir3 organ = organs.get(codiEntitat);
		List<NodeDir3> fills = organ.getFills();
		List<ArbreNode<OrganGestorDto>> nodes = new ArrayList<>();
		if (fills == null || fills.isEmpty()) {
			organsList.add(conversioTipusHelper.convertir(organ, OrganGestorDto.class));
			return nodes;
		}

		for (int foo = 0; foo < fills.size(); foo++) {
			NodeDir3 node = organs.get(fills.get(foo).getCodi());
			ArbreNode<OrganGestorDto> actual = new ArbreNode<>(pare, conversioTipusHelper.convertir(node, OrganGestorDto.class));
			List<ArbreNode<OrganGestorDto>> nets = generarFillsArbre(organs, actual, node.getCodi());
			actual.setFills(nets);
			nodes.add(actual);
		}
		return nodes;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OficinaDto> getOficinesSIR(
			Long entitatId,
			String dir3codi,
			boolean isFiltre) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			List<OficinaDto> oficines = new ArrayList<OficinaDto>();
			try {
				if (!isFiltre) {
					Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
					oficines = cacheHelper.getOficinesSIRUnitat(
							arbreUnitats,
							dir3codi);
				} else {
					oficines = cacheHelper.getOficinesSIREntitat(dir3codi);
				}
	 		} catch (Exception e) {
	 			String errorMessage = "No s'han pogut recuperar les oficines SIR [dir3codi=" + dir3codi + "]";
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return oficines;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}




	private List<OrganGestorEntity> recuperarOrgansPerProcedimentAmbPermis(
			String usuari,
			EntitatEntity entitat,
			PermisEnum permis) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
		List<ProcSerEntity> procedimentsDisponibles = procedimentsCacheable.getProcedimentsWithPermis(
				usuari,
				entitat,
				permisos);
		List<ProcSerOrganEntity> procedimentsOrgansDisponibles = procedimentsCacheable.getProcedimentOrganWithPermis(
				auth,
				entitat,
				permisos);

		procedimentsDisponibles = mergeProcedimentsWithProcedimentsOrgans(procedimentsDisponibles, procedimentsOrgansDisponibles);

		List<OrganGestorEntity> organsGestorsProcediments = new ArrayList<>();
		List<Long> procedimentsDisponiblesIds = new ArrayList<>();
		for (ProcSerEntity pro : procedimentsDisponibles)
			procedimentsDisponiblesIds.add(pro.getId());

		// 1-recuperam els òrgans dels procediments disponibles (amb permís)
		if (!procedimentsDisponiblesIds.isEmpty())
			organsGestorsProcediments = organGestorRepository.findByProcedimentIds(procedimentsDisponiblesIds);
		// 2-recuperam els òrgans amb permís
		List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.getOrgansGestorsWithPermis(
				usuari,
				auth,
				entitat,
				permisos);
		// 3-juntam tots els òrgans i ordenam per nom
		List<OrganGestorEntity> organsGestors;
		Set<OrganGestorEntity> setOrgansGestors = new HashSet<>(organsGestorsProcediments);
		setOrgansGestors.addAll(organsGestorsAmbPermis);
		if (procedimentsOrgansDisponibles != null) {
			for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setOrgansGestors.add(procedimentOrgan.getOrganGestor());
			}
		}
		organsGestors = new ArrayList<>(setOrgansGestors);
		if (!configHelper.getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes")) {
			organsGestors.remove(organGestorRepository.findByCodi(entitat.getDir3Codi()));
		}
		if (procedimentsDisponibles.isEmpty() && organsGestors.isEmpty())
			throw new NoPermisosException("Usuari sense permios assignats");
		return organsGestors;
	}

	private List<ProcSerEntity> mergeProcedimentsWithProcedimentsOrgans(
			List<ProcSerEntity> procedimentsDisponibles,
			List<ProcSerOrganEntity> procedimentsOrgansDisponibles) {
		if (procedimentsOrgansDisponibles != null && !procedimentsOrgansDisponibles.isEmpty()) {
			// Empleam un set per no afegir duplicats
			Set<ProcSerEntity> setProcediments = new HashSet<>(procedimentsDisponibles);
			for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setProcediments.add(procedimentOrgan.getProcSer());
			}
			procedimentsDisponibles = new ArrayList<ProcSerEntity>(setProcediments);
		}
		return procedimentsDisponibles;
	}

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorServiceImpl.class);

}
