package es.caib.notib.core.service;

import java.util.ArrayList;
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

import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
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
import es.caib.notib.core.api.dto.TipusAssumpteDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.SistemaExternException;
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
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcedimentHelper;
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
	private MetricsHelper metricsHelper;
	
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
