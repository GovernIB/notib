package es.caib.notib.plugin.imprimible;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class ImprimiblePluginImpl implements ImprimiblePlugin{

	private IntegracioManager integracioManager;
	private IArxiuPlugin arxiuPlugin;
	private String integracioArxiuCodi = "ARXIU";

	@Override
	public Document documentDescarregar(String identificador, String versio, boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		return arxiuDocumentConsultar(
				identificador,
				versio, 
				ambContingut,
				ambVersioImprimible);
	}

	
	
	private Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		String accioDescripcio = "Obtenint detalls del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuUuid", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		Document documentDetalls = null;
		try {
			documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir detalls del document";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
		if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
			boolean isPdf = false;
			for (Firma firma : documentDetalls.getFirmes()) {
				if (firma.getTipus() == FirmaTipus.PADES) {
					isPdf = true;
				}
			}
			if (isPdf) {
				generarVersioImprimible(documentDetalls);
			}
		}
		return documentDetalls;
	}

	private void generarVersioImprimible(
			Document documentDetalls) throws SistemaExternException {
		String accioDescripcio = "Generant versió imprimible del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("identificador", documentDetalls.getIdentificador());
		long t0 = System.currentTimeMillis();
		try {
			documentDetalls.setContingut(
					getArxiuPlugin().documentImprimible(
							documentDetalls.getIdentificador()));
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al generar la versió imprimible del document";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
	}

	private IArxiuPlugin getArxiuPlugin() throws SistemaExternException {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.distribucio.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							integracioArxiuCodi,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						integracioArxiuCodi,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}
	
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.arxiu.class");
	}
	
	private void integracioAddAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			long tempsResposta) {
		if (integracioManager != null) {
			integracioManager.addAccioOk(
					integracioCodi,
					descripcio,
					parametres,
					tempsResposta);
		}
	}
	
	private void integracioAddAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		if (integracioManager != null) {
			integracioManager.addAccioError(
					integracioCodi,
					descripcio,
					parametres,
					tempsResposta,
					errorDescripcio,
					throwable);
		}
	}
}
