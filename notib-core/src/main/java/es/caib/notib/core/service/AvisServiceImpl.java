package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.AvisService;
import es.caib.notib.core.entity.AvisEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.AvisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementació del servei de gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AvisServiceImpl implements AvisService {

	@Autowired
	private AvisRepository avisRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	
	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {

		log.debug("Creant una nova avis (avis=" + avis + ")");
		AvisEntity entity = AvisEntity.getBuilder(avis.getAssumpte(), avis.getMissatge(), avis.getDataInici(), avis.getDataFinal(), avis.getAvisNivell(),
							avis.getAvisAdministrador(), avis.getEntitatId()).build();
		return conversioTipusHelper.convertir(avisRepository.save(entity), AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto update(AvisDto avis) {

		log.debug("Actualitzant avis existent (avis=" + avis + ")");
		AvisEntity avisEntity = avisRepository.findOne(avis.getId());
		avisEntity.update(avis.getAssumpte(), avis.getMissatge(), avis.getDataInici(), avis.getDataFinal(), avis.getAvisNivell());
		return conversioTipusHelper.convertir(avisEntity, AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto updateActiva(Long id, boolean activa) {

		log.debug("Actualitzant propietat activa d'una avis existent (id=" + id + ", activa=" + activa + ")");
		AvisEntity avisEntity = avisRepository.findOne(id);
		avisEntity.updateActiva(activa);
		return conversioTipusHelper.convertir(avisEntity, AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto delete(Long id) {

		log.debug("Esborrant avis (id=" + id +  ")");
		AvisEntity avisEntity = avisRepository.findOne(id);
		avisRepository.delete(avisEntity);
		return conversioTipusHelper.convertir(avisEntity, AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {

		log.debug("Consulta de l'avis (id=" + id + ")");
		AvisEntity avisEntity = avisRepository.findOne(id);
		AvisDto dto = conversioTipusHelper.convertir(avisEntity, AvisDto.class);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {

		log.debug("Consulta de totes les avisos paginades (paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.esPaginacioActivada(paginacioParams) ?
				paginacioHelper.toPaginaDto(avisRepository.findAll(paginacioHelper.toSpringDataPageable(paginacioParams)), AvisDto.class)
			 	: paginacioHelper.toPaginaDto(avisRepository.findAll(paginacioHelper.toSpringDataSort(paginacioParams)), AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AvisDto> findActive() {
		log.debug("Consulta els avisos actius");
		return conversioTipusHelper.convertirList(avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)), AvisDto.class);
	}

	@Override
	public List<AvisDto> findActiveAdmin(Long entitatId) {
		return conversioTipusHelper.convertirList(avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitatId), AvisDto.class);
	}
}
