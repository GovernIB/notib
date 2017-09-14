package es.caib.notib.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.notib.core.api.ws.notificacio2.DomiciliConcretTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliNumeracioTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.Notificacio;
import es.caib.notib.core.api.ws.notificacio2.NotificacioEnviament;
import es.caib.notib.core.api.ws.notificacio2.ServeiTipusEnum;

public class RestAltaNotificacio {

	private static final int NUM_DESTINATARIS = 2;
	private static final String ENTITAT_DIR3CODI = "A04013511";

	public static void main(String[] args) throws JsonProcessingException {
		try {
			new RestAltaNotificacio().testAlta();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testAlta() throws JsonProcessingException, IOException, DecoderException {
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		String user = "notibapp";
		String pass = "notibapp";
		String urlAmbMetode = "http://localhost:8080/notib/api/services/alta";
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		String body = mapper.writeValueAsString(
				generarNotificacio(
						notificacioId,
						NUM_DESTINATARIS,
						false));
		jerseyClient.addFilter(
				new HTTPBasicAuthFilter(user, pass));
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		System.out.println(
				">>> Resposta HTTP estat: " + response.getStatus());
		System.out.println(
				">>> Resposta HTTP JSON: " + response.getEntity(String.class));
	}

	private Notificacio generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		Notificacio notificacio = new Notificacio();
		notificacio.setEntitatDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		if (ambEnviamentPostal) {
			notificacio.setPagadorCorreusCodiDir3("A04013511");
			notificacio.setPagadorCorreusContracteNum("00001");
			notificacio.setPagadorCorreusCodiClientFacturacio("A04013511");
			notificacio.setPagadorCorreusDataVigencia(new Date());
			notificacio.setPagadorCorreusContracteNum(
					"pccNum_" + notificacioId);
			notificacio.setPagadorCorreusCodiClientFacturacio(
					"ccFac_" + notificacioId);
			notificacio.setPagadorCorreusDataVigencia(
					new Date(0));
			notificacio.setPagadorCieCodiDir3(
					"A04013511");
			notificacio.setPagadorCieDataVigencia(
					new Date(0));
		}
		notificacio.setProcedimentCodiSia(
				"0000");
		notificacio.setProcedimentDescripcioSia(
				"Procediment desc.");
		notificacio.setDocumentArxiuNom(
				"documentArxiuNom_" + notificacioId + ".pdf");
		notificacio.setDocumentContingutBase64(
				Base64.encodeBase64String(arxiuBytes));
		notificacio.setDocumentSha1(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		notificacio.setDocumentNormalitzat(
				false);
		notificacio.setDocumentGenerarCsv(
				false);
		notificacio.setSeuExpedientSerieDocumental(
				"0000S");
		notificacio.setSeuExpedientUnitatOrganitzativa(
				"00000000T");
		notificacio.setSeuExpedientIdentificadorEni(
				"seuExpedientIdentificadorEni_" + notificacioId);
		notificacio.setSeuExpedientTitol(
				"seuExpedientTitol_" + notificacioId);
		notificacio.setSeuRegistreOficina(
				"seuRegistreOficina_" + notificacioId);
		notificacio.setSeuRegistreLlibre(
				"seuRegistreLlibre_" + notificacioId);
		notificacio.setSeuIdioma(
				"seuIdioma_" + notificacioId);
		notificacio.setSeuAvisTitol(
				"seuAvisTitol_" + notificacioId);
		notificacio.setSeuAvisText(
				"seuAvisText_" + notificacioId);
		notificacio.setSeuAvisTextMobil(
				"seuAvisTextMobil_" + notificacioId);
		notificacio.setSeuOficiTitol(
				"seuOficiTitol_" + notificacioId);
		notificacio.setSeuOficiText(
				"seuOficiText_" + notificacioId);
		List<NotificacioEnviament> enviaments = new ArrayList<>();
		for (int i = 0; i < numDestinataris; i++) {
			NotificacioEnviament enviament = new NotificacioEnviament();
			enviament.setTitularNom("titularNom" + i);
			enviament.setTitularLlinatges("titularLlinatges" + i);
			enviament.setTitularNif("00000000T");
			enviament.setTitularTelefon("666010101");
			enviament.setTitularEmail("titular@gmail.com");
			enviament.setDestinatariNom("destinatariNom" + i);
			enviament.setDestinatariLlinatges("destinatariLlinatges" + i);
			enviament.setDestinatariNif("12345678Z");
			enviament.setDestinatariTelefon("666020202");
			enviament.setDestinatariEmail("destinatari@gmail.com");
			if (ambEnviamentPostal) {
				enviament.setDomiciliTipus(DomiciliTipusEnum.CONCRET);
				enviament.setDomiciliConcretTipus(DomiciliConcretTipusEnum.NACIONAL);
				enviament.setDomiciliViaTipus("CALLE");
				enviament.setDomiciliViaNom("Bas");
				enviament.setDomiciliNumeracioTipus(DomiciliNumeracioTipusEnum.SENSE_NUMERO);
				enviament.setDomiciliNumeracioNumero("00");
				enviament.setDomiciliNumeracioPuntKm("pk01");
				enviament.setDomiciliApartatCorreus("0228");
				enviament.setDomiciliBloc("bloc" + i);
				enviament.setDomiciliPortal("portal" + i);
				enviament.setDomiciliEscala("escala" + i);
				enviament.setDomiciliPlanta("planta" + i);
				enviament.setDomiciliPorta("porta" + i);
				enviament.setDomiciliComplement("complement" + i);
				enviament.setDomiciliPoblacio("poblacio" + i);
				enviament.setDomiciliMunicipiCodiIne("07033");
				enviament.setDomiciliMunicipiNom("Manacor");
				enviament.setDomiciliCodiPostal("07500");
				enviament.setDomiciliProvinciaCodi("07");
				enviament.setDomiciliProvinciaNom("Illes Balears");
				enviament.setDomiciliPaisCodiIso("ES");
				enviament.setDomiciliPaisNom("Espanya");
				enviament.setDomiciliLinea1("linea1" + i);
				enviament.setDomiciliLinea2("linea2" + i);
				enviament.setDomiciliCie(new Integer(8));
			}
			enviament.setDehObligat(true);
			enviament.setDehNif("00000000T");
			enviament.setDehProcedimentCodi("0000");
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			enviament.setRetardPostal(5);
			enviament.setCaducitat(
					new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		return notificacio;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}

}
