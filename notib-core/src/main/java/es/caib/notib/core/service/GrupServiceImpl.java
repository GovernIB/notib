package es.caib.notib.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.GrupFiltreDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.GrupHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.GrupRepository;

@Service
public class GrupServiceImpl implements GrupService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private GrupRepository grupReposity;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private GrupHelper conversioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	
	@Override
	public GrupDto create(
			Long procedimentId,
			Long entitatId,
			List<GrupDto> grups) {
		logger.debug("Creant un nou gurp ("
				+ "grup=" + grups + ")");
		
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(procedimentId);
		
		GrupEntity grupEntity = null;
		
		for (GrupDto grup: grups) {
			grupEntity = grupReposity.save(
					GrupEntity.getBuilder(
							grup.getCodi(),
							grup.getNom(),
							procediment).build());
		}
		
		return conversioTipusHelper.convertir(
				grupEntity, 
				GrupDto.class);
	}

	@Override
	public GrupDto update(GrupDto grup) throws NotFoundException {
		logger.debug("Actualitzant grup ("
				+ "grup=" + grup + ")");
		
				
		GrupEntity grupEntity = entityComprovarHelper.comprovarGrup(grup.getId());
		
		
		grupEntity.update(
						grup.getCodi(),
						grup.getNom());
		
		grupReposity.save(grupEntity);
		
		return conversioTipusHelper.convertir(
				grupEntity, 
				GrupDto.class);
	}

	@Override
	public GrupDto delete(Long id) throws NotFoundException {
		GrupEntity grupEntity = entityComprovarHelper.comprovarGrup(id);
		
		grupReposity.delete(grupEntity);
		
		return conversioTipusHelper.convertir(
				grupEntity, 
				GrupDto.class);
	}

	@Override
	public GrupDto findById(Long id) {
		
		GrupEntity grupEntity = grupReposity.findOne(id);
		
		return conversioTipusHelper.convertir(
				grupEntity, 
				GrupDto.class);
	}
	
	@Override
	public List<GrupDto> deleteGrupsProcediment(
			List<GrupDto> grups) throws NotFoundException {
		
		List<GrupEntity> grupsEntity = entityComprovarHelper.comprovarGrups(grups);
		
		grupReposity.delete(grupsEntity);
		
		return grups;
	}
	
	@Override
	public List<GrupDto> findByIdProcediment(Long procedimentId) {
		
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(procedimentId);
		
		List<GrupEntity> grupEntity = grupReposity.findByProcediment(procedimentEntity);
		
		return conversioTipusHelper.convertirList(
				grupEntity, 
				GrupDto.class);
	}
	

	@Override
	public List<GrupDto> findByIdProcedimentAndGrupsId(
			Long procedimentId, 
			List<Long> grupsId) {
		
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(procedimentId);
		
		List<GrupEntity> grupEntity = grupReposity.findByIdProcedimentAndInGrupsId(
				procedimentEntity,
				grupsId);
		
		return conversioTipusHelper.convertirList(
				grupEntity, 
				GrupDto.class);
	}

	@Override
	public PaginaDto<GrupDto> findAmbFiltrePaginat(Long entitatId, GrupFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false );
		
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		Page<GrupEntity> grup = null;

		grup = grupReposity.findByCodiNotNullFiltrePaginat(
				filtre.getCodi() == null,
				filtre.getCodi(),
				paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
		
		return paginacioHelper.toPaginaDto(
				grup,
				GrupDto.class);
	}

	@Override
	public List<GrupDto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);




}
