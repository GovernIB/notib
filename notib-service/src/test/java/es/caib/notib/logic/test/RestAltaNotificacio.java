package es.caib.notib.logic.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.notib.client.domini.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class RestAltaNotificacio {

	private static final int NUM_DESTINATARIS = 2;
//	private static final String ENTITAT_DIR3CODI = "A04013511";
	private static final String ENTITAT_DIR3CODI = "L01070276";
	public static void main(String[] args) throws JsonProcessingException {
		try {
			new RestAltaNotificacio().testAltaV2();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
//	@Test
	private void testAltaV2() throws JsonProcessingException, IOException, DecoderException {
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		String user = "admin";
		String pass = "admin";
		String urlAmbMetode = "http://localhost:8081/notib/api/services/notificacioV2/alta";
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		String body = mapper.writeValueAsString(
				generarNotificacioV2(
						notificacioId,
						NUM_DESTINATARIS,
						true));
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
	
	private NotificacioV2 generarNotificacioV2(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioV2 notificacio = NotificacioV2.builder()
			.emisorDir3Codi(ENTITAT_DIR3CODI)
			.enviamentTipus(EnviamentTipus.COMUNICACIO)
				.concepte("concepte_" + notificacioId)
				.descripcio("descripcio_" + notificacioId)
				.enviamentDataProgramada(null)
				.retard(5)
				.caducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000))
			.build();
		DocumentV2 document = new DocumentV2();
//		document.setCsv("4edd743942cec762cdf9389dc626ee01209fd30e6884aca9845c9573a57f2df3");
//		document.setUuid("614841f4-a93f-4307-8009-32970cf01632");
//		document.setUrl("http://www.cursodejava.com.mx/descargas/CursoJava.pdf");
//		document.setArxiuNom("prova");
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.getEncoder().encodeToString(arxiuBytes));
//		document.setHash(
//				Base64.getEncoder().encodeToString(
//						Hex.decodeHex(
//								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
//		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi("AJINCA");
		List<Enviament> enviaments = new ArrayList<Enviament>();
//		if (ambEnviamentPostal) {
//			PagadorPostal pagadorPostal = new PagadorPostal();
//			pagadorPostal.setDir3Codi("A04013511");
//			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
//			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
//			pagadorPostal.setContracteDataVigencia(new Date(0));
////			notificacio.setPagadorPostal(pagadorPostal);
//			PagadorCie pagadorCie = new PagadorCie();
//			pagadorCie.setDir3Codi("A04013511");
//			pagadorCie.setContracteDataVigencia(new Date(0));
////			notificacio.setPagadorCie(pagadorCie);
//		}
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("titular@gmail.com");
			enviament.setTitular(titular);
			List<Persona> destinataris = new ArrayList<Persona>();
			Persona destinatari = new Persona();
			destinatari.setNom("destinatariNom" + i);
			destinatari.setLlinatge1("destLlinatge1_" + i);
			destinatari.setLlinatge2("destLlinatge2_" + i);
			destinatari.setNif("12345678Z");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("destinatari@gmail.com");
			destinataris.add(destinatari);
			enviament.setDestinataris(destinataris);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(NotificaDomiciliConcretTipus.NACIONAL);
				entregaPostal.setViaTipus(EntregaPostalVia.CALLE);
				entregaPostal.setViaNom("Bas");
				entregaPostal.setNumeroCasa("25");
				entregaPostal.setNumeroQualificador("bis");
				entregaPostal.setPuntKm("pk01");
				entregaPostal.setApartatCorreus("0228");
				entregaPostal.setPortal("portal" + i);
				entregaPostal.setEscala("escala" + i);
				entregaPostal.setPlanta("planta" + i);
				entregaPostal.setPorta("porta" + i);
				entregaPostal.setBloc("bloc" + i);
				entregaPostal.setComplement("complement" + i);
				entregaPostal.setCodiPostal("07500");
				entregaPostal.setPoblacio("poblacio" + i);
				entregaPostal.setMunicipiCodi("07033");
				entregaPostal.setProvincia("07");
				entregaPostal.setPaisCodi("ES");
				entregaPostal.setLinea1("linea1_" + i);
				entregaPostal.setLinea2("linea2_" + i);
				entregaPostal.setCie(new Integer(8));
				enviament.setEntregaPostal(entregaPostal);
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi("0000");
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
//		ParametresSeu parametresSeu = new ParametresSeu();
//		parametresSeu.setExpedientSerieDocumental(
//				"0000S");
//		parametresSeu.setExpedientUnitatOrganitzativa(
//				"00000000T");
//		parametresSeu.setExpedientIdentificadorEni(
//				"seuExpedientIdentificadorEni_" + notificacioId);
//		parametresSeu.setExpedientTitol(
//				"seuExpedientTitol_" + notificacioId);
//		parametresSeu.setRegistreOficina(
//				"seuRegistreOficina_" + notificacioId);
//		parametresSeu.setRegistreLlibre(
//				"seuRegistreLlibre_" + notificacioId);
//		parametresSeu.setIdioma(
//				"seuIdioma_" + notificacioId);
//		parametresSeu.setAvisTitol(
//				"seuAvisTitol_" + notificacioId);
//		parametresSeu.setAvisText(
//				"seuAvisText_" + notificacioId);
//		parametresSeu.setAvisTextMobil(
//				"seuAvisTextMobil_" + notificacioId);
//		parametresSeu.setOficiTitol(
//				"seuOficiTitol_" + notificacioId);
//		parametresSeu.setOficiText(
//				"seuOficiText_" + notificacioId);
//		notificacio.setParametresSeu(parametresSeu);
		return notificacio;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
                "/es/caib/notib/logic/notificacio_adjunt.pdf");
	}

}
