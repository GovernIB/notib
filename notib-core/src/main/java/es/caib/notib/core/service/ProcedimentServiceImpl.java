package es.caib.notib.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.ProcedimentHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;

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
	
	@Override
	public ProcedimentDto create(
			Long entitatId,
			ProcedimentDto procediment) {
		logger.debug("Creant un nou procediment ("
				+ "procediment=" + procediment + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		
		PagadorPostalEntity pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
				procediment.getPagadorpostal().getId());
				
		PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(
				procediment.getPagadorcie().getId());
		
		
		ProcedimentEntity procedimentEntity = procedimentRepository.save(
				ProcedimentEntity.getBuilder(
						procediment.getCodi(),
						procediment.getNom(),
						procediment.getCodisia(),
						procediment.getRetard(),
						entitat,
						pagadorPostal,
						pagadorCie,
						procediment.isAgrupar(),
						procediment.getLlibre(),
						procediment.getOficina(),
						procediment.getTipusAssumpte()).build());
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto update(
			Long entitatId,
			ProcedimentDto procediment,
			boolean isAdmin) throws NotFoundException {
		logger.debug("Actualitzant procediment ("
				+ "procediment=" + procediment + ")");
		
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		ProcedimentEntity procedimentEntity = null;
		if(!isAdmin) {
			procedimentEntity = entityComprovarHelper.comprovarProcediment(
					entitat,
					procediment.getId());
		} else {
			procedimentEntity = procedimentRepository.findOne(procediment.getId());
		}
		
		PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(
				procediment.getPagadorpostal().getId());
		
		PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(
				procediment.getPagadorcie().getId());
		
		List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepository.findByProcediment(procedimentEntity);
		
		if (!procediment.isAgrupar()) {
			grupProcedimentRepository.delete(grupsProcediment);
		}
		
		procedimentEntity.update(
					procediment.getCodi(),
					procediment.getNom(),
					procediment.getCodisia(),
					entitat,
					pagadorPostalEntity,
					pagadorCieEntity,
					procediment.getRetard(),
					procediment.isAgrupar(),
					procediment.getLlibre(),
					procediment.getOficina(),
					procediment.getTipusAssumpte());
		
		procedimentRepository.save(procedimentEntity);
			
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
				entitat, 
				id);
	
		//Eliminar procediment
		procedimentRepository.delete(procedimentEntity);
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
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
			procedimentHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		
		return resposta;
	}

	@Override
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		
		List<ProcedimentEntity> procediment = procedimentRepository.findByEntitat(entitat);
		
		return conversioTipusHelper.convertirList(
				procediment,
				ProcedimentDto.class);
		
	}
	
	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
		List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
		Page<ProcedimentEntity> procediments = null;
		PaginaDto<ProcedimentDto> procedimentsPage = null;
		
		if (filtre == null) {
			
			if (isUsuariEntitat) {
				procediments = procedimentRepository.findByEntitatActual(
						entitatActual,
						paginacioHelper.toSpringDataPageable(paginacioParams));
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentDto.class);
			} else if (isAdministrador) {
				procediments = procedimentRepository.findByEntitatActiva(
						entitatsActiva,
						paginacioHelper.toSpringDataPageable(paginacioParams));
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentDto.class);
			}
		} else {
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			
			if (isUsuariEntitat) {
				procediments = procedimentRepository.findByFiltre(
						entitatActual == null,
						entitatActual,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getCodisia() == null || filtre.getCodisia().isEmpty(),
						filtre.getCodisia() == null ? "" : filtre.getCodisia(),
						pageable);
				
				procedimentsPage = paginacioHelper.toPaginaDto(
						procediments, 
						ProcedimentDto.class);
				
			} else if (isAdministrador) {
				procediments = procedimentRepository.findByFiltre(
						true,
						entitatActual,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						filtre.getCodisia() == null || filtre.getCodisia().isEmpty(),
						filtre.getCodisia() == null ? "" : filtre.getCodisia(),
						pageable);
				
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentDto.class);
			}
		}
		return procedimentsPage;
	}
	
	@Override
	public List<ProcedimentDto> findAll() {
		logger.debug("Consulta de tots els procediments");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
					procedimentRepository.findAll(),
					ProcedimentDto.class);
	}

	@Override
	public List<ProcedimentGrupDto> findAllGrups() {
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
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsSenseGrups() {
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		List<GrupProcedimentEntity> grupsProcediments = grupProcedimentRepository.findAll();
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		
		for (GrupProcedimentEntity grupProcedimentEntity : grupsProcediments) {
			procediments.add(procedimentRepository.findOne(grupProcedimentEntity.getProcediment().getId()));
		}
		List<ProcedimentEntity> procedimentsSenseGrups = new ArrayList<ProcedimentEntity>();
		if(procediments.size() > 0) {
			procedimentsSenseGrups = procedimentRepository.findProcedimentsSenseGrups(procediments);	
		}
		
		return conversioTipusHelper.convertirList(
				procedimentsSenseGrups,
				ProcedimentDto.class);
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
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
	}
	
	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) {
		logger.debug("Modificació del permis del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
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
	}
	
	@Transactional
	@Override
	public void grupCreate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		logger.debug("Modificació del grup del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + procedimentGrup + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
				entitat,
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
	}
	
	@Transactional
	@Override
	public void grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		logger.debug("Modificació del grup del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + procedimentGrup + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
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
		GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
		
		GrupProcedimentEntity grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(
				procedimentGrup.getId());
		
		grupProcedimentEntity.update(procediment, grup);
		
		grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
	}

	@Transactional
	@Override
	public void grupDelete(
			Long entitatId, 
			Long procedimentGrupId) throws NotFoundException {
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
	}

	
	@Override
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) {
		logger.debug("Eliminació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
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
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

	@Override
	public boolean hasPermisConsultaProcediment(EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
						ExtendedPermission.READ},
				entitatActual
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasPermisGestioProcediment(
			Long procedimentId,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		ProcedimentEntity procediment = procedimentRepository.findByIdAndEntitat(procedimentId, entitatActual);
		
		procediments.add(procediment);
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcediments(
				procediments,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasPermisProcessarProcediment(
			String procedimentCodi,
			Long procedimentId,
			EntitatDto entitat,
			boolean isAdministrador) {
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		ProcedimentEntity procediment;
		
		if (!isAdministrador) {
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
				procediment = procedimentRepository.findByIdAndEntitat(procedimentId, entitatActual);
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
	}
	
	@Override
	public boolean hasPermisNotificacioProcediment(EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
					ExtendedPermission.NOTIFICACIO},
				entitatActual
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasGrupPermisConsultaProcediment(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.READ}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasGrupPermisNotificacioProcediment(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.NOTIFICACIO}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}

}
