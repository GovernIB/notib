package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class GestioDocumentalPluginHelper extends AbstractPluginHelper<GestioDocumentalPlugin> {

	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	public static final String GESDOC_AGRUPACIO_TEMPORALS = "tmp";
	public static final String GESDOC_AGRUPACIO_MASSIUS_CSV = "massius_csv";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ZIP = "massius_zip";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ERRORS = "massius_errors";
	public static final String GESDOC_AGRUPACIO_MASSIUS_INFORMES = "massius_informes";

	@Autowired
	private DocumentRepository documentRepository;

	public GestioDocumentalPluginHelper(IntegracioHelper integracioHelper, ConfigHelper configHelper) {
		super(integracioHelper, configHelper);
	}

	@Synchronized
	public String gestioDocumentalCreate(String agrupacio, byte[] contingut) {
		
		var info = new IntegracioInfo(IntegracioCodiEnum.GESDOC, "Creació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));
		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);

		try {
			log.info("Creant el document en el gestor documental amb agrupacio " + agrupacio);
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			var gestioDocumentalId = getPlugin().create(agrupacio, new ByteArrayInputStream(contingut));
			info.getParams().add(new AccioParam("Id retornat", gestioDocumentalId));
			integracioHelper.addAccioOk(info);
			return gestioDocumentalId;
		} catch (Exception ex) {
			var errorDescripcio = "Error al crear document a dins la gestió documental";
			log.error("Error creant el document en el gestor documental amb agrupacio " + agrupacio);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(codiEntitat);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), errorDescripcio, ex);
		}
	}

	@Synchronized
	public void gestioDocumentalUpdate(String id, String agrupacio, byte[] contingut) {
		
		var info = new IntegracioInfo(IntegracioCodiEnum.GESDOC, "Modificació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio),
				new AccioParam("Núm bytes", (contingut != null) ? Integer.toString(contingut.length) : "0"));
		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);

		try {
			log.info("Actualitzant el document " + id);
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			getPlugin().update(id, agrupacio, new ByteArrayInputStream(contingut));
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental";
			log.error("Error actualitzant el document " + id);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(codiEntitat);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), errorDescripcio, ex);
		}
	}
	
	public void gestioDocumentalDelete(String id, String agrupacio) {
		
		var info = new IntegracioInfo(IntegracioCodiEnum.GESDOC,"Eliminació d'un arxiu", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id), new AccioParam("Agrupacio", agrupacio));
		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);

		try {
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			getPlugin().delete(id, agrupacio);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(codiEntitat);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), errorDescripcio, ex);
		}
	}

	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut) {

		gestioDocumentalGet(id, agrupacio, contingutOut, null);
	}
	
	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut, Boolean isZip) {
		
		var info = new IntegracioInfo(IntegracioCodiEnum.GESDOC, "Consultant arxiu de la gestió documental", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Id del document", id),
				new AccioParam("Agrupacio", agrupacio));
		var codiEntitat = getCodiEntitatActual();
		info.setCodiEntitat(codiEntitat);

		try {
			peticionsPlugin.updatePeticioTotal(codiEntitat);
			var agrupacioPotContenirZip = GESDOC_AGRUPACIO_NOTIFICACIONS.equals(agrupacio) || GESDOC_AGRUPACIO_TEMPORALS.equals(agrupacio) || GESDOC_AGRUPACIO_MASSIUS_ZIP.equals(agrupacio);
			if (isZip == null && agrupacioPotContenirZip) {
				var document = documentRepository.getByArxiuGestdocId(id);
				if (document == null) {
					throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), "El document a recuperar no existeix o és temporal i no s'ha indicat si es tracta d'un document zip");
				}
				isZip = document.isMediaTypeZip();
			}
			getPlugin().get(id, agrupacio, contingutOut, isZip);
			integracioHelper.addAccioOk(info);
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de gestió documental per a obtenir el document amb id: " + (agrupacio != null ? agrupacio + "/" : "") + id;
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			peticionsPlugin.updatePeticioError(codiEntitat);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), errorDescripcio, ex);
		}
	}
	
//	public boolean isGestioDocumentalPluginDisponible() {
//
//		var pluginClass = getPropertyPluginGestioDocumental();
//		if (pluginClass == null || pluginClass.length() == 0) {
//			return false;
//		}
//		try {
//			return getGestioDocumentalPlugin() != null;
//		} catch (SistemaExternException sex) {
//			log.error("Error al obtenir la instància del plugin de gestió documental", sex);
//			return false;
//		}
//	}

	@Override
	protected GestioDocumentalPlugin getPlugin() {

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
			var msg = "La classe del plugin de gestió documental no està configurada";
			log.error(msg);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), msg);
		}
		try {
			Class<?> clazz = Class.forName(pluginClass);
			plugin = (GestioDocumentalPlugin) clazz.getDeclaredConstructor(Properties.class)
					.newInstance(configHelper.getAllEntityProperties(codiEntitat));
			pluginMap.put(codiEntitat, plugin);
			return plugin;
		} catch (Exception ex) {
			var msg = "Error al crear la instància del plugin de gestió documental (" + pluginClass + ") ";
			log.error(msg, ex);
			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), msg, ex);
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
		return configHelper.getConfig("es.caib.notib.plugin.gesdoc.class");
	}

	// Mètodes pels tests
	public void setGestioDocumentalPlugin(GestioDocumentalPlugin gestioDocumentalPlugin) {
		this.pluginMap.put(getCodiEntitatActual(), gestioDocumentalPlugin);
	}
	public void setGestioDocumentalPlugin(Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin) {
		pluginMap = gestioDocumentalPlugin;
	}

}
