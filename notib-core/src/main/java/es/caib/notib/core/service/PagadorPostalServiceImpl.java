package es.caib.notib.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.helper.PagadorPostalHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorPostalRepository;

@Service
public class PagadorPostalServiceImpl implements PagadorPostalService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorPostalRepository pagadorPostalReposity;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PagadorPostalHelper pagadorPostalHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	
	@Override
	public PagadorPostalDto create(
			Long entitatId,
			PagadorPostalDto postal) {
		logger.debug("Creant un nou pagador postal ("
				+ "pagador=" + postal + ")");
		
		PagadorPostalEntity pagadorPostalEntity = null;
		
		pagadorPostalEntity = pagadorPostalReposity.save(
				PagadorPostalEntity.getBuilder(
						postal.getDir3codi(),
						postal.getContracteNum(),
						postal.getContracteDataVig(),
						postal.getFacturacioClientCodi()).build());
		
		return conversioTipusHelper.convertir(
				pagadorPostalEntity, 
				PagadorPostalDto.class);
	}

	@Override
	public PagadorPostalDto update(PagadorPostalDto postal) throws NotFoundException {
		logger.debug("Actualitzant pagador postal ("
				+ "pagador=" + postal + ")");
		
				
		PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(postal.getId());
		
		
		pagadorPostalEntity.update(
						postal.getDir3codi(),
						postal.getContracteNum(),
						postal.getContracteDataVig(),
						postal.getFacturacioClientCodi());
		
		pagadorPostalReposity.save(pagadorPostalEntity);
		
		return conversioTipusHelper.convertir(
				pagadorPostalEntity, 
				PagadorPostalDto.class);
	}

	@Override
	public PagadorPostalDto delete(Long id) throws NotFoundException {
		
		PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
		
		
		pagadorPostalReposity.delete(id);
		return conversioTipusHelper.convertir(
				pagadorPostalEntity, 
				PagadorPostalDto.class);
	}

	@Override
	public PagadorPostalDto findById(Long id) {
		
		PagadorPostalEntity pagadorPostalEntity = pagadorPostalReposity.findOne(id);
		
		return conversioTipusHelper.convertir(
				pagadorPostalEntity, 
				PagadorPostalDto.class);
	}

	@Override
	public PaginaDto<PagadorPostalDto> findAmbFiltrePaginat(Long entitatId, PagadorPostalFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		Page<PagadorPostalEntity> pagadorPostal = null;

		pagadorPostal = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginat(
				filtre.getDir3codi() == null,
				filtre.getDir3codi(),
				filtre.getContracteNum() == null,
				filtre.getContracteNum(),
				paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
		
		return paginacioHelper.toPaginaDto(
				pagadorPostal,
				PagadorPostalDto.class);
	}

	@Override
	public List<PagadorPostalDto> findAll() {
		logger.debug("Consulta de tots els pagadors postals");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
					pagadorPostalReposity.findAll(),
					PagadorPostalDto.class);
	}

	@Override
	public PaginaDto<PagadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
