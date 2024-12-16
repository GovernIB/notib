package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.carpeta.CarpetaPlugin;
import es.caib.notib.plugin.carpeta.MissatgeCarpetaParams;
import es.caib.notib.plugin.carpeta.VincleInteressat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class CarpetaPluginHelper extends AbstractPluginHelper<CarpetaPlugin> {

	private final IntegracioHelper integracioHelper;
	private final NotificacioEventHelper eventHelper;
	private final ConfigHelper configHelper;

	public CarpetaPluginHelper(IntegracioHelper integracioHelper,
                               NotificacioEventHelper eventHelper,
                               ConfigHelper configHelper) {

		super(integracioHelper, configHelper);
        this.integracioHelper = integracioHelper;
		this.eventHelper = eventHelper;
		this.configHelper = configHelper;
	}


	public void enviarNotificacioMobil(NotificacioEnviamentEntity e) {

		if (e.isPerEmail() || InteressatTipus.ADMINISTRACIO.equals(e.getTitular().getInteressatTipus())) {
			return;
		}
		var info = new IntegracioInfo(IntegracioCodiEnum.CARPETA, "Enviar notificació mòvil", IntegracioAccioTipusEnumDto.ENVIAMENT);
		var eventInfo = NotificacioEventHelper.EventInfo.builder().enviament(e).tipus(NotificacioEventTipusEnumDto.API_CARPETA).build();
		var enviarCarpeta = enviarCarpeta();
		try {
			if (!enviarCarpeta) {
				throw new Exception("El plugin de CARPETA no està configurat");
			}
			peticionsPlugin.updatePeticioTotal(configHelper.getEntitatActualCodi());
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
			integracioHelper.addAccioError(info, msg, ex);
			if (enviarCarpeta) {
				peticionsPlugin.updatePeticioError(configHelper.getEntitatActualCodi());
			}
		}
		eventHelper.addEvent(eventInfo);
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
			throw new SistemaExternException(IntegracioCodiEnum.CARPETA.name(), error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (CarpetaPlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(entitatCodi));
			pluginMap.put(entitatCodi, plugin);
			return (CarpetaPlugin) plugin;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioCodiEnum.CARPETA.name(), "Error al crear la instància del plugin de CARPETA", ex);
		}
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
