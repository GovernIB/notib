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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.sortida.RegistrePlugin;
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
	private RegistrePlugin registrePlugin;

	@Autowired
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
			NotificacioEnviamentEntity notificacioDestinatari) {
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
		boolean isNotificacio = NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus());
		long t0 = System.currentTimeMillis();
		try {
			SeuPersona representant = new SeuPersona();
			SeuPersona representat = null;
			
			if (notificacioDestinatari.getDestinatariNif() != null) {
				// Representant
				representant.setNif(notificacioDestinatari.getDestinatariNif());
				representant.setNom(notificacioDestinatari.getDestinatariNom());
				representant.setLlinatge1(notificacioDestinatari.getDestinatariLlinatge1());
				representant.setLlinatge2(notificacioDestinatari.getDestinatariLlinatge2());
				// Representat
				representat = new SeuPersona();
				representat.setNif(notificacioDestinatari.getTitularNif());
				representat.setNom(notificacioDestinatari.getTitularNom());
				representat.setLlinatge1(notificacioDestinatari.getTitularLlinatge1());
				representat.setLlinatge2(notificacioDestinatari.getTitularLlinatge2());
			} else {
				// Representant
				representant.setNif(notificacioDestinatari.getTitularNif());
				representant.setNom(notificacioDestinatari.getTitularNom());
				representant.setLlinatge1(notificacioDestinatari.getTitularLlinatge1());
				representant.setLlinatge2(notificacioDestinatari.getTitularLlinatge2());
			}
			
			
			
			String telefonMobil = null;
			if (isTelefonMobil(notificacioDestinatari.getDestinatariTelefon())) {
				telefonMobil = notificacioDestinatari.getDestinatariTelefon();
			}
			getSeuPlugin().comprovarExpedientCreat(
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuProcedimentCodi(),
					notificacio.getSeuIdioma(),
					notificacio.getSeuExpedientTitol(),
					representant,
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
					notificacio.getSeuRegistreOrgan(),
					representant,
					representat,
					notificacio.getSeuIdioma(),
					notificacio.getSeuOficiTitol(),
					notificacio.getSeuOficiText(),
					notificacio.getSeuAvisTitol(),
					notificacio.getSeuAvisText(),
					notificacio.getSeuAvisTextMobil(),
					notificacio.getCaducitat(),
					isNotificacio,
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
			NotificacioEnviamentEntity notificacioDestinatari) {
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
			if (ex.getMessage().contains("No existeix la notificació"))
				errorDescripcio = "Error al consultar la notificació. No existeix la notificació";
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
	
	public SeuDocument obtenirJustificant(NotificacioEnviamentEntity notificacioDestinatari) {
		
		SeuDocument seuDocument = null;
		try {
			seuDocument = getSeuPlugin().notificacioObtenirFitxerJustificantRecepcio(
					notificacioDestinatari.getSeuFitxerCodi(),
					notificacioDestinatari.getSeuFitxerClau());
		} catch (Exception ex) {
			// TODO: handle exception
		}
		return seuDocument;
	}

	/*public RespostaAnotacioRegistre registreSortida(Notificacio notificacio) {
		String accioDescripcio = "Anotació al registre de sortida";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("notificacio.cifEntitat", notificacio.getCifEntitat());
		accioParams.put("notificacio.concepte", notificacio.getConcepte());
		accioParams.put("notificacio.enviamentTipus", notificacio.getEnviamentTipus().getText());
		long t0 = System.currentTimeMillis();
		try {
			RespostaAnotacioRegistre resposta = null;
			if (	getPropertyRegistrePluginDesactivat() &&
					!getPropertyRegistrePluginObligatori()) return null;
			if (	getPropertyRegistrePluginObligatori() ||
					notificacio.isRegistreEnviar()) {
				RegistreAssentament registreSortida = new RegistreAssentament();
				List<RegistreAssentamentInteressat> registreAssentamentInteressat = new ArrayList<RegistreAssentamentInteressat>();
				DocumentRegistre document = new DocumentRegistre();
				EntitatEntity entitat = entitatRepository.findByDir3Codi(notificacio.getCifEntitat());
				registreSortida.setOrganisme(entitat.getDir3Codi());
				registreSortida.setOficina(notificacio.getRegistreOficina());
				registreSortida.setLlibre(notificacio.getRegistreLlibre());
				registreSortida.setExtracte(notificacio.getSeuAvisText()); // Preguntar si es correcte
				registreSortida.setAssumpteTipus(notificacio.getRegistreTipusAssumpte());
				registreSortida.setAssumpteCodi(notificacio.getRegistreCodiAssumpte());
				registreSortida.setIdioma(notificacio.getRegistreIdioma());
				registreSortida.setDocumentacioFisicaCodi(notificacio.getDocumentacioFisicaCodi());
				for (NotificacioDestinatari destinatari: notificacio.getDestinataris()) {
					RegistreAssentamentInteressat interessat = new RegistreAssentamentInteressat();
					if (esPersonaJuridica(destinatari.getDestinatariNif())) {
						interessat.setTipus("3");
						interessat.setDocumentTipus("CIF");
						interessat.setRaoSocial(destinatari.getDestinatariNom());
					} else {
						interessat.setTipus("2");
						interessat.setDocumentTipus("NIF");
						interessat.setNom(destinatari.getDestinatariNom());
					}
					interessat.setDocumentNum(destinatari.getDestinatariNif());
					interessat.setLlinatge1(destinatari.getDestinatariLlinatge1());
					interessat.setLlinatge2(destinatari.getDestinatariLlinatge2());
					interessat.setPais(destinatari.getDomiciliPaisCodiIso());
					interessat.setProvincia(destinatari.getDomiciliProvinciaCodi());
					interessat.setMunicipi(destinatari.getDomiciliMunicipiCodiIne());
					interessat.setAdresa(destinatari.getDireccio());
					interessat.setCodiPostal(destinatari.getDomiciliCodiPostal());
					interessat.setEmail(destinatari.getDestinatariEmail());
					interessat.setTelefon(destinatari.getDestinatariTelefon());
					registreAssentamentInteressat.add(interessat);
				}
				document.setTitol("Document de la notificació");
				document.setArxiuNom(notificacio.getDocumentArxiuNom());
				byte[] contingut = Base64.decode(notificacio.getDocumentContingutBase64());
				document.setArxiuContingut(contingut);
				document.setArxiuMida(contingut.length);
				InputStream is = new BufferedInputStream(new ByteArrayInputStream(contingut));
				String mimeType = URLConnection.guessContentTypeFromStream(is);
				document.setTipusMIMEFitxerAnexat(mimeType);
				document.setTipusDocumental(notificacio.getRegistreTipusDocumental());
				document.setOrigenCiutadaAdmin(notificacio.getRegistreOrigenCiutadaAdmin());
				document.setDataCaptura(notificacio.getRegistreDataCaptura());
				registreSortida.setInteressats(registreAssentamentInteressat);
				registreSortida.setDocument(document);
				resposta = getRegistrePlugin().registrarSortida(registreSortida);
			}
			return resposta;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de registre";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_REGISTRE,
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
	}*/

	public boolean isDadesUsuariPluginDisponible() {
		String pluginClass = getPropertyPluginDadesUsuari();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getDadesUsuariPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de dades d'usuari",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isGestioDocumentalPluginDisponible() {
		String pluginClass = getPropertyPluginGestioDocumental();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getGestioDocumentalPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de gestió documental",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isSeuPluginDisponible() {
		String pluginClass = getPropertyPluginSeu();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getSeuPlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de seu electrònica",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isRegistrePluginDisponible() {
		String pluginClass = getPropertyPluginRegistre();
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				return getRegistrePlugin() != null;
			} catch (SistemaExternException sex) {
				logger.error(
						"Error al obtenir la instància del plugin de seu electrònica",
						sex);
				return false;
			}
		} else {
			return false;
		}
	}



	/*private boolean esPersonaJuridica(String codiId) {
		String letrasCif = "ABCDEFGHJKLMNPQRSVW";
		String primeraLletraCif = codiId.toUpperCase().substring(0, 1);
        return letrasCif.contains(primeraLletraCif);
	}*/

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
	
	private RegistrePlugin getRegistrePlugin() {
		if (registrePlugin == null) {
			String pluginClass = getPropertyPluginRegistre();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_REGISTRE,
							"Error al crear la instància del plugin de registre",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_REGISTRE,
						"La classe del plugin de registre regweb3 no està configurada");
			}
		}
		return registrePlugin;
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
	private String getPropertyPluginRegistre() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.sortida");
	}
	/*private boolean getPropertyRegistrePluginDesactivat() {
		return "true".equals(PropertiesHelper.getProperties().getProperty("es.caib.notib.registre.sortida.desactivat"));
	}
	private boolean getPropertyRegistrePluginObligatori() {
		return "true".equals(PropertiesHelper.getProperties().getProperty("es.caib.notib.registre.sortida.obligatori"));
	}*/
	public int getNotificaReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.notifica.enviaments.reintents.maxim", 5);
	}
	public int getNotificaReintentsPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.notifica.enviaments.periode", 300000);
	}
	public int getSeuReintentsMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.enviaments.reintents.maxim", 5);
	}
	public int getSeuReintentsEnviamentPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.enviaments.periode", 300000);
	}
	public int getSeuReintentsConsultaPeriodeProperty() {
		return PropertiesHelper.getProperties().getAsInt("es.caib.notib.tasca.seu.consulta.periode", 300000);
	}
	public NotificacioComunicacioTipusEnumDto getNotibTipusComunicacioDefecte() {
		NotificacioComunicacioTipusEnumDto tipus = NotificacioComunicacioTipusEnumDto.SINCRON;
		
		try {
			String tipusStr = PropertiesHelper.getProperties().getProperty("es.caib.notib.comunicacio.tipus.defecte", "SINCRON");
			if (tipusStr != null && !tipusStr.isEmpty())
				tipus = NotificacioComunicacioTipusEnumDto.valueOf(tipusStr);
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el tipus de comunicació per defecte. S'utilitzarà el tipus SINCRON.");
		}
				
		return tipus;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}
