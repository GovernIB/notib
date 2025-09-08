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
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RespostaTestAplicacio;
import es.caib.notib.logic.intf.dto.callback.NotificacioCanviClient;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusObjecte;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Resource
	private EntitatRepository entitatRepository;


	@Audita(entityType = TipusEntitat.APLICACIO, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public AplicacioDto create(AplicacioDto aplicacio) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant una nova aplicació (aplicació=" + aplicacio.toString() + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false, false);
			var entity = AplicacioEntity.builder()
							.entitat(entitat)
							.usuariCodi(aplicacio.getUsuariCodi())
							.callbackUrl(aplicacio.getCallbackUrl())
                            .activa(true)
							.headerCsrf(aplicacio.isHeaderCsrf())
                            .horariLaboralInici(aplicacio.getHorariLaboralInici())
                            .horariLaboralFi(aplicacio.getHorariLaboralFi())
                            .maxEnviamentsMinutLaboral(aplicacio.getMaxEnviamentsMinutLaboral())
                            .maxEnviamentsMinutNoLaboral(aplicacio.getMaxEnviamentsMinutNoLaboral())
                            .maxEnviamentsDiaLaboral(aplicacio.getMaxEnviamentsDiaLaboral())
                            .maxEnviamentsDiaNoLaboral(aplicacio.getMaxEnviamentsDiaNoLaboral())
                            .build();
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
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitatId(), true, true, false, false, false);
			var entity = aplicacioRepository.findById(aplicacio.getId()).orElseThrow();
			entity.update(aplicacio);
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
			entityComprovarHelper.comprovarEntitat(entitatId, true, true, false, false, false);
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
			entityComprovarHelper.comprovarPermisos(null, true, false, false, true);
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
			entityComprovarHelper.comprovarPermisos(null, true, true, false, true);
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
			entityComprovarHelper.comprovarEntitat(aplicacio.getEntitat().getId(), true, true, false, false, false);
			aplicacio.updateActiva(activa);
			return conversioTipusHelper.convertir(aplicacio, AplicacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public RespostaTestAplicacio provarAplicacio(Long aplicacioId) {

		try {
			log.info("Provant aplicacio " + aplicacioId);
			var aplicacio = aplicacioRepository.findById(aplicacioId).orElseThrow();
			var urlCallback = aplicacio.getCallbackUrl() + (aplicacio.getCallbackUrl().endsWith("/") ? "" : "/") +  CallbackHelper.NOTIFICACIO_CANVI;
			var resposta = requestsHelper.callbackAplicacioNotificaCanvi(urlCallback, new NotificacioCanviClient(), aplicacio.isHeaderCsrf());
			var ok = resposta != null && ClientResponse.Status.OK.getStatusCode() == resposta.getStatusInfo().getStatusCode();
			var error = !ok && resposta != null ? resposta.getStatus() + " " + resposta.getStatusInfo() : null;
			return RespostaTestAplicacio.builder().ok(ok).error(error).build();
		} catch (Exception ex) {
			var msg = "Error inesperat provant la aplicacio";
			log.error(msg, ex);
			return RespostaTestAplicacio.builder().ok(false).error(msg + ex.getMessage()).build();
		}
	}

	@Override
	public boolean diagnosticarAplicacions(Map<String, IntegracioDiagnostic> diagnostics) {

		var entitats = entitatRepository.findAll();
		IntegracioDiagnostic diagnostic;
		IntegracioDiagnostic diagnosticEntitat;
		List<AplicacioEntity> aplicacions;
		Map<String, IntegracioDiagnostic> diagnosticsEntitat;
		RespostaTestAplicacio resposta;
		String error;
		for (var entitat : entitats) {
			aplicacions = aplicacioRepository.findByEntitat(entitat);
			if (aplicacions.isEmpty()) {
				continue;
			}
			diagnosticsEntitat = new HashMap<>();
			for (var aplicacio : aplicacions) {
				resposta = provarAplicacio(aplicacio.getId());
				error = !resposta.isOk()? resposta.getError() : null;
				diagnostic = IntegracioDiagnostic.builder().correcte(resposta.isOk()).errMsg(error).build();
				diagnosticsEntitat.put(aplicacio.getUsuariCodi(), diagnostic);
			}
			diagnosticEntitat = IntegracioDiagnostic.builder().diagnosticsEntitat(diagnosticsEntitat).build();
			diagnostics.put(entitat.getCodi(), diagnosticEntitat);
		}
		return false;
	}

	@Override
	public IntegracioDiagnostic diagnosticarAplicacions(Long entitatId) {

		IntegracioDiagnostic diagnostic;
		IntegracioDiagnostic diagnosticEntitat;
		Map<String, IntegracioDiagnostic> diagnosticsEntitat;
		RespostaTestAplicacio resposta;
		String error;
		try {
			var entitat = entitatRepository.findById(entitatId).orElseThrow();
			var aplicacions = aplicacioRepository.findByEntitatAndActivaIsTrue(entitat);
			if (aplicacions.isEmpty()) {
				return IntegracioDiagnostic.builder().correcte(false).errMsg("No hi han aplicacions per aquesta entitat").build();
			}
			diagnosticsEntitat = new HashMap<>();
			for (var aplicacio : aplicacions) {
				resposta = provarAplicacio(aplicacio.getId());
				error = !resposta.isOk()? resposta.getError() : null;
				diagnostic = IntegracioDiagnostic.builder().correcte(resposta.isOk()).errMsg(error).build();
				diagnosticsEntitat.put(aplicacio.getUsuariCodi(), diagnostic);
			}
			return IntegracioDiagnostic.builder().correcte(true).diagnosticsEntitat(diagnosticsEntitat).build();
		} catch (Exception ex) {
			log.error("Error al diagnosticar les aplicacions per l'entitat " + entitatId, ex);
			return IntegracioDiagnostic.builder().correcte(false).errMsg(ex.getMessage()).build();

		}
	}


}
