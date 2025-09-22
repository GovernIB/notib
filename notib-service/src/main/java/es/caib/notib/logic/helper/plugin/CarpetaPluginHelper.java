package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.accioMassiva.ResultatAccio;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.carpeta.CarpetaPlugin;
import es.caib.notib.plugin.carpeta.MissatgeCarpetaParams;
import es.caib.notib.plugin.carpeta.VincleInteressat;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointDiscoverer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class CarpetaPluginHelper extends AbstractPluginHelper<CarpetaPlugin> {

	public static final String GRUP = "CARPETA";

	private final NotificacioEventHelper eventHelper;
//    private final ControllerEndpointDiscoverer controllerEndpointDiscoverer;

    public CarpetaPluginHelper(IntegracioHelper integracioHelper,
                               ConfigHelper configHelper,
                               EntitatRepository entitatRepository,
                               NotificacioEventHelper eventHelper,
                               MeterRegistry meterRegistry/*, ControllerEndpointDiscoverer controllerEndpointDiscoverer*/) {

		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
		this.eventHelper = eventHelper;
//        this.controllerEndpointDiscoverer = controllerEndpointDiscoverer;
    }


	public ResultatAccio enviarNotificacioMobil(NotificacioEnviamentEntity e) {

		if (e.isPerEmail() || InteressatTipus.ADMINISTRACIO.equals(e.getTitular().getInteressatTipus())) {
			var error = e.isPerEmail() ? "Enviament per email, no s'envien notificaions mòvils per aquest tipus d'enviament"
					: "Interessat tipus administració, no s'envien notificacions mòvils per aquest tipus d'interessat";
			return ResultatAccio.builder().id(e.getId()).error(true).errorDescripcio(error).build();
		}
		var info = new IntegracioInfo(IntegracioCodi.CARPETA, "Enviar notificació mòvil", IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setNotificacioId(e.getNotificacio().getId());
		info.setAplicacio(e.getNotificacio().getTipusUsuari(), e.getNotificacio().getCreatedBy().get().getCodi());
		var eventInfo = NotificacioEventHelper.EventInfo.builder().enviament(e).tipus(NotificacioEventTipusEnumDto.API_CARPETA).build();
		var enviarCarpeta = enviarCarpeta();
		String errorStackTrace = null;
		try {
			if (!enviarCarpeta) {
				throw new Exception("El plugin de CARPETA no està configurat");
			}
			// peticionsPlugin.updatePeticioTotal(configHelper.getEntitatActualCodi());
            if (configHelper.getEntitatActualCodi() == null) {
                configHelper.setEntitatCodi(e.getNotificacio().getEntitat().getCodi());
            }
			var res = getPlugin().enviarNotificacioMobil(crearMissatgeCarpetaParams(e));
			if (!Strings.isNullOrEmpty(res.getCode()) && "OK".equalsIgnoreCase(res.getCode())) {
				integracioHelper.addAccioOk(info);
			} else {
				eventInfo.setError(true);
				eventInfo.setErrorDescripcio(res.getMessage());
				integracioHelper.addAccioError(info, res.getMessage());
			}
		} catch (Exception ex) {
			var msg = "Error al enviar notificació mòvil";
			log.error(msg, ex);
			eventInfo.setError(true);
			eventInfo.setErrorDescripcio(ex.getMessage());
			errorStackTrace = Arrays.toString(ex.getStackTrace());
			integracioHelper.addAccioError(info, msg, ex);
			if (enviarCarpeta) {
				// peticionsPlugin.updatePeticioError(configHelper.getEntitatActualCodi());
			}
		}
		eventHelper.addEvent(eventInfo);
		return ResultatAccio.builder().id(e.getId()).error(eventInfo.isError()).errorDescripcio(eventInfo.getErrorDescripcio()).errorStackTrace(errorStackTrace).build();
	}

	public static MissatgeCarpetaParams crearMissatgeCarpetaParams(NotificacioEnviamentEntity enviament) {

		var not = enviament.getNotificacio();
		var entitat = not.getEntitat();
		var isRepresentant = enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty();
		var interessat = isRepresentant ? enviament.getDestinataris().get(0) : enviament.getTitular();
		var idioma = not.getIdioma();
		var organ = not.getOrganGestor();
		var nomOrgan = Idioma.ES.equals(idioma) && !Strings.isNullOrEmpty(organ.getNomEs()) ? organ.getNomEs() : organ.getNom();
		var dataCompareixenca = not.getEnviamentDataProgramada() != null ? not.getEnviamentDataProgramada() : not.getNotificaEnviamentData();
		return MissatgeCarpetaParams.builder().nifDestinatari(interessat.getNif()).nomCompletDestinatari(interessat.getNomSencer())
				.codiDir3Entitat(entitat.getDir3Codi()).nomEntitat(entitat.getNom())
				.codiOrganEmisor(not.getEmisorDir3Codi()).nomOrganEmisor(nomOrgan)
				.concepteNotificacio(not.getConcepte()).descNotificacio(not.getDescripcio()).uuIdNotificacio(not.getReferencia())
				.tipus(not.getEnviamentTipus()).vincleInteressat(isRepresentant ? VincleInteressat.REPRESENTANT :VincleInteressat.TITULAR)
				.codiSiaProcediment(not.getProcediment().getCodi()).nomProcediment(not.getProcediment().getNom())
				.caducitatNotificacio(not.getCaducitat())
				.dataDisponibleCompareixenca(dataCompareixenca)
				.numExpedient(not.getNumExpedient())
				.build();
	}

	public boolean enviarCarpeta() {
		return !Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.usuari")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.contrasenya")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.missatge.codi.comunicacio")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.missatge.codi.notificacio")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.class")) &&
				!Strings.isNullOrEmpty(configHelper.getConfig("es.caib.notib.plugin.carpeta.url")) &&
				configHelper.getConfigAsBoolean("es.caib.notib.plugin.carpeta.msg.actiu");
	}

	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) {

		var entitats = entitatRepository.findAll();
		IntegracioDiagnostic diagnostic;
		var diagnosticOk = true;
		String codi;
		for (var entitat : entitats) {
			codi = entitat.getCodi();
			try {
				var plugin = pluginMap.get(codi);
				if (plugin == null)  {
					continue;
				}
				plugin.existeixNif("99999999R");
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(true);
				diagnostics.put(codi, diagnostic);
			} catch(Exception ex) {
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setErrMsg(ex.getMessage());
				diagnostics.put(codi, diagnostic);
				diagnosticOk = false;
			}
		}
		if (diagnostics.isEmpty() && !entitats.isEmpty()) {
			var entitat = entitatRepository.findByCodi(getCodiEntitatActual());
			diagnostic = new IntegracioDiagnostic();
			try {
				getPlugin().existeixNif("99999999R");
				diagnostic.setCorrecte(true);
			} catch (Exception ex) {
				diagnostic.setCorrecte(false);
			}
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}

	@Override
	protected CarpetaPlugin getPlugin() {

		var entitatCodi = configHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(entitatCodi)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = pluginMap.get(entitatCodi);
		if (plugin != null) {
			return (CarpetaPlugin) plugin;
		}
		var pluginClass = getPluginClassProperty();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var error = "No està configurada la classe per al plugin de CARPETA";
			log.error(error);
			throw new SistemaExternException(IntegracioCodi.CARPETA.name(), error);
		}
		try {
			var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(entitatCodi, getConfigGrup());
			var propietats = configHelper.getAllEntityProperties(entitatCodi);
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (CarpetaPlugin) clazz.getDeclaredConstructor(Properties.class, boolean.class, String.class)
                    .newInstance(propietats, configuracioEspecifica, entitatCodi);
			plugin.init(meterRegistry, getCodiApp().name());
            pluginMap.put(entitatCodi, plugin);
			return (CarpetaPlugin) plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioCodi.CARPETA.name(), "Error al crear la instància del plugin de CARPETA", ex);
		}
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.carpeta.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.CAR;
	}

}
