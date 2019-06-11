package es.caib.notib.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
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
public class PagadorCieFormatSobreServiceImpl implements PagadorCieFormatSobreService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;

	@Override
	public PagadorCieFormatSobreDto create(Long entitatId, PagadorCieFormatSobreDto formatSobre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagadorCieFormatSobreDto update(PagadorCieFormatSobreDto formatSobre) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagadorCieFormatSobreDto delete(Long id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagadorCieFormatSobreDto findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PagadorCieFormatSobreDto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginaDto<PagadorCieFormatSobreDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);
}
