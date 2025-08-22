package es.caib.notib.logic.service;

import es.caib.notib.logic.comanda.ComandaListener;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.AvisService;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.AvisEntity;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.persist.repository.AvisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
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
    @Autowired
    private ComandaListener comandaListener;

	
	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {

		log.debug("Creant una nova avis (avis=" + avis + ")");
		var entity = AvisEntity.getBuilder(avis.getAssumpte(), avis.getMissatge(), avis.getDataInici(), avis.getDataFinal(), avis.getAvisNivell(),
							avis.getAvisAdministrador(), avis.getEntitatId()).build();
		var dto = conversioTipusHelper.convertir(avisRepository.save(entity), AvisDto.class);
//        comandaListener.enviarAvisCommanda(dto);
	    return dto;
    }

	@Transactional
	@Override
	public AvisDto update(AvisDto avis) {

		log.debug("Actualitzant avis existent (avis=" + avis + ")");
		var avisEntity = avisRepository.findById(avis.getId()).orElseThrow();
		avisEntity.update(avis.getAssumpte(), avis.getMissatge(), avis.getDataInici(), avis.getDataFinal(), avis.getAvisNivell());
		var dto = conversioTipusHelper.convertir(avisEntity, AvisDto.class);
//        comandaListener.enviarAvisCommanda(dto);
	    return dto;
    }

	@Transactional
	@Override
	public AvisDto updateActiva(Long id, boolean activa) {

		try {
			log.debug("Actualitzant propietat activa d'una avis existent (id=" + id + ", activa=" + activa + ")");
			var avisEntity = avisRepository.findById(id).orElseThrow();
			avisEntity.updateActiva(activa);
			var dto = conversioTipusHelper.convertir(avisEntity, AvisDto.class);
//            comandaListener.enviarAvisCommanda(dto);
		    return dto;
        } catch (Exception e) {
			log.error("[Avis] Error arctualtizant la propietat activa per l'avis " + id, e);
			return null;
		}
	}

	@Transactional
	@Override
	public AvisDto delete(Long id) {

		try {
			log.debug("Esborrant avis (id=" + id +  ")");
			var avisEntity = avisRepository.findById(id).orElseThrow();
			avisRepository.delete(avisEntity);
			return conversioTipusHelper.convertir(avisEntity, AvisDto.class);
		} catch (Exception e) {
			log.error("[Avis] Error esborrant l'avis " + id, e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {

		log.debug("Consulta de l'avis (id=" + id + ")");
		var avisEntity = avisRepository.findById(id).orElse(null);
		return conversioTipusHelper.convertir(avisEntity, AvisDto.class);
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

	@Override
	public List<Long> findAllIds() {
		return avisRepository.findAllIds();
	}
}
