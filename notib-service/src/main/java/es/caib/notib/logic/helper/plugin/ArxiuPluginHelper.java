package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
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
public class ArxiuPluginHelper extends AbstractPluginHelper<IArxiuPlugin> {

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

		var info = new IntegracioInfo(IntegracioCodi.ARXIU, "Consulta d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
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
		
		var info = new IntegracioInfo(IntegracioCodi.ARXIU, "Obtenir versió imprimible d'un document", IntegracioAccioTipusEnumDto.ENVIAMENT,
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
			throw new SistemaExternException(IntegracioCodi.ARXIU.name(), errorDescripcio, ex);
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
	protected IArxiuPlugin getPlugin() {

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
			throw new SistemaExternException(IntegracioCodi.ARXIU.name(), msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (IArxiuPlugin) clazz.getDeclaredConstructor(String.class, Properties.class).newInstance("es.caib.notib.", configHelper.getEnvironmentProperties());
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin d'arxiu digital (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodi.ARXIU.name(), msg, ex);
		}
	}

	@Override
	protected EstatSalutEnum getEstat() {
		// TODO: Petició per comprovar la salut
		return EstatSalutEnum.UP;
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.arxiu.class");
	}

	// Mètodes pels tests
	public void setArxiuPlugin(IArxiuPlugin arxiuPlugin) {
		this.pluginMap.put(getCodiEntitatActual(), arxiuPlugin);
	}
	public void setArxiuPlugin(Map<String, IArxiuPlugin> arxiuPlugin) {
		pluginMap = arxiuPlugin;
	}

}
