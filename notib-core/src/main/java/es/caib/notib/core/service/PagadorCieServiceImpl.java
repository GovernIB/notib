package es.caib.notib.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorCieRepository;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PagadorCieServiceImpl implements PagadorCieService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	
	@Override
	public PagadorCieDto create(
			Long entitatId,
			PagadorCieDto cie) {
		logger.debug("Creant un nou pagador cie ("
				+ "pagador=" + cie + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		PagadorCieEntity pagadorCieEntity = null;
		
		pagadorCieEntity = pagadorCieReposity.save(
				PagadorCieEntity.getBuilder(
						cie.getDir3codi(),
						cie.getContracteDataVig(),
						entitat).build());
		
		return conversioTipusHelper.convertir(
				pagadorCieEntity, 
				PagadorCieDto.class);
	}

	@Override
	public PagadorCieDto update(PagadorCieDto cie) throws NotFoundException {
		logger.debug("Actualitzant pagador cie ("
				+ "pagador=" + cie + ")");	
		PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(cie.getId());
		pagadorCieEntity.update(
						cie.getDir3codi(),
						cie.getContracteDataVig());
		
		pagadorCieReposity.save(pagadorCieEntity);
		
		return conversioTipusHelper.convertir(
				pagadorCieEntity, 
				PagadorCieDto.class);
	}

	@Override
	public PagadorCieDto delete(Long id) throws NotFoundException {
		PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
		
		pagadorCieReposity.delete(id);
		return conversioTipusHelper.convertir(
				pagadorCieEntity, 
				PagadorCieDto.class);
	}

	@Override
	public PagadorCieDto findById(Long id) {
		PagadorCieEntity pagadorCieEntity = pagadorCieReposity.findOne(id);
		
		return conversioTipusHelper.convertir(
				pagadorCieEntity, 
				PagadorCieDto.class);
	}

	@Override
	public PaginaDto<PagadorCieDto> findAmbFiltrePaginat(
			Long entitatId, 
			PagadorCieFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		Page<PagadorCieEntity> pagadorCie = null;

		pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitat(
				filtre.getDir3codi() == null || filtre.getDir3codi().isEmpty(),
				filtre.getDir3codi(),
				entitat,
				paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
		
		return paginacioHelper.toPaginaDto(
				pagadorCie,
				PagadorCieDto.class);
	}

	@Override
	public List<PagadorCieDto> findAll() {
		logger.debug("Consulta de tots els pagadors cie");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
					pagadorCieReposity.findAll(),
					PagadorCieDto.class);
	}

	@Override
	public PaginaDto<PagadorCieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
