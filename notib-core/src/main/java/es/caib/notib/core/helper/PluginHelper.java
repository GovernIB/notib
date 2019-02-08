/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.RegistreAnnexDto;
import es.caib.notib.core.api.dto.RegistreAnotacioDto;
import es.caib.notib.core.api.dto.RegistreAnotacioDtoV2;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.RegistreInteressatDto;
import es.caib.notib.core.api.exception.PluginException;
import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.ws.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.core.api.ws.registre.CodiAssumpte;
import es.caib.notib.core.api.ws.registre.DocumentRegistre;
import es.caib.notib.core.api.ws.registre.Llibre;
import es.caib.notib.core.api.ws.registre.Oficina;
import es.caib.notib.core.api.ws.registre.Organisme;
import es.caib.notib.core.api.ws.registre.RegistreAssentament;
import es.caib.notib.core.api.ws.registre.RegistreAssentamentInteressat;
import es.caib.notib.core.api.ws.registre.RegistreDocumentacioFisicaEnum;
import es.caib.notib.core.api.ws.registre.RegistreInteressatTipusEnum;
import es.caib.notib.core.api.ws.registre.RegistrePluginRegWeb3;
import es.caib.notib.core.api.ws.registre.RespostaAnotacioRegistre;
import es.caib.notib.core.api.ws.registre.RespostaJustificantRecepcio;
import es.caib.notib.core.api.ws.registre.TipusAssumpte;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
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
//	private RegistrePlugin registrePlugin;
	private RegistrePluginRegWeb3 registrePluginRegWeb3;


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
	/*
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
	 */
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
//
//	public boolean isRegistrePluginDisponible() {
//		String pluginClass = getPropertyPluginRegistre();
//		if (pluginClass != null && pluginClass.length() > 0) {
//			try {
//				return getRegistrePlugin() != null;
//			} catch (SistemaExternException sex) {
//				logger.error(
//						"Error al obtenir la instància del plugin de seu electrònica",
//						sex);
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}



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
//	
//	private RegistrePlugin getRegistrePlugin() {
//		if (registrePlugin == null) {
//			String pluginClass = getPropertyPluginRegistre();
//			if (pluginClass != null && pluginClass.length() > 0) {
//				try {
//					Class<?> clazz = Class.forName(pluginClass);
//					registrePlugin = (RegistrePlugin)clazz.newInstance();
//				} catch (Exception ex) {
//					throw new SistemaExternException(
//							IntegracioHelper.INTCODI_REGISTRE,
//							"Error al crear la instància del plugin de registre",
//							ex);
//				}
//			} else {
//				throw new SistemaExternException(
//						IntegracioHelper.INTCODI_REGISTRE,
//						"La classe del plugin de registre regweb3 no està configurada");
//			}
//		}
//		return registrePlugin;
//	}

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
	

	// Plugin REGISTRE RegWeb3
	// -------------------------------------------------------------------------------------------------------
	
	public boolean isRegistrePluginRegWeb3Actiu() {
		try {
			return getRegistrePluginRegWeb3() != null;
		} catch (Exception ex) {
			throw new PluginException("No s'ha pogut obtenir el plugin de registre RegWeb3", ex);
		}
	}
	
	public class Valida<T> {
	    private T t;

	    public void set(T t) { this.t = t; }
	    public T get() { return t; }
	    
	    public T notNull(T t, String nom) throws RegistrePluginException {
	    	if (t ==  null) {
	    		throw new RegistrePluginException("El camp " + nom + "no pot ser null.");
	    	}
	    	return t;
	    }
	}
	
	public RegistreIdDto registrarEntrada(
			RegistreAnotacioDto anotacio,
			String aplicacioNom,
			String aplicacioVersio) throws RegistrePluginException {
		
		RegistreAssentament registreEntrada = new RegistreAssentament();
		
		// Emplenar el registreEntrada
		registreEntrada.setOrgan(new Valida<String>().notNull(anotacio.getOrgan(), "Codi destí"));								// Destí - Codi DIR3
//		registreEntrada.setOrganDescripcio(new Valida<String>().notNull(anotacio.getOrganDescripcio(), "Descripció destí"));	// Destí - Denominació DIR3
		registreEntrada.setLlibreCodi(new Valida<String>().notNull(anotacio.getLlibre(), "Llibre"));							// Llibre
		registreEntrada.setOficinaCodi(new Valida<String>().notNull(anotacio.getOficina(), "Oficina"));							// Oficina
		registreEntrada.setExtracte(new Valida<String>().notNull(anotacio.getAssumpteExtracte(), "Extracte"));
		registreEntrada.setAssumpteTipusCodi(new Valida<String>().notNull(anotacio.getAssumpteTipus(), "Tipus assumpte"));
		registreEntrada.setAssumpteCodi(anotacio.getAssumpteCodi());
		registreEntrada.setExpedientNumero(anotacio.getExpedientNumero());
		registreEntrada.setIdiomaCodi(new Valida<String>().notNull(anotacio.getAssumpteIdiomaCodi(), "Idioma"));				// Idioma ('ca', 'es', 'gl', 'eu', 'en')
		registreEntrada.setDocumentacioFisicaCodi(																				// Acompanya documentació física
				anotacio.getDocumentacioFisica() != null ? 																		// 		'01' = Acompanya documentació física requerida.
						anotacio.getDocumentacioFisica().getValor() : 															//		'02' = Acompanya documentació física complementària.
							RegistreDocumentacioFisicaEnum.NO_ACOMPANYA_DOCUMENTACIO.getValor());								//		'03' = No acompanya documentació física ni altres suports.
		registreEntrada.setAplicacioCodi(aplicacioNom);
		registreEntrada.setAplicacioVersio(aplicacioVersio);
		registreEntrada.setUsuariCodi(getUsuariRegistre());
		registreEntrada.setUsuariContacte(null);
		
		registreEntrada.setTransportNumero(null);
		registreEntrada.setTransportTipusCodi("07");						// En cas de ser un registre electrònic aquest camp prendrà el valor Altres (07).
		
		registreEntrada.setReferencia(null);
		registreEntrada.setObservacions(anotacio.getObservacions());
		
		registreEntrada.setExposa(anotacio.getExposa());					// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		registreEntrada.setSolicita(anotacio.getSolicita());				// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		
		// Interessat
		List<RegistreAssentamentInteressat> interessats = new ArrayList<RegistreAssentamentInteressat>();
		
		if (anotacio.getInteressats() != null) {
		
	        for (RegistreInteressatDto inter: anotacio.getInteressats()) {
	        	RegistreAssentamentInteressat interessat = new RegistreAssentamentInteressat();
	
	        	interessat.setTipus(RegistreInteressatTipusEnum.valorAsEnum(inter.getTipus()));
	        	interessat.setDocumentTipus(inter.getDocumentTipus().getValor());
	        	interessat.setDocumentNum(inter.getDocumentNumero());
	        	interessat.setEmail(inter.getEmail());
	        	interessat.setNom(inter.getNom());
	        	interessat.setLlinatge1(inter.getLlinatge1());
	        	interessat.setLlinatge2(inter.getLlinatge2());
	        	interessat.setRaoSocial(inter.getRaoSocial());
	        	interessat.setPais(inter.getPaisCodi());
	        	interessat.setProvincia(inter.getProvinciaCodi());
	        	interessat.setAdresa(inter.getAdressa());
	        	interessat.setCodiPostal(inter.getCodiPostal());
	        	interessat.setMunicipi(inter.getMunicipiCodi());
	        	interessat.setTelefon(inter.getTelefon());
	            
	            // Representant
	            if (inter.getRepresentant() != null) {
	            	RegistreInteressatDto repre = inter.getRepresentant();
	            	RegistreAssentamentInteressat representant = new RegistreAssentamentInteressat();
	            	
	            	representant.setTipus(RegistreInteressatTipusEnum.valorAsEnum(repre.getTipus()));
	            	representant.setDocumentTipus(repre.getDocumentTipus().getValor());
	            	representant.setDocumentNum(repre.getDocumentNumero());
	            	representant.setEmail(repre.getEmail());
	            	representant.setNom(repre.getNom());
	            	representant.setLlinatge1(repre.getLlinatge1());
	            	representant.setLlinatge2(repre.getLlinatge2());
	            	representant.setPais(repre.getPaisCodi());
	            	representant.setProvincia(repre.getProvinciaCodi());
	            	representant.setAdresa(repre.getAdressa());
	            	representant.setCodiPostal(repre.getCodiPostal());
	            	representant.setMunicipi(repre.getMunicipiCodi());
	            	representant.setTelefon(repre.getTelefon());
	            	
	            	interessat.setRepresentant(representant);
	            }
	            
	            interessats.add(interessat);
	        }
		}
        registreEntrada.setInteressats(interessats);
            
        // Annexos
        List<DocumentRegistre> annexos = new ArrayList<DocumentRegistre>();
		if (anotacio.getAnnexos() != null) {
		
	        for (RegistreAnnexDto an: anotacio.getAnnexos()) {
	        	DocumentRegistre annex = new DocumentRegistre();
	        	
	        	annex.setNom(an.getNom());
	        	annex.setTipusDocument(an.getTipusDocument() != null ? an.getTipusDocument().getValor() : null);		
	        																	// annex.setTipoDocumento("02");
			            														//				'01' = Formulari		
			            														//				'02' = Document adjunt al formulari
			            														//				'03' = Fitxer tècnic intern
	        	annex.setTipusDocumental(an.getTipusDocumental().getValor());	// annex.setTipus("TD01");
																				//            	Documentos de decisión:
																				//            		- Resolución.
																				//            		- Acuerdo.
																				//            		- Contrato.
																				//            		- Convenio.
																				//            		- Declaración.
																				//            		Documentos de transmisión:
																				//            		- Comunicación.
																				//            		- Notificación.
																				//            		- Publicación.
																				//            		- Acuse de recibo.
																				//            		Documentos de constancia:
																				//            		- Acta.
																				//            		- Certificado.
																				//            		- Diligencia.
																				//            		Documentos de juicio:
																				//            		- Informe.
																				//            		Documentos de ciudadano:
																				//            		- Solicitud.
																				//            		- Denuncia.
																				//            		- Alegación.
																				//            		- Recursos.
																				//            		- Comunicación ciudadano.
																				//            		- Factura.
																				//            		- Otros incautados.
																				//            		Otros.
	        	annex.setOrigen(an.getOrigen().getValor());						// annex.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
			            														//				0 = Ciutadà
			            														//				1 = aDMINISTRACIÓ
	        	annex.setObservacions(an.getObservacions());					// annex.setObservaciones("");
	        	annex.setModeFirma(an.getModeFirma() != null ? an.getModeFirma().getValor() : null);				
	        																	// annex.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA);
			            														// 				0 = No te firma
			            														//				1 = Autofirma SI
			            														//				2 = Autofirma NO 
	        	annex.setArxiuNom(an.getArxiuNom());
	        	annex.setArxiuContingut(an.getArxiuContingut());
	        	annex.setData(an.getData());
	        	annex.setIdiomaCodi(an.getIdiomaCodi());
	        	
	        	annexos.add(annex);
	        }
		}
        registreEntrada.setDocuments(annexos);
		
		
		RespostaAnotacioRegistre resposta = getRegistrePluginRegWeb3().registrarEntrada(
				registreEntrada, 
				aplicacioNom, 
				aplicacioVersio,
				getPropertyPluginCodiEntitatDir3());
		
		if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().equals(RespostaAnotacioRegistre.ERROR_CODI_OK)) {
			throw new RegistrePluginException("Error al registrar document d'entrada (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
		} else {
			RegistreIdDto registreId = new RegistreIdDto();
			registreId.setNumero(resposta.getNumero());
			registreId.setData(resposta.getData());
			if (resposta.getData() != null) {
	        	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	        	registreId.setHora(sdf.format(resposta.getData()));
	        }
			registreId.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
			return registreId;
		}
	}
	
//	public RespostaConsultaRegistre obtenirRegistreEntrada(
//	String numRegistre, 
//	String usuariCodi,
//	String entitatCodi) throws RegistrePluginException;

	public byte[] obtenirJustificantEntrada(
			Integer anyRegistre,
			Integer numRegistre,
			String llibre,
			String usuariCodi) throws RegistrePluginException {
		
		RespostaJustificantRecepcio resposta = getRegistrePluginRegWeb3().obtenirJustificantEntrada(
				anyRegistre, 
				numRegistre, 
				llibre, 
				usuariCodi, 
				getPropertyPluginCodiEntitatDir3());
		
		return resposta.getJustificant();
	}
	
	public void anularRegistreEntrada(
		String registreNumero,
		String usuariCodi,
		boolean anular) throws RegistrePluginException {
	
	getRegistrePluginRegWeb3().anularRegistreEntrada(
			registreNumero, 
			usuariCodi, 
			getPropertyPluginCodiEntitatDir3(), 
			anular);
	}

	
//	public RegistreIdDto registrarSortida(
//			RegistreAnotacioDto anotacio,
//			String aplicacioNom,
//			String aplicacioVersio) throws RegistrePluginException {
//		
//		RegistreAssentament registreSortida = new RegistreAssentament();
//		
//		// Emplenar el registreSortida
//		registreSortida.setOrgan(anotacio.getOrgan());						// Origen - Codi DIR3
////		registreSortida.setOrganDescripcio(anotacio.getOrganDescripcio());	// Origen - Denominació DIR3
//		registreSortida.setOficinaCodi(anotacio.getOficina());				// Oficina
//		registreSortida.setLlibreCodi(anotacio.getLlibre());				// Llibre
////		registreSortida.setDocumentacioFisicaCodi(anotacio.getDocumentacioFisica().getValor());				
//																			// Acompanya documentació física
//																			// 		'01' = Acompanya documentació física requerida.
//																			//		'02' = Acompanya documentació física complementària.
//																			//		'03' = No acompanya documentació física ni altres suports.
//		registreSortida.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());	// Idioma ('ca', 'es', 'gl', 'eu', 'en')
//		registreSortida.setAplicacioCodi(aplicacioNom);
//		registreSortida.setAplicacioVersio(aplicacioVersio);
//		registreSortida.setUsuariCodi(getUsuariRegistre());
//		registreSortida.setUsuariContacte(null);
//		registreSortida.setExpedientNumero(anotacio.getExpedientNumero());
//		registreSortida.setTransportNumero(null);
//		registreSortida.setTransportTipusCodi("07");						// En cas de ser un registre electrònic aquest camp prendrà el valor Altres (07).
//		registreSortida.setExtracte(anotacio.getAssumpteExtracte());
//		registreSortida.setAssumpteTipusCodi(anotacio.getAssumpteTipus());
//		registreSortida.setAssumpteCodi(anotacio.getAssumpteCodi());
//		registreSortida.setReferencia(null);
//		registreSortida.setObservacions(anotacio.getObservacions());
//		
//		registreSortida.setExposa(anotacio.getExposa());					// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
//		registreSortida.setSolicita(anotacio.getSolicita());				// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
//		
//		// Interessat
//        for (RegistreInteressatDto inter: anotacio.getInteressats()) {
//        	RegistreAssentamentInteressat interessat = new RegistreAssentamentInteressat();
//
//        	interessat.setTipus(RegistreInteressatTipusEnum.valorAsEnum(inter.getTipus()));
//        	interessat.setDocumentTipus(inter.getDocumentTipus().getValor());
//        	interessat.setDocumentNum(inter.getDocumentNumero());
//        	interessat.setEmail(inter.getEmail());
//        	interessat.setNom(inter.getNom());
//        	interessat.setLlinatge1(inter.getLlinatge1());
//        	interessat.setLlinatge2(inter.getLlinatge2());
//        	interessat.setPais(inter.getPaisCodi());
//        	interessat.setRaoSocial(inter.getRaoSocial());
//        	interessat.setProvincia(inter.getProvinciaCodi());
//        	interessat.setAdresa(inter.getAdressa());
//        	interessat.setCodiPostal(inter.getCodiPostal());
//        	interessat.setMunicipi(inter.getMunicipiCodi());
//        	interessat.setTelefon(inter.getTelefon());
//            
//            // Representant
//            if (inter.getRepresentant() != null) {
//            	RegistreInteressatDto repre = inter.getRepresentant();
//            	RegistreAssentamentInteressat representant = new RegistreAssentamentInteressat();
//            	
//            	representant.setTipus(RegistreInteressatTipusEnum.valorAsEnum(repre.getTipus()));
//            	representant.setDocumentTipus(repre.getDocumentTipus().getValor());
//            	representant.setDocumentNum(repre.getDocumentNumero());
//            	representant.setEmail(repre.getEmail());
//            	representant.setNom(repre.getNom());
//            	representant.setLlinatge1(repre.getLlinatge1());
//            	representant.setLlinatge2(repre.getLlinatge2());
//            	representant.setRaoSocial(repre.getRaoSocial());
//            	representant.setPais(repre.getPaisCodi());
//            	representant.setProvincia(repre.getProvinciaCodi());
//            	representant.setAdresa(repre.getAdressa());
//            	representant.setCodiPostal(repre.getCodiPostal());
//            	representant.setMunicipi(repre.getMunicipiCodi());
//            	representant.setTelefon(repre.getTelefon());
//            	
//            	interessat.setRepresentant(representant);
//            }
//            
//            registreSortida.getInteressats().add(interessat);
//        }
//            
//        for (RegistreAnnexDto an: anotacio.getAnnexos()) {
//        	DocumentRegistre annex = new DocumentRegistre();
//        	
//        	annex.setNom(an.getNom());
//        	annex.setTipusDocument(an.getTipusDocument() != null ? an.getTipusDocument().getValor() : null);		
//        																	// annex.setTipoDocumento("02");
//		            														//				'01' = Formulari		
//		            														//				'02' = Document adjunt al formulari
//		            														//				'03' = Fitxer tècnic intern
//        	annex.setTipusDocumental(an.getTipusDocumental().getValor());	// annex.setTipus("TD01");
//																			//            	Documentos de decisión:
//																			//            		- Resolución.
//																			//            		- Acuerdo.
//																			//            		- Contrato.
//																			//            		- Convenio.
//																			//            		- Declaración.
//																			//            		Documentos de transmisión:
//																			//            		- Comunicación.
//																			//            		- Notificación.
//																			//            		- Publicación.
//																			//            		- Acuse de recibo.
//																			//            		Documentos de constancia:
//																			//            		- Acta.
//																			//            		- Certificado.
//																			//            		- Diligencia.
//																			//            		Documentos de juicio:
//																			//            		- Informe.
//																			//            		Documentos de ciudadano:
//																			//            		- Solicitud.
//																			//            		- Denuncia.
//																			//            		- Alegación.
//																			//            		- Recursos.
//																			//            		- Comunicación ciudadano.
//																			//            		- Factura.
//																			//            		- Otros incautados.
//																			//            		Otros.
//        	annex.setOrigen(an.getOrigen().getValor());						// annex.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
//		            														//				0 = Ciutadà
//		            														//				1 = aDMINISTRACIÓ
//        	annex.setObservacions(an.getObservacions());					// annex.setObservaciones("");
//        	annex.setModeFirma(an.getModeFirma() != null ? an.getModeFirma().getValor() : null);				
//        																	// annex.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA);
//		            														// 				0 = No te firma
//		            														//				1 = Autofirma SI
//		            														//				2 = Autofirma NO 
//        	annex.setArxiuNom(an.getArxiuNom());
//        	annex.setArxiuContingut(an.getArxiuContingut());
//        	annex.setData(an.getData());
//        	annex.setIdiomaCodi(an.getIdiomaCodi());
//        	
//        	registreSortida.getDocuments().add(annex);
//        }
//		
//		RespostaAnotacioRegistre resposta = getRegistrePluginRegWeb3().registrarSortida(
//				registreSortida, 
//				aplicacioNom, 
//				aplicacioVersio,
//				getPropertyPluginCodiEntitatDir3());
//		
//		if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().equals(RespostaAnotacioRegistre.ERROR_CODI_OK)) {
//			throw new RegistrePluginException("Error al registrar document d'entrada (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
//		} else {
//			RegistreIdDto registreId = new RegistreIdDto();
//			registreId.setNumero(resposta.getNumero());
//			registreId.setData(resposta.getData());
//			registreId.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
//			return registreId;
//		}
//	}
	
	public RegistreIdDto registrarSortida(
			RegistreAnotacioDto anotacio,
			String aplicacioNom,
			String aplicacioVersio) throws RegistrePluginException {
		
		RegistreAssentament registreSortida = new RegistreAssentament();
		
		// Emplenar el registreSortida
		registreSortida.setOrgan(anotacio.getOrgan());						// Origen - Codi DIR3
//		registreSortida.setOrganDescripcio(anotacio.getOrganDescripcio());	// Origen - Denominació DIR3
		registreSortida.setOficinaCodi(anotacio.getOficina());				// Oficina
		registreSortida.setLlibreCodi(anotacio.getLlibre());				// Llibre
//		registreSortida.setDocumentacioFisicaCodi(anotacio.getDocumentacioFisica().getValor());				
																			// Acompanya documentació física
																			// 		'01' = Acompanya documentació física requerida.
																			//		'02' = Acompanya documentació física complementària.
																			//		'03' = No acompanya documentació física ni altres suports.
		registreSortida.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());	// Idioma ('ca', 'es', 'gl', 'eu', 'en')
		registreSortida.setAplicacioCodi(aplicacioNom);
		registreSortida.setAplicacioVersio(aplicacioVersio);
		registreSortida.setUsuariCodi(getUsuariRegistre());
		registreSortida.setUsuariContacte(null);
		registreSortida.setExpedientNumero(anotacio.getExpedientNumero());
		registreSortida.setTransportNumero(null);
		registreSortida.setTransportTipusCodi("07");						// En cas de ser un registre electrònic aquest camp prendrà el valor Altres (07).
		registreSortida.setExtracte(anotacio.getAssumpteExtracte());
		registreSortida.setAssumpteTipusCodi(anotacio.getAssumpteTipus());
		registreSortida.setAssumpteCodi(anotacio.getAssumpteCodi());
		registreSortida.setReferencia(null);
		registreSortida.setObservacions(anotacio.getObservacions());
		
		registreSortida.setExposa(anotacio.getExposa());					// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		registreSortida.setSolicita(anotacio.getSolicita());				// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		
		// Interessat
        for (RegistreInteressatDto inter: anotacio.getInteressats()) {
        	RegistreAssentamentInteressat interessat = new RegistreAssentamentInteressat();

        	interessat.setTipus(RegistreInteressatTipusEnum.valorAsEnum(inter.getTipus()));
        	interessat.setDocumentTipus(inter.getDocumentTipus().getValor());
        	interessat.setDocumentNum(inter.getDocumentNumero());
        	interessat.setEmail(inter.getEmail());
        	interessat.setNom(inter.getNom());
        	interessat.setLlinatge1(inter.getLlinatge1());
        	interessat.setLlinatge2(inter.getLlinatge2());
        	interessat.setPais(inter.getPaisCodi());
        	interessat.setRaoSocial(inter.getRaoSocial());
        	interessat.setProvincia(inter.getProvinciaCodi());
        	interessat.setAdresa(inter.getAdressa());
        	interessat.setCodiPostal(inter.getCodiPostal());
        	interessat.setMunicipi(inter.getMunicipiCodi());
        	interessat.setTelefon(inter.getTelefon());
            
            // Representant
            if (inter.getRepresentant() != null) {
            	RegistreInteressatDto repre = inter.getRepresentant();
            	RegistreAssentamentInteressat representant = new RegistreAssentamentInteressat();
            	
            	representant.setTipus(RegistreInteressatTipusEnum.valorAsEnum(repre.getTipus()));
            	representant.setDocumentTipus(repre.getDocumentTipus().getValor());
            	representant.setDocumentNum(repre.getDocumentNumero());
            	representant.setEmail(repre.getEmail());
            	representant.setNom(repre.getNom());
            	representant.setLlinatge1(repre.getLlinatge1());
            	representant.setLlinatge2(repre.getLlinatge2());
            	representant.setRaoSocial(repre.getRaoSocial());
            	representant.setPais(repre.getPaisCodi());
            	representant.setProvincia(repre.getProvinciaCodi());
            	representant.setAdresa(repre.getAdressa());
            	representant.setCodiPostal(repre.getCodiPostal());
            	representant.setMunicipi(repre.getMunicipiCodi());
            	representant.setTelefon(repre.getTelefon());
            	
            	interessat.setRepresentant(representant);
            }
            
            registreSortida.getInteressats().add(interessat);
        }
            
        for (RegistreAnnexDto an: anotacio.getAnnexos()) {
        	DocumentRegistre annex = new DocumentRegistre();
        	
        	annex.setNom(an.getNom());
        	annex.setTipusDocument(an.getTipusDocument() != null ? an.getTipusDocument().getValor() : null);		
        																	// annex.setTipoDocumento("02");
		            														//				'01' = Formulari		
		            														//				'02' = Document adjunt al formulari
		            														//				'03' = Fitxer tècnic intern
        	annex.setTipusDocumental(an.getTipusDocumental().getValor());	// annex.setTipus("TD01");
																			//            	Documentos de decisión:
																			//            		- Resolución.
																			//            		- Acuerdo.
																			//            		- Contrato.
																			//            		- Convenio.
																			//            		- Declaración.
																			//            		Documentos de transmisión:
																			//            		- Comunicación.
																			//            		- Notificación.
																			//            		- Publicación.
																			//            		- Acuse de recibo.
																			//            		Documentos de constancia:
																			//            		- Acta.
																			//            		- Certificado.
																			//            		- Diligencia.
																			//            		Documentos de juicio:
																			//            		- Informe.
																			//            		Documentos de ciudadano:
																			//            		- Solicitud.
																			//            		- Denuncia.
																			//            		- Alegación.
																			//            		- Recursos.
																			//            		- Comunicación ciudadano.
																			//            		- Factura.
																			//            		- Otros incautados.
																			//            		Otros.
        	annex.setOrigen(an.getOrigen().getValor());						// annex.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
		            														//				0 = Ciutadà
		            														//				1 = aDMINISTRACIÓ
        	annex.setObservacions(an.getObservacions());					// annex.setObservaciones("");
        	annex.setModeFirma(an.getModeFirma() != null ? an.getModeFirma().getValor() : null);				
        																	// annex.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA);
		            														// 				0 = No te firma
		            														//				1 = Autofirma SI
		            														//				2 = Autofirma NO 
        	annex.setArxiuNom(an.getArxiuNom());
        	annex.setArxiuContingut(an.getArxiuContingut());
        	annex.setData(an.getData());
        	annex.setIdiomaCodi(an.getIdiomaCodi());
        	
        	registreSortida.getDocuments().add(annex);
        }
//		
//		RespostaAnotacioRegistre resposta = getRegistrePluginRegWeb3().registrarSortida(
//				registreSortida, 
//				aplicacioNom, 
//				aplicacioVersio,
//				getPropertyPluginCodiEntitatDir3());
//		
//		if (resposta.getErrorCodi() != null && !resposta.getErrorCodi().equals(RespostaAnotacioRegistre.ERROR_CODI_OK)) {
//			throw new RegistrePluginException("Error al registrar document d'entrada (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
//		} else {
//			RegistreIdDto registreId = new RegistreIdDto();
//			registreId.setNumero(resposta.getNumero());
//			registreId.setData(resposta.getData());
//			registreId.setNumeroRegistreFormat(resposta.getNumeroRegistroFormateado());
//			return registreId;
//		}
        return new RegistreIdDto();
	}
	
//	public RespostaConsultaRegistre obtenirRegistreSortida(
//	String numRegistre, 
//	String usuariCodi,
//	String entitatCodi) throws RegistrePluginException;
	
	public void anularRegistreSortida(
			String registreNumero,
			String usuariCodi,
			boolean anular) throws RegistrePluginException {
		
		getRegistrePluginRegWeb3().anularRegistreSortida(
				registreNumero, 
				usuariCodi, 
				getPropertyPluginCodiEntitatDir3(), 
				anular);
		
	}

	
	public List<TipusAssumpte> llistarTipusAssumpte() throws RegistrePluginException {
		
		List<TipusAssumpte> tipusAssumptes = getRegistrePluginRegWeb3().llistarTipusAssumpte(
				getPropertyPluginCodiEntitatDir3());
		
		return tipusAssumptes;
	}

	public List<CodiAssumpte> llistarCodisAssumpte(
			String tipusAssumpte) throws RegistrePluginException {
		
		List<CodiAssumpte> assumptes = getRegistrePluginRegWeb3().llistarCodisAssumpte(
				getPropertyPluginCodiEntitatDir3(), 
				tipusAssumpte);

		return assumptes;
	}
	
	public List<Oficina> llistarOficines(
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Oficina> oficines = getRegistrePluginRegWeb3().llistarOficines(
				getPropertyPluginCodiEntitatDir3(), 
				autoritzacio.getValor());
	
		return oficines;
	}
	
	public List<Llibre> llistarLlibres(
			String oficina,
			AutoritzacioRegiWeb3Enum autoritzacio) throws RegistrePluginException {
		
		List<Llibre> llibres = getRegistrePluginRegWeb3().llistarLlibres(
				getPropertyPluginCodiEntitatDir3(), 
				oficina, 
				autoritzacio.getValor());
	
		return llibres;
	}
	
	public List<Organisme> llistarOrganismes() throws RegistrePluginException {
		
		List<Organisme> organismes = getRegistrePluginRegWeb3().llistarOrganismes(
				getPropertyPluginCodiEntitatDir3());
	
		return organismes;
	}
	
	private RegistrePluginRegWeb3 getRegistrePluginRegWeb3() {
		if (registrePluginRegWeb3 == null) {
			String pluginClass = getPropertyPluginRegistreRegWeb3();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePluginRegWeb3 = (RegistrePluginRegWeb3)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException("Error al crear la instància del plugin de registre de RegWeb3", ex);
				}
			} else {
				throw new PluginException("La classe del plugin de registre de RegWeb3 no està configurada");
			}
		}
		return registrePluginRegWeb3;
	}
	private String getPropertyPluginRegistreRegWeb3() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.class");
	}
	private String getPropertyPluginCodiEntitatDir3() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.regweb.entitat.dir3");
	}
	private String getUsuariRegistre() {
		String usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null)
			usuari = auth.getName();
		return usuari;
	}
	
	public RegistreAnotacioDto notificacioToRegistreAnotacio(NotificacioEntity notificaio) {
		return null;
	}
	
	public RegistreAnotacioDtoV2 notificacioToRegistreAnotacioV2(NotificacioEntity notificaio) {
		return null;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}
