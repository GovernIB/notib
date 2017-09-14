package es.caib.notib.plugin.registre.sortida;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import es.caib.notib.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.notib.plugin.registre.sortida.DocumentRegistre;
import es.caib.notib.plugin.registre.sortida.RegistreAssentament;
import es.caib.notib.plugin.registre.sortida.RegistreAssentamentInteressat;
import es.caib.notib.plugin.registre.sortida.RegistrePluginException;
import es.caib.notib.plugin.registre.sortida.RegistrePlugin;
import es.caib.notib.plugin.registre.sortida.RespostaAnotacioRegistre;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.regweb3.ws.api.v3.AnexoWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.IdentificadorWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWsService;
import es.caib.regweb3.ws.api.v3.RegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;

public class RegistrePluginRegweb3Imp implements RegistrePlugin {
	
	private static final int MODO_FIRMA_ATTACHED = 1;
	private final static String TIPUS_DOCUMENT_ADJUNT = "02";
	
	protected static RegWebRegistroSalidaWs registroSalidaApi;

	@Override
	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida)
			throws RegistrePluginException {

		RegistroSalidaWs registroSalidaWs = new RegistroSalidaWs();
		registroSalidaWs.setOrigen(registreSortida.getOrganisme());
		registroSalidaWs.setOficina(registreSortida.getOficina());
		registroSalidaWs.setLibro(registreSortida.getLlibre());
		registroSalidaWs.setExtracto(registreSortida.getExtracte());
		registroSalidaWs.setTipoAsunto(registreSortida.getAssumpteTipus());
		registroSalidaWs.setCodigoAsunto(registreSortida.getAssumpteCodi());
		registroSalidaWs.setIdioma(registreSortida.getIdioma());
		registroSalidaWs.setCodigoUsuario(getAppUserName());
		registroSalidaWs.setDocFisica(registreSortida.getDocumentacioFisicaCodi() != null ? new Long(registreSortida.getDocumentacioFisicaCodi()) : (long)3);
		
		
		for (RegistreAssentamentInteressat inter : registreSortida.getInteressats()) {
			
			InteresadoWs interesadoWs = new InteresadoWs();
			DatosInteresadoWs datosInteresadoWs = new DatosInteresadoWs();
			
			datosInteresadoWs.setTipoInteresado( Long.parseLong(inter.getTipus()) );
			datosInteresadoWs.setTipoDocumentoIdentificacion(inter.getDocumentTipus());
			datosInteresadoWs.setDocumento(inter.getDocumentNum());
			datosInteresadoWs.setRazonSocial(inter.getRaoSocial());
			datosInteresadoWs.setNombre(inter.getNom());
			datosInteresadoWs.setApellido1(inter.getLlinatge1());
			datosInteresadoWs.setApellido2(inter.getLlinatge2());
			datosInteresadoWs.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
			datosInteresadoWs.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
			datosInteresadoWs.setLocalidad(inter.getMunicipi() != null ? new Long(inter.getMunicipi()) : null);
			datosInteresadoWs.setDireccion(inter.getAdresa());
			datosInteresadoWs.setCp(inter.getCodiPostal());
			datosInteresadoWs.setEmail(inter.getEmail());
			datosInteresadoWs.setTelefono(inter.getTelefon());
			
			interesadoWs.setInteresado(datosInteresadoWs);

			registroSalidaWs.getInteresados().add(interesadoWs);
		}
		
		
		DocumentRegistre document = registreSortida.getDocument();
		AnexoWs anexoWs = new AnexoWs();

		anexoWs.setTitulo(document.getTitol());
		anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
		anexoWs.setFicheroAnexado(document.getArxiuContingut());
		//tamaño fichero anexado
		anexoWs.setTipoMIMEFicheroAnexado(document.getTipusMIMEFitxerAnexat());
		anexoWs.setTipoDocumental(document.getTipusDocumental());
		anexoWs.setOrigenCiudadanoAdmin(document.getOrigenCiutadaAdmin());
		anexoWs.setFechaCaptura(new Timestamp(document.getDataCaptura().getTime()));
		anexoWs.setModoFirma(MODO_FIRMA_ATTACHED);
		anexoWs.setTipoDocumento(TIPUS_DOCUMENT_ADJUNT);
		
		registroSalidaWs.getAnexos().add(anexoWs);
		
		
		RespostaAnotacioRegistre resposta = null;
		try {
			registroSalidaApi = getRegistroSalidaApi();
			IdentificadorWs identificadorWs = registroSalidaApi.altaRegistroSalida(registroSalidaWs);
			resposta = new RespostaAnotacioRegistre();
			resposta.setData(identificadorWs.getFecha());
			resposta.setNumero(identificadorWs.getNumero().toString());
			resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
		} catch (WsI18NException e) {
			String msg = WsClientUtils.toString(e);
			throw new RegistrePluginException("Error WsI18NException: " + msg);
		} catch (WsValidationException e) {
			String msg = WsClientUtils.toString(e);
			throw new RegistrePluginException("Error WsValidationException: " + msg);
		} catch (Exception e) {
			throw new RegistrePluginException("Error WsValidationException: " + e);
		}
		return resposta;
	}

	private static RegWebRegistroSalidaWs getRegistroSalidaApi() throws Exception {

		final String endpoint = getEndPoint();

		final URL wsdl = new URL(endpoint + "?wsdl");
		RegWebRegistroSalidaWsService service = new RegWebRegistroSalidaWsService(wsdl);
		RegWebRegistroSalidaWs api = service.getRegWebRegistroSalidaWs();

		Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, getAppUserName());
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, getAppPassword());

		return api;
	}

	private static String getEndPoint() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.sortida.host");
	}

	private static String getAppUserName() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.sortida.usuari");
	}

	private static String getAppPassword() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.sortida.password");
	}
	

}

// package net.conselldemallorca.helium.integracio.plugins.registre;
//
// import java.io.BufferedInputStream;
// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.net.URLConnection;
// import java.sql.Timestamp;
//
// import org.junit.BeforeClass;
//
// import es.caib.regweb3.ws.api.v3.AnexoWs;
// import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
// import es.caib.regweb3.ws.api.v3.IdentificadorWs;
// import es.caib.regweb3.ws.api.v3.InteresadoWs;
// import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWs;
// import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWs;
// import es.caib.regweb3.ws.api.v3.RegistroSalidaResponseWs;
// import es.caib.regweb3.ws.api.v3.RegistroSalidaWs;
// import es.caib.regweb3.ws.api.v3.WsI18NException;
// import es.caib.regweb3.ws.api.v3.WsValidationException;
// import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;
// import
// net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressatTipusEnum;
//
//
/// **
// * Implementació del plugin de registre per a la interficie de
// * serveis web del registre de la CAIB.
// *
// * @author Limit Tecnologies <limit@limit.es>
// */
//
// public class RegistrePluginRegweb3 extends RegWeb3Utils implements
// RegistrePluginRegWeb3{
//
// protected static RegWebRegistroEntradaWs registroEntradaApi;
// protected static RegWebRegistroSalidaWs registroSalidaApi;
//
// @BeforeClass
// public static void setUpBeforeClass() throws Exception {
// registroSalidaApi = getRegistroSalidaApi();
// }
//
// public RespostaAnotacioRegistre registrarSortida(
// RegistreAssentament registreSortida,
// String aplicacioNom,
// String aplicacioVersio) throws RegistrePluginException {
// RegistroSalidaWs registroSalidaWs = new RegistroSalidaWs();
//
// registroSalidaWs.setOrigen(registreSortida.getOrgan());
// registroSalidaWs.setOficina(registreSortida.getOficinaCodi());
// registroSalidaWs.setLibro(registreSortida.getLlibreCodi());
//
// registroSalidaWs.setExtracto(registreSortida.getExtracte());
// registroSalidaWs.setDocFisica(registreSortida.getDocumentacioFisicaCodi() !=
// null ? new Long(registreSortida.getDocumentacioFisicaCodi()) : (long)3);
// registroSalidaWs.setIdioma(registreSortida.getIdiomaCodi());
// registroSalidaWs.setTipoAsunto(registreSortida.getAssumpteTipusCodi());
//
// registroSalidaWs.setAplicacion(aplicacioNom);
// registroSalidaWs.setVersion(aplicacioVersio);
//
// registroSalidaWs.setCodigoUsuario(registreSortida.getUsuariCodi());
// registroSalidaWs.setContactoUsuario(registreSortida.getUsuariContacte());
//
// registroSalidaWs.setNumExpediente(registreSortida.getExpedientNumero());
//// registroSalidaWs.setNumTransporte("");
//// registroSalidaWs.setObservaciones("");
//
//// registroSalidaWs.setRefExterna("");
//// registroSalidaWs.setCodigoAsunto(null);
//// registroSalidaWs.setTipoTransporte("");
//
//// registroSalidaWs.setExpone("expone");
//// registroSalidaWs.setSolicita("solicita");
//
// // Interesados
// for (RegistreAssentamentInteressat inter: registreSortida.getInteressats()) {
// InteresadoWs interesadoWs = new InteresadoWs();
//
// DatosInteresadoWs interesado = new DatosInteresadoWs();
// interesado.setTipoInteresado((long)RegistreInteressatTipusEnum.valorAsEnum(inter.getTipus()).ordinal());
// interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus());
// interesado.setDocumento(inter.getDocumentNum());
// interesado.setEmail(inter.getEmail());
// interesado.setNombre(inter.getNom());
// interesado.setApellido1(inter.getLlinatge1());
// interesado.setApellido2(inter.getLlinatge2());
// interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) :
// null);
// interesado.setProvincia(inter.getProvincia() != null ? new
// Long(inter.getProvincia()) : null);
// interesado.setDireccion(inter.getAdresa());
// interesado.setCp(inter.getCodiPostal());
// interesado.setLocalidad(inter.getMunicipi() != null ? new
// Long(inter.getMunicipi()) : null);
// interesado.setTelefono(inter.getTelefon());
// interesadoWs.setInteresado(interesado);
//
// if (inter.getRepresentant() != null) {
// RegistreAssentamentInteressat repre = inter.getRepresentant();
// DatosInteresadoWs representante = new DatosInteresadoWs();
// representante.setTipoInteresado((long)RegistreInteressatTipusEnum.valorAsEnum(repre.getTipus()).ordinal());
// representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus());
// representante.setDocumento(repre.getDocumentNum());
// representante.setEmail(repre.getEmail());
// representante.setNombre(repre.getNom());
// representante.setApellido1(repre.getLlinatge1());
// representante.setApellido2(repre.getLlinatge2());
// representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) :
// null);
// representante.setProvincia(repre.getProvincia() != null ? new
// Long(repre.getProvincia()) : null);
// representante.setDireccion(repre.getAdresa());
// representante.setCp(repre.getCodiPostal());
// representante.setLocalidad(repre.getMunicipi() != null ? new
// Long(repre.getMunicipi()) : null);
// representante.setTelefono(repre.getTelefon());
// interesadoWs.setRepresentante(representante);
// }
//
// registroSalidaWs.getInteresados().add(interesadoWs);
// }
//
// for (DocumentRegistre document: registreSortida.getDocuments()) {
// AnexoWs anexoWs = new AnexoWs();
//
// anexoWs.setTitulo(document.getNom());
// anexoWs.setTipoDocumental("TD01");
// anexoWs.setTipoDocumento("02");
// anexoWs.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
// anexoWs.setObservaciones("");
// anexoWs.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA);
//
// anexoWs.setFicheroAnexado(document.getArxiuContingut());
// anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
// anexoWs.setFechaCaptura(new Timestamp(document.getData().getTime()));
//
// InputStream is = new BufferedInputStream(new
// ByteArrayInputStream(document.getArxiuContingut()));
// try {
// String mimeType = URLConnection.guessContentTypeFromStream(is);
// anexoWs.setTipoMIMEFicheroAnexado(mimeType);
// } catch (IOException e) {
// throw new RegistrePluginException("Error IOException: " + e);
// }
//
// registroSalidaWs.getAnexos().add(anexoWs);
// }
//
//
// RespostaAnotacioRegistre resposta = null;
// try {
// registroSalidaApi = getRegistroSalidaApi();
// IdentificadorWs identificadorWs =
// registroSalidaApi.altaRegistroSalida(registroSalidaWs);
// resposta = new RespostaAnotacioRegistre();
// resposta.setData(identificadorWs.getFecha());
// resposta.setNumero(identificadorWs.getNumero().toString());
// resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
// resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
// } catch (WsI18NException e) {
// String msg = WsClientUtils.toString(e);
// throw new RegistrePluginException("Error WsI18NException: " + msg);
// } catch (WsValidationException e) {
// String msg = WsClientUtils.toString(e);
// throw new RegistrePluginException("Error WsValidationException: " + msg);
// } catch (Exception e) {
// throw new RegistrePluginException("Error WsValidationException: " + e);
// }
// return resposta;
// }
//
// @Override
// public RespostaConsultaRegistre obtenirRegistrSortida(String numRegistre,
// String usuariCodi, String entitatCodi)
// throws RegistrePluginException {
//
// RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();
// try {
// RegistroSalidaResponseWs registroSalida =
// registroSalidaApi.obtenerRegistroSalida(numRegistre, usuariCodi,
// entitatCodi);
//
// resposta.setRegistreNumero(registroSalida.getNumeroRegistroFormateado());
// resposta.setRegistreData(registroSalida.getFechaRegistro());
// resposta.setEntitatCodi(registroSalida.getEntidadCodigo());
// resposta.setEntitatDenominacio(registroSalida.getEntidadDenominacion());
// resposta.setOficinaCodi(registroSalida.getOficinaCodigo());
// resposta.setOficinaDenominacio(registroSalida.getOficinaDenominacion());
//
// } catch (WsI18NException e) {
// String msg = WsClientUtils.toString(e);
// throw new RegistrePluginException("Error WsI18NException: " + msg);
// } catch (WsValidationException e) {
// String msg = WsClientUtils.toString(e);
// throw new RegistrePluginException("Error WsValidationException: " + msg);
// }
//
// return resposta;
// }
//
// }
