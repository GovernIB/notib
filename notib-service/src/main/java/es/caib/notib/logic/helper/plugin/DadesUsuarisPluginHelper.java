package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class DadesUsuarisPluginHelper extends AbstractPluginHelper<DadesUsuariPlugin> {


	public DadesUsuarisPluginHelper(IntegracioHelper integracioHelper,
                                    ConfigHelper configHelper) {

		super(integracioHelper, configHelper);
    }

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {

		var dades = dadesUsuariConsultarAmbCodi(diagnostics.keySet().stream().iterator().next());
		return dades != null;
	}


	public List<String> consultarRolsAmbCodi(String usuariCodi) {

		var info = new IntegracioInfo(IntegracioCodi.USUARIS,"Consulta rols usuari amb codi",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Codi d'usuari", usuariCodi));

		try {
			peticionsPlugin.updatePeticioTotal(null);
			var rols = getPlugin().consultarRolsAmbCodi(usuariCodi);
			info.addParam("Rols Consultats: ", StringUtils.join(rols, ", "));
			integracioHelper.addAccioOk(info, false);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(IntegracioCodi.USUARIS.name(), errorDescripcio, ex);
		}
	}
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(String usuariCodi) {
		
		var info = new IntegracioInfo(IntegracioCodi.USUARIS,"Consulta d'usuari amb codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi d'usuari", usuariCodi));

		try {
			peticionsPlugin.updatePeticioTotal(null);
			var dadesUsuari = getPlugin().consultarAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(IntegracioCodi.USUARIS.name(), errorDescripcio, ex);
		}
	}
	
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(String grupCodi) {
		
		var info = new IntegracioInfo(IntegracioCodi.USUARIS,"Consulta d'usuaris d'un grup", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi de grup", grupCodi));

		try {
			peticionsPlugin.updatePeticioTotal(null);
			var dadesUsuari = getPlugin().consultarAmbGrup(grupCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(IntegracioCodi.USUARIS.name(), errorDescripcio, ex);
		}
	}

//	public boolean isDadesUsuariPluginDisponible() {
//
//		var pluginClass = getPropertyPluginDadesUsuari();
//		if (pluginClass == null || pluginClass.length() == 0) {
//			return false;
//		}
//		try {
//			return getDadesUsuariPlugin() != null;
//		} catch (SistemaExternException sex) {
//			log.error("Error al obtenir la instància del plugin de dades d'usuari", sex);
//			return false;
//		}
//	}

	private final static String GLOBAL = "GLOBAL";

	@Override
	protected DadesUsuariPlugin getPlugin() {

		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}
		var pluginClass = getPluginClassProperty();
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "La classe del plugin d'usuari no està definida";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodi.USUARIS.name(), msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = pluginClass.endsWith("DadesUsuariPluginKeycloak") ?
							(DadesUsuariPlugin)clazz.getDeclaredConstructor(String.class, Properties.class).newInstance("es.caib.notib.plugin.dades.usuari.",
									configHelper.getAllEntityProperties(null))
							: (DadesUsuariPlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(null));
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} catch (Exception ex) {
			log.error("Error al crear la instància del plugin de dades d'usuari (" + pluginClass + "): ", ex);
			throw new SistemaExternException(IntegracioCodi.USUARIS.name(), "Error al crear la instància del plugin de dades d'usuari", ex);
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
		return configHelper.getConfig("es.caib.notib.plugin.dades.usuari.class");
	}

	// Mètodes pels tests
	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		this.pluginMap.put(GLOBAL, dadesUsuariPlugin);
	}

}
