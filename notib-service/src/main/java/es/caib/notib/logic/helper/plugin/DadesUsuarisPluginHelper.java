package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto.ENVIAMENT;
import static es.caib.notib.logic.intf.dto.IntegracioCodi.USUARIS;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class DadesUsuarisPluginHelper extends AbstractPluginHelper<DadesUsuariPlugin> {

	public static final String GRUP = "USUARIS";

	public DadesUsuarisPluginHelper(IntegracioHelper integracioHelper,
                                    ConfigHelper configHelper,
									EntitatRepository entitatRepository) {

		super(integracioHelper, configHelper, entitatRepository);
    }

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {

		var dades = dadesUsuariConsultarAmbCodi(diagnostics.keySet().stream().iterator().next());
		return dades != null;
	}


//	@Monitor(codi = USUARIS, descripcio = "Consulta rols usuari amb codi", tipus = ENVIAMENT)
	public List<String> consultarRolsAmbCodi(String usuariCodi) {

		var info = new IntegracioInfo(USUARIS,"Consulta rols usuari amb codi " + usuariCodi,
				ENVIAMENT, new AccioParam("Codi d'usuari", usuariCodi));
		try {
			// peticionsPlugin.updatePeticioTotal(null);
			var rols = getPlugin().consultarRolsAmbCodi(usuariCodi);
			info.addParam("Rols Consultats: ", StringUtils.join(rols, ", "));
			integracioHelper.addAccioOk(info, false);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			// peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
		}
	}
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(String usuariCodi) {
		
		var info = new IntegracioInfo(USUARIS,"Consulta d'usuari amb codi " + usuariCodi, ENVIAMENT,
				new AccioParam("Codi d'usuari", usuariCodi));

		try {
			// peticionsPlugin.updatePeticioTotal(null);
			var dadesUsuari = getPlugin().consultarAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			// peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
		}
	}
	
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(String grupCodi) {
		
		var info = new IntegracioInfo(USUARIS,"Consulta d'usuaris d'un grup", ENVIAMENT,
				new AccioParam("Codi de grup", grupCodi));

		try {
			// peticionsPlugin.updatePeticioTotal(null);
			var dadesUsuari = getPlugin().consultarAmbGrup(grupCodi);
			integracioHelper.addAccioOk(info, false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(info, errorDescripcio, ex, false);
			// peticionsPlugin.updatePeticioError(null);
			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
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
			throw new SistemaExternException(USUARIS.name(), msg);
		}
		try {
			String propertyKeyBase = "es.caib.notib.plugin.dades.usuari.";
			var properties = configHelper.getAllEntityProperties(null);
			Class<?> clazz = Class.forName(pluginClass);
			plugin = pluginClass.endsWith("DadesUsuariPluginKeycloak") || pluginClass.endsWith("DadesUsuariPluginLdapCaib") ?
							(DadesUsuariPlugin) clazz.getDeclaredConstructor(String.class, Properties.class, boolean.class).newInstance(propertyKeyBase, properties, false)
							: (DadesUsuariPlugin) clazz.getDeclaredConstructor(Properties.class, boolean.class).newInstance(properties, false);
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} catch (Exception ex) {
			log.error("Error al crear la instància del plugin de dades d'usuari (" + pluginClass + "): ", ex);
			throw new SistemaExternException(USUARIS.name(), "Error al crear la instància del plugin de dades d'usuari", ex);
		}
	}

	// PROPIETATS PLUGIN
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.notib.plugin.dades.usuari.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.USR;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

	// Mètodes pels tests
	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		this.pluginMap.put(GLOBAL, dadesUsuariPlugin);
	}


	// SALUT

	@Override
	public List<es.caib.comanda.ms.salut.model.IntegracioInfo> getIntegracionsInfo() {
		return List.of(es.caib.comanda.ms.salut.model.IntegracioInfo.builder()
				.codi(getCodiApp().name())
				.nom(getCodiApp().getNom())
				.build());
	}

	@Override
	public List<IntegracioSalut> getIntegracionsSalut() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin == null) {
			return List.of(IntegracioSalut.builder().codi(getCodiApp().name()).estat(EstatSalutEnum.UNKNOWN).build());
		}

		EstatSalut estatSalut = plugin.getEstatPlugin();
		return List.of(IntegracioSalut.builder()
				.codi(getCodiApp().name())
				.estat(estatSalut.getEstat())
				.latencia(estatSalut.getLatencia())
				.peticions(plugin.getPeticionsPlugin())
				.build());
	}
}
