/**
 * 
 */
package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import com.sun.jersey.api.client.ClientResponse;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EmailNotificacioHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.RequestsHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDetall;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.callback.NotificacioCanviClient;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.MonitorIntegracioService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.logic.plugin.cie.CiePluginHelper;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.filtres.FiltreMonitorIntegracio;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaPostalRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioParamRepository;
import es.caib.notib.persist.repository.monitor.MonitorIntegracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private MonitorIntegracioRepository monitorRepository;
	@Resource
	private MonitorIntegracioParamRepository paramRepository;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private AplicacioService aplicacioService;
    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    private CiePluginHelper ciePluginHelper;
    @Autowired
    private NotificaHelper notificaHelper;
	@Autowired
	private RequestsHelper requestsHelper;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private UsuariAplicacioService usuariAplicacioService;
    @Autowired
    private EntregaPostalRepository entregaPostalRepository;

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(IntegracioCodi codi, PaginacioParamsDto paginacio, IntegracioFiltreDto filtre) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant les darreres accions per a la integració ( codi=" + codi + ")");
			var pageable = paginacioHelper.toSpringDataPageable(paginacio);
			var entitatCodiNull = Strings.isNullOrEmpty(filtre.getEntitatCodi());
			var appNull = Strings.isNullOrEmpty(filtre.getAplicacio());
			filtre.setDataFi(DatesUtils.incrementarDataFi(filtre.getDataFi()));

			var f = FiltreMonitorIntegracio.builder()
					.codi(codi)
					.codiEntitatNull(Strings.isNullOrEmpty(filtre.getEntitatCodi()))
					.codiEntitat(!entitatCodiNull ? filtre.getEntitatCodi() : "")
					.aplicacioNull(Strings.isNullOrEmpty(filtre.getAplicacio()))
					.aplicacio(!appNull ? filtre.getAplicacio() : "")
					.descripcioNull(Strings.isNullOrEmpty(filtre.getDescripcio()))
					.descripcio(filtre.getDescripcio())
					.dataIniciNull(filtre.getDataInici() == null)
					.dataInici(filtre.getDataInici())
					.dataFiNull(filtre.getDataFi() == null)
					.dataFi(filtre.getDataFi())
					.tipusNull(filtre.getTipus() == null)
					.tipus(filtre.getTipus())
					.estatNull(filtre.getEstat() == null)
					.estat(filtre.getEstat())
					.build();
			var accions = monitorRepository.getByFiltre(f, pageable);
			return paginacioHelper.toPaginaDto(accions, IntegracioAccioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Map<IntegracioCodi, Integer> countErrors() {

		var timer = metricsHelper.iniciMetrica();
		try {
			return integracioHelper.countErrorsGroupByCodi();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void netejarMonitor() {

		var timer = metricsHelper.iniciMetrica();
		try {
			monitorRepository.deleteAll();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public IntegracioDetall detallIntegracio(Long id) {

		try {
			var i = monitorRepository.findById(id).orElseThrow();
			var a = paramRepository.findByMonitorIntegracioOrderByIdAsc(i);
			if (a == null) {
				return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
			}
			var accions = conversioTipusHelper.convertirList(a, AccioParam.class);
			return IntegracioDetall.builder().data(i.getData()).descripcio(i.getDescripcio()).tipus(i.getTipus()).estat(i.getEstat())
					.errorDescripcio(i.getErrorDescripcio()).excepcioMessage(i.getExcepcioMessage())
					.excepcioStacktrace(i.getExcepcioStacktrace()).parametres(accions).build();
		} catch (Exception ex) {
			return IntegracioDetall.builder().descripcio("Error obtinguent el detall de la integració " + id).build();
		}
	}

	@Override
	public IntegracioDiagnostic diagnostic(String codi) {

		var diagnostic = new IntegracioDiagnostic();
		var usuari = aplicacioService.getUsuariActual();
		try {
			var integracioCodi = IntegracioCodi.valueOf(codi);
			var prova = messageHelper.getMessage("integracio.list.pipella." + integracioCodi + ".descripcio");
			diagnostic.setProva(prova);
			NotificacioEnviamentEntity enviament;
			Map<String, IntegracioDiagnostic> diagnostics = new HashMap<>();
			switch (integracioCodi) {
				case USUARIS:
					diagnostics.put(usuari.getCodi(), null);
					diagnostic.setCorrecte(pluginHelper.diagnosticarDadesUsuaris(diagnostics));
					diagnostics = null;
					break;
				case REGISTRE:
					diagnostic.setCorrecte(pluginHelper.diagnosticarRegistre(diagnostics));
					break;
				case NOTIFICA:
					enviament = enviamentRepository.findTopByNotificaIdentificadorNullOrderByIdDesc().orElseThrow();
					notificaHelper.enviamentRefrescarEstat(enviament.getId());
					diagnostic.setCorrecte(true);
					break;
				case ARXIU:
					diagnostic.setCorrecte(pluginHelper.diagnosticarArxiu(diagnostics));
					break;
				case CALLBACK:
					diagnostic.setProva(messageHelper.getMessage("integracio.diagnostic.callback.descripcio"));
					usuariAplicacioService.diagnosticarAplicacions(diagnostics);
//					var aplicacio = aplicacioRepository.findTopByCallbackUrlNotNullOrderByIdDesc().orElseThrow();
//					var r = requestsHelper.callbackAplicacioNotificaCanvi(aplicacio.getCallbackUrl(), new NotificacioCanviClient());
//					var ok = r != null && ClientResponse.Status.OK.getStatusCode() == r.getStatusInfo().getStatusCode();
//					diagnostic.setCorrecte(ok);
//					diagnostic.setErrMsg(!ok ? r.getStatus() + " " + r.getStatusInfo() : null);
					break;
				case GESDOC:
					diagnostic.setCorrecte(pluginHelper.diagnosticarGestorDocumental(diagnostics));
					break;
				case UNITATS:
					pluginHelper.diagnosticarUnitats(diagnostics);
					break;
				case GESCONADM:
				case PROCEDIMENTS:
					diagnostic.setCorrecte(pluginHelper.diagnosticarGestorDocumentalAdministratiu(diagnostics));
					break;
				case FIRMASERV:
					diagnostic.setCorrecte(pluginHelper.diagnosticarFirmaEnServidor(diagnostics));
					break;
				case VALIDASIG:
					diagnostic.setCorrecte(pluginHelper.diagnosticarValidacioFirmes(diagnostics));
					break;
				case CARPETA:
					diagnostic.setCorrecte(pluginHelper.diagnosticarCarpeta(diagnostics));
					break;
//				case EMAIL:
//					emailHelper.sendEmailTest(usuari.getEmail());
//					diagnostic.setCorrecte(true);
//					break;
				case CIE:
					var entrega = entregaPostalRepository.findTopByCieIdNotNullOrderByIdDesc().orElseThrow();
					enviament = enviamentRepository.findByCieId(entrega.getCieId());
					var resultat = ciePluginHelper.consultarEstatEntregaPostal(enviament.getId());
					var correcte = resultat != null && "000".equals(resultat.getCodiResposta());
					diagnostic.setCorrecte(correcte);
					if (!correcte) {
						diagnostic.setErrMsg(resultat.getDescripcioResposta());
					}
					break;
			}
			diagnostic.setDiagnosticsEntitat(diagnostics);
			return diagnostic;
		} catch (Exception ex) {
			var error = "Error realitzant el diagnostic de la integracio: ";
			log.error(error + codi);
			diagnostic.setCorrecte(false);
			diagnostic.setErrMsg(ex.getMessage());
			return diagnostic;
		}
	}

}
