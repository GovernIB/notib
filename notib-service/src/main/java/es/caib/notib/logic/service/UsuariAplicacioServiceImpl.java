/**
 * 
 */
package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.RequestsHelper;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.callback.NotificacioCanviClient;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusObjecte;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Implementació del servei de gestió d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class UsuariAplicacioServiceImpl implements UsuariAplicacioService {
	
	@Resource
	private AplicacioRepository aplicacioRepository;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private RequestsHelper requestsHelper;


	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto create(AplicacioDto aplicacio) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant una nova aplicació (aplicació=" + aplicacio.toString() + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false);
			var entity = AplicacioEntity.getBuilder(entitat, aplicacio.getUsuariCodi(), aplicacio.getCallbackUrl()).build();
			return conversioTipusHelper.convertir(aplicacioRepository.save(entity), AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant l'aplicació existent (aplicacio=" + aplicacio.toString() + ")");
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false);
			var entity = aplicacioRepository.findById(aplicacio.getId()).orElseThrow();
			entity.update(aplicacio.getUsuariCodi(), aplicacio.getCallbackUrl());
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto delete(Long id, Long entitatId) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Esborrant aplicacio (id=" + id +  ")");
			entityComprovarHelper.comprovarEntitat(entitatId, true, true, false, false);
			var entity = aplicacioRepository.findByEntitatIdAndId(entitatId, id );
			aplicacioRepository.delete(entity);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findById(Long aplicacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb id = " + aplicacioId.toString());
			var entity = aplicacioRepository.findById(aplicacioId).orElseThrow();
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb entitatId= " + entitatId + " i id = " + aplicacioId.toString());
			var entity = aplicacioRepository.findByEntitatIdAndId(entitatId, aplicacioId);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByUsuariCodi(String usuariCodi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb codi = " + usuariCodi);
			var entity = aplicacioRepository.findByUsuariCodi(usuariCodi);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public AplicacioDto findByEntitatAndUsuariCodi(Long entitatId, String usuariCodi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta una aplicació amb entitatId= " + entitatId + " i codi = " + usuariCodi);
//			entityComprovarHelper.comprovarPermisos( null, true, false, false);
			var entity = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitatId, usuariCodi);
			return conversioTipusHelper.convertir(entity, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams) {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de un llistat paginat de totes les aplicacions");
			entityComprovarHelper.comprovarPermisos(null, true, false, false);
			var aplicacions = aplicacioRepository.findAllFiltrat(paginacioParams.getFiltre(), paginacioHelper.toSpringDataPageable(paginacioParams));
			return paginacioHelper.toPaginaDto(aplicacions, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de un llistat paginat de totes les aplicacions de l'entitat amb Id: " + entitatId);
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			var filtres = paginacioParams.getFiltres();
			var codi = filtres.get(0).getValor();
			var url = filtres.get(1).getValor();
			var params = paginacioHelper.toSpringDataPageable(paginacioParams);
			var activa = !Strings.isNullOrEmpty(filtres.get(2).getValor()) ? Integer.parseInt(filtres.get(2).getValor()) == 1 : null;
			var aplicacions = activa != null ? aplicacioRepository.findByEntitatIdFiltrat(entitatId, codi, url, activa, params)
					: aplicacioRepository.findByEntitatIdFiltrat(entitatId, codi, url, params);
			return paginacioHelper.toPaginaDto(aplicacions, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<AplicacioDto> findByEntitatAndText(Long entitatId, String text) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant usuaris aplicació amb text (text=" + text + ")");
			return conversioTipusHelper.convertirList(aplicacioRepository.findByText(entitatId, text), AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public AplicacioDto updateActiva(Long id, boolean activa) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant propietat activa d'una aplicació existent (id=" + id + ", activa=" + activa + ")");
			var aplicacio = aplicacioRepository.findById(id).orElseThrow();
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitat().getId(), true, true, false, false);
			aplicacio.updateActiva(activa);
			return conversioTipusHelper.convertir(aplicacio, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean provarAplicacio(Long aplicacioId) {

		try {
			log.info("Provant aplicacio " + aplicacioId);
			var aplicacio = aplicacioRepository.findById(aplicacioId).orElseThrow();
			var urlCallback = aplicacio.getCallbackUrl() + (aplicacio.getCallbackUrl().endsWith("/") ? "" : "/") +  CallbackHelper.NOTIFICACIO_CANVI;
			var resposta = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, new NotificacioCanviClient());
			return resposta != null && ClientResponse.Status.OK.getStatusCode() == resposta.getStatusInfo().getStatusCode();
		} catch (Exception ex) {
			log.error("Error inesperat provant la aplicacio", ex);
			return false;
		}
	}


}
