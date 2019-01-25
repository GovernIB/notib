package es.caib.notib.core.service;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.ProcedimentHelper;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentRepository;

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
	private GrupService grupService;
	@Resource
	private GrupRepository grupRepository;
	
	@Override
	public ProcedimentDto create(ProcedimentDto procediment) {
		logger.debug("Creant un nou procediment ("
				+ "procediment=" + procediment + ")");
		
		ProcedimentEntity procedimentEntity = null;
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				procediment.getEntitat().getId());
		
		PagadorPostalEntity pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
				procediment.getPagadorpostal().getId());
				
		PagadorCieEntity pagadorCie = entityComprovarHelper.comprovarPagadorCie(
				procediment.getPagadorcie().getId());
		
		
		procedimentEntity = procedimentRepository.save(
				ProcedimentEntity.getBuilder(
						procediment.getCodi(),
						procediment.getNom(),
						procediment.getCodisia(),
						procediment.getEnviamentDataProgramada(),
						procediment.getRetard(),
						entitat,
						pagadorPostal,
						pagadorCie,
						procediment.isAgrupar()).build());
		
		if (procediment.getGrups() != null)
		grupService.create(
				procedimentEntity.getId(), 
				null, 
				procediment.getGrups());
		
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto update(
			ProcedimentDto procediment) throws NotFoundException {
		logger.debug("Actualitzant procediment ("
				+ "procediment=" + procediment + ")");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
				procediment.getId());
		
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(
				procediment.getEntitat().getId());
		
		PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(
				procediment.getPagadorpostal().getId());
		
		PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(
				procediment.getPagadorcie().getId());
		
		procedimentEntity.update(
					procediment.getCodi(),
					procediment.getNom(),
					procediment.getCodisia(),
					entitatEntity,
					pagadorPostalEntity,
					pagadorCieEntity,
					procediment.isAgrupar());
		
		procedimentRepository.save(procedimentEntity);
		
		if (procediment.getGrups() != null)
			grupService.create(
					procedimentEntity.getId(), 
					null, 
					procediment.getGrups());
		
		//Si s'agrupen s'han d'eliminar els grups que li pertanyen
		if(!procediment.isAgrupar()) {
			List<GrupDto> grups = grupService.findByIdProcediment(procediment.getId());
			
			grupService.deleteGrupsProcediment(grups);
		}
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto delete(Long id) throws NotFoundException {
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(id);
		
		//Eliminar grups
		List<GrupEntity> grups = grupRepository.findByProcediment(procedimentEntity);
		grupRepository.delete(grups);
		
		//Eliminar procediment
		procedimentRepository.delete(procedimentEntity);
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto findById(Long id) {
		ProcedimentEntity procedimentEntity = procedimentRepository.findOne(id);
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId);
		
		List<ProcedimentEntity> procediment = procedimentRepository.findByEntitat(entitat);
		
		return conversioTipusHelper.convertirList(
				procediment,
				ProcedimentDto.class);
		
	}
	
	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId);
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		Page<ProcedimentEntity> procediments = null;

		procediments = procedimentRepository.findByEntitat(
					entitat,
					paginacioHelper.toSpringDataPageable(paginacioParams));
		
		return paginacioHelper.toPaginaDto(
				procediments,
				ProcedimentDto.class);
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
	public PaginaDto<ProcedimentDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GrupDto> permisFindByProcedimentCodi(String codi) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);



	
}
