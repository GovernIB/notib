/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.seu.SeuDocument;
import es.caib.notib.plugin.seu.SeuNotificacioEstat;
import es.caib.notib.plugin.seu.SeuNotificacioResultat;
import es.caib.notib.plugin.seu.SeuPersona;
import es.caib.notib.plugin.seu.SeuPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";

	private DadesUsuariPlugin dadesUsuariPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private SeuPlugin seuPlugin;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private IntegracioHelper integracioHelper;



	public DadesUsuari dadesUsuariConsultarAmbCodi(
			String usuariCodi) {
		String accioDescripcio = "Consulta d'usuari amb codi";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().consultarAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(
			String grupCodi) {
		String accioDescripcio = "Consulta d'usuaris d'un grup";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().consultarAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}

	public String gestioDocumentalCreate(
			String agrupacio,
			InputStream contingut) {
		String accioDescripcio = "Creació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("agrupacio", agrupacio);
		accioParams.put("contingut", (contingut != null) ? contingut.toString() : "<null>");
		long t0 = System.currentTimeMillis();
		try {
			String gestioDocumentalId = getGestioDocumentalPlugin().create(
					agrupacio,
							contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	public void gestioDocumentalUpdate(
			String id,
			String agrupacio,
			InputStream contingut) {
		String accioDescripcio = "Modificació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		accioParams.put("contingut", (contingut != null) ? contingut.toString() : "<null>");
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().update(
					id,
					agrupacio,
					contingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		String accioDescripcio = "Eliminació d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().delete(
					id,
					agrupacio);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {
		String accioDescripcio = "Obtenció d'un arxiu";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		long t0 = System.currentTimeMillis();
		try {
			getGestioDocumentalPlugin().get(
					id,
					agrupacio,
					contingutOut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}

	public SeuNotificacioResultat seuNotificacioDestinatariEnviar(
			NotificacioDestinatariEntity notificacioDestinatari) {
		NotificacioEntity notificacio = notificacioDestinatari.getNotificacio();
		String accioDescripcio = "Enviament d'una notificació a la seu electrònica per un destinatari";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", notificacio.getSeuExpedientIdentificadorEni());
		accioParams.put("expedientUnitatOrganitzativa", notificacio.getSeuExpedientUnitatOrganitzativa());
		accioParams.put("expedientSerieDocumental", notificacio.getSeuExpedientSerieDocumental());
		accioParams.put("expedientTitol", notificacio.getSeuExpedientTitol());
		accioParams.put("registreOficina", notificacio.getSeuRegistreOficina());
		accioParams.put("registreLlibre", notificacio.getSeuRegistreLlibre());
		accioParams.put("idioma", notificacio.getSeuIdioma());
		accioParams.put("avisTitol", notificacio.getSeuAvisTitol());
		accioParams.put("oficiTitol", notificacio.getSeuOficiTitol());
		accioParams.put("destinatariNif", notificacioDestinatari.getDestinatariNif());
		long t0 = System.currentTimeMillis();
		try {
			SeuPersona destinatari = new SeuPersona();
			destinatari.setNif(
					notificacioDestinatari.getDestinatariNif());
			destinatari.setNom(
					notificacioDestinatari.getDestinatariNom());
			String llinatges = notificacioDestinatari.getDestinatariLlinatges();
			int espaiIndex = llinatges.indexOf(" ");
			if (espaiIndex != -1) {
				destinatari.setLlinatge1(
						llinatges.substring(0, espaiIndex));
				destinatari.setLlinatge2(
						llinatges.substring(espaiIndex + 1));
			} else {
				destinatari.setLlinatge1(llinatges);
			}
			SeuPersona representat = null;
			String telefonMobil = null;
			if (isTelefonMobil(notificacioDestinatari.getDestinatariTelefon())) {
				telefonMobil = notificacioDestinatari.getDestinatariTelefon();
			}
			getSeuPlugin().comprovarExpedientCreat(
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getProcedimentCodiSia(),
					notificacio.getSeuIdioma(),
					notificacio.getSeuExpedientTitol(),
					destinatari,
					representat,
					null, //bantelNumeroEntrada,
					true,
					notificacioDestinatari.getDestinatariEmail(),
					telefonMobil);
			List<SeuDocument> annexos = new ArrayList<SeuDocument>();
			SeuDocument annex = new SeuDocument();
			annex.setArxiuNom(notificacio.getDocumentArxiuNom());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gestioDocumentalGet(
					notificacio.getDocumentArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			annex.setArxiuContingut(baos.toByteArray());
			annexos.add(annex);
			SeuNotificacioResultat notificacioResultat = getSeuPlugin().notificacioCrear(
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuRegistreLlibre(),
					notificacio.getSeuRegistreOficina(),
					destinatari,
					representat,
					notificacio.getSeuIdioma(),
					notificacio.getSeuOficiTitol(),
					notificacio.getSeuOficiText(),
					notificacio.getSeuAvisTitol(),
					notificacio.getSeuAvisText(),
					notificacio.getSeuAvisTextMobil(),
					true,
					annexos);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return notificacioResultat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de seu electrònica";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SEU,
					errorDescripcio,
					ex);
		}
	}

	public SeuNotificacioEstat seuNotificacioComprovarEstat(
			NotificacioDestinatariEntity notificacioDestinatari) {
		NotificacioEntity notificacio = notificacioDestinatari.getNotificacio();
		String accioDescripcio = "Consulta de l'estat d'una notificació a la seu electrònica";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientId", notificacio.getSeuExpedientIdentificadorEni());
		accioParams.put("expedientUnitatOrganitzativa", notificacio.getSeuExpedientUnitatOrganitzativa());
		accioParams.put("expedientSerieDocumental", notificacio.getSeuExpedientSerieDocumental());
		accioParams.put("expedientTitol", notificacio.getSeuExpedientTitol());
		accioParams.put("registreNumero", notificacioDestinatari.getSeuRegistreNumero());
		long t0 = System.currentTimeMillis();
		try {
			SeuNotificacioEstat notificacioEstat = getSeuPlugin().notificacioObtenirJustificantRecepcio(
					notificacioDestinatari.getSeuRegistreNumero());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return notificacioEstat;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de seu electrònica";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SEU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SEU,
					errorDescripcio,
					ex);
		}
	}

	public boolean isDadesUsuariPluginConfigurat() {
		try {
			return getDadesUsuariPlugin() != null;
		} catch (SistemaExternException sex) {
			logger.error(
					"Error al obtenir la instància del plugin de dades d'usuari",
					sex);
			return false;
		}
	}

	public boolean isGestioDocumentalPluginConfigurat() {
		try {
			return getGestioDocumentalPlugin() != null;
		} catch (SistemaExternException sex) {
			logger.error(
					"Error al obtenir la instància del plugin de gestió documental",
					sex);
			return false;
		}
	}

	public boolean isSeuPluginConfigurat() {
		try {
			return getSeuPlugin() != null;
		} catch (SistemaExternException sex) {
			logger.error(
					"Error al obtenir la instància del plugin de seu electrònica",
					sex);
			return false;
		}
	}



	private boolean isTelefonMobil(String telefonMobil) {
		if (telefonMobil == null) {
			return false;
		}
		String telefonTrim = telefonMobil.replace(" ", "");
		return (
				telefonTrim.startsWith("00346") ||
				telefonTrim.startsWith("+346") ||
				telefonTrim.startsWith("6"));
	}

	private boolean dadesUsuariPluginConfiguracioProvada = false;
	private DadesUsuariPlugin getDadesUsuariPlugin() {
		if (dadesUsuariPlugin == null && !dadesUsuariPluginConfiguracioProvada) {
			dadesUsuariPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_USUARIS,
							"Error al crear la instància del plugin de dades d'usuari",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de dades d'usuari no està configurada");
			}
		}
		return dadesUsuariPlugin;
	}

	private boolean gestioDocumentalPluginConfiguracioProvada = false;
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null && !gestioDocumentalPluginConfiguracioProvada) {
			gestioDocumentalPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}
		}
		return gestioDocumentalPlugin;
	}

	private boolean seuPluginConfiguracioProvada = false;
	private SeuPlugin getSeuPlugin() {
		if (seuPlugin == null && !seuPluginConfiguracioProvada) {
			seuPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginSeu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					seuPlugin = (SeuPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de seu electrònica",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de seu electrònica no està configurada");
			}
		}
		return seuPlugin;
	}

	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesdoc.class");
	}
	private String getPropertyPluginSeu() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.seu.class");
	}

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}
