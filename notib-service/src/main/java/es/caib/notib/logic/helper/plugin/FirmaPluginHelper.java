package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class FirmaPluginHelper extends AbstractPluginHelper<FirmaServidorPlugin> {

	private final NotificacioEnviamentRepository enviamentRepository;

	public FirmaPluginHelper(IntegracioHelper integracioHelper,
                             ConfigHelper configHelper, NotificacioEnviamentRepository enviamentRepository) {
		super(integracioHelper, configHelper);
        this.enviamentRepository = enviamentRepository;
    }

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {

		var enviament = enviamentRepository.findTopByRegistreNumeroFormatatNotNullOrderByIdDesc().orElseThrow();
		var justificantOriginal = new FitxerDto();
		justificantOriginal.setNom("justificant_comunicacio_sir_" + enviament.getId() + ".pdf");
		justificantOriginal.setContentType("application/pdf");
		justificantOriginal.setContingut(String.valueOf( new Date().getTime()).getBytes());
		var firmaContingut = firmaServidorFirmar(enviament.getNotificacio(), justificantOriginal, FirmaServidorPlugin.TipusFirma.PADES, "Test justificant enviament Notib", "ca");
		return firmaContingut.length > 0;
	}

	public byte[] firmaServidorFirmar(NotificacioEntity notificacio, FitxerDto fitxer, TipusFirma tipusFirma, String motiu, String idioma) {

		var info = new IntegracioInfo(IntegracioCodi.FIRMASERV, "Firma en servidor d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("notificacioId", notificacio.getId().toString()),
				new AccioParam("títol", fitxer.getNom()));
		info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
		info.setCodiEntitat(notificacio.getEntitat().getCodi());
		try {
			peticionsPlugin.updatePeticioTotal(getCodiEntitatActual());
			var firmaContingut = getPlugin().firmar(fitxer.getNom(), motiu, fitxer.getContingut(), tipusFirma, idioma);
			integracioHelper.addAccioOk(info);
			return firmaContingut;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(getCodiEntitatActual());
			throw new SistemaExternException(IntegracioCodi.FIRMASERV.name(), errorDescripcio, ex);
		}
	}

	@Override
	protected FirmaServidorPlugin getPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = pluginMap.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPluginClassProperty();
		if (pluginClass == null || pluginClass.length() == 0) {
			var error = "No està configurada la classe per al plugin de firma en servidor";
			log.error(error);
			throw new SistemaExternException(IntegracioCodi.FIRMASERV.name(), error);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (FirmaServidorPlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var error = "Error al crear la instància del plugin de firma en servidor" ;
			log.error(error + " (" + pluginClass + "): ", ex);
			throw new SistemaExternException(IntegracioCodi.FIRMASERV.name(), error, ex);
		}
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.firmaservidor.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.PFI;
	}

}
