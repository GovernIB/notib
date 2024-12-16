package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.plugin.arxiu.ArxiuPlugin;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ArxiuPluginHelper extends AbstractPluginHelper<ArxiuPlugin> {

	public ArxiuPluginHelper(IntegracioHelper integracioHelper,
                             ConfigHelper configHelper) {
		super(integracioHelper, configHelper);
	}


	// ARXIU
	// /////////////////////////////////////////////////////////////////////////////////////
	
//	public Document arxiuDocumentConsultar(String arxiuUuid, String versio, boolean isUuid) {
//		return arxiuDocumentConsultar(arxiuUuid, versio, false, isUuid);
//	}

	public Document arxiuDocumentConsultar(String identificador, String versio, boolean ambContingut, boolean isUuid) throws DocumentNotFoundException {

		var info = new IntegracioInfo(IntegracioCodiEnum.ARXIU, "Consulta d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("identificador del document", identificador),
				new AccioParam("Versio", versio));

		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);
		try {
			identificador = isUuid ? "uuid:" + identificador : "csv:" + identificador;
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			var documentDetalls = getPlugin().documentDetalls(identificador, versio, ambContingut);
			integracioHelper.addAccioOk(info);
			return documentDetalls;
		} catch (Exception ex) {
			var ex1 = new DocumentNotFoundException(isUuid ? "UUID" : "CSV", identificador, ex);
			integracioHelper.addAccioError(info, ex1.getMessage(), ex1);
			throw ex1;
		}
	}
	
	public DocumentContingut arxiuGetImprimible(String id, boolean isUuid) {
		
		var info = new IntegracioInfo(IntegracioCodiEnum.ARXIU, "Obtenir versió imprimible d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador del document", id),
				new AccioParam("Tipus d'identificador", isUuid ? "uuid" : "csv"));

		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);

		try {
			id = isUuid ? "uuid:" + id : "csv:" + id;
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			var documentContingut = getPlugin().documentImprimible(id);
			integracioHelper.addAccioOk(info);
			return documentContingut;
		} catch (Exception ex) {
			var errorDescripcio = "No s'ha pogut recuperar el document amb " + id;
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new SistemaExternException(IntegracioCodiEnum.ARXIU.name(), errorDescripcio, ex);
		}
	}
	
	public boolean isArxiuPluginDisponible() {

		var pluginClass = getPluginClassProperty();
		if (pluginClass == null || pluginClass.length() == 0) {
			return false;
		}
		try {
			return getPlugin() != null;
		} catch (SistemaExternException sex) {
			log.error("Error al obtenir la instància del plugin d'arxiu", sex);
			return false;
		}
	}

	@Override
	protected ArxiuPlugin getPlugin() {

		var codiEntitat = getCodiEntitatActual();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi d'entitat no pot ser nul");
		}
		var plugin = pluginMap.get(codiEntitat);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPluginClassProperty();
		if (Strings.isNullOrEmpty(pluginClass)) {
			String msg = "La classe del plugin d'arxiu digital no està definida";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodiEnum.ARXIU.name(), msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (ArxiuPlugin) clazz.getDeclaredConstructor(String.class, Properties.class).newInstance("es.caib.notib.", configHelper.getEnvironmentProperties());
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin d'arxiu digital (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodiEnum.ARXIU.name(), msg, ex);
		}
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.arxiu.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.ARX;
	}

	// Mètodes pels tests
	public void setArxiuPlugin(ArxiuPlugin arxiuPlugin) {
		this.pluginMap.put(getCodiEntitatActual(), arxiuPlugin);
	}
	public void setArxiuPlugin(Map<String, ArxiuPlugin> arxiuPlugin) {
		pluginMap = arxiuPlugin;
	}

}
