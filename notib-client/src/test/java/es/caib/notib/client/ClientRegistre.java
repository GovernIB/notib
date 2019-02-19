package es.caib.notib.client;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

import es.caib.notib.ws.notificacio.Document;
import es.caib.notib.ws.notificacio.DocumentV2;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.Enviament;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaServeiTipusEnumDto;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.PagadorCie;
import es.caib.notib.ws.notificacio.PagadorPostal;
import es.caib.notib.ws.notificacio.ParametresSeu;
import es.caib.notib.ws.notificacio.Persona;



public class ClientRegistre {

		private static final int NUM_DESTINATARIS = 2;
//		private static final String ENTITAT_DIR3CODI = "A04013511";
		private static final String ENTITAT_DIR3CODI = "L01070276";
		public static void main(String[] args) throws JsonProcessingException {
			try {
				new ClientRegistre().testAltaV2();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void testAlta() throws JsonProcessingException, IOException, DecoderException {
			Client jerseyClient = new Client();
			ObjectMapper mapper  = new ObjectMapper();
			String user = "admin";
			String pass = "admin";
			String urlAmbMetode = "http://localhost:8081/notib/api/services/notificacio/alta";
			String notificacioId = new Long(System.currentTimeMillis()).toString();
			String body = mapper.writeValueAsString(
					generarNotificacio(
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

		private void testAltaV2() throws JsonProcessingException, IOException, DecoderException, DatatypeConfigurationException {
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
		
		
		private Notificacio generarNotificacio(
				String notificacioId,
				int numDestinataris,
				boolean ambEnviamentPostal) throws IOException, DecoderException {
			byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			Notificacio notificacio = new Notificacio();
			notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
			notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
			notificacio.setConcepte(
					"concepte_" + notificacioId);
			notificacio.setDescripcio(
					"descripcio_" + notificacioId);
			notificacio.setEnviamentDataProgramada(null);
			notificacio.setRetard(5);
			notificacio.setCaducitat(
					new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
			Document document = new Document();
			document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
			document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
			document.setHash(
					Base64.encodeBase64String(
							Hex.decodeHex(
									DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
			document.setNormalitzat(false);
			document.setGenerarCsv(false);
			notificacio.setDocument(document);
			notificacio.setProcedimentCodi("0000");
			List<Enviament> enviaments = new ArrayList<Enviament>();
			if (ambEnviamentPostal) {
				PagadorPostal pagadorPostal = new PagadorPostal();
				pagadorPostal.setDir3Codi("A04013511");
				pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
				pagadorPostal.setContracteNum("pccNum_" + notificacioId);
				pagadorPostal.setContracteDataVigencia(new Date(0));
				notificacio.setPagadorPostal(pagadorPostal);
				PagadorCie pagadorCie = new PagadorCie();
				pagadorCie.setDir3Codi("A04013511");
				pagadorCie.setContracteDataVigencia(new Date(0));
				notificacio.setPagadorCie(pagadorCie);
			}
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
					entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
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
					entregaPostal.setProvinciaCodi("07");
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
			ParametresSeu parametresSeu = new ParametresSeu();
			parametresSeu.setExpedientSerieDocumental(
					"0000S");
			parametresSeu.setExpedientUnitatOrganitzativa(
					"00000000T");
			parametresSeu.setExpedientIdentificadorEni(
					"seuExpedientIdentificadorEni_" + notificacioId);
			parametresSeu.setExpedientTitol(
					"seuExpedientTitol_" + notificacioId);
			parametresSeu.setRegistreOficina(
					"seuRegistreOficina_" + notificacioId);
			parametresSeu.setRegistreLlibre(
					"seuRegistreLlibre_" + notificacioId);
			parametresSeu.setIdioma(
					"seuIdioma_" + notificacioId);
			parametresSeu.setAvisTitol(
					"seuAvisTitol_" + notificacioId);
			parametresSeu.setAvisText(
					"seuAvisText_" + notificacioId);
			parametresSeu.setAvisTextMobil(
					"seuAvisTextMobil_" + notificacioId);
			parametresSeu.setOficiTitol(
					"seuOficiTitol_" + notificacioId);
			parametresSeu.setOficiText(
					"seuOficiText_" + notificacioId);
			notificacio.setParametresSeu(parametresSeu);
			return notificacio;
		}
		
		private NotificacioV2 generarNotificacioV2(
				String notificacioId,
				int numDestinataris,
				boolean ambEnviamentPostal) throws IOException, DecoderException, DatatypeConfigurationException {
//			byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			NotificacioV2 notificacio = new NotificacioV2();
			notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
			notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
			notificacio.setConcepte(
					"concepte_" + notificacioId);
			notificacio.setDescripcio(
					"descripcio_" + notificacioId);
			notificacio.setEnviamentDataProgramada(null);
			notificacio.setRetard(5);
			notificacio.setCaducitat(toXmlGregorianCalendar(
					new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000)));
			DocumentV2 document = new DocumentV2();
//			document.setCsv("4edd743942cec762cdf9389dc626ee01209fd30e6884aca9845c9573a57f2df3");
			document.setUuid("614841f4-a93f-4307-8009-32970cf01632");
			document.setArxiuNom("prova");
//			document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
//			document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
//			document.setHash(
//					Base64.encodeBase64String(
//							Hex.decodeHex(
//									DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
			document.setNormalitzat(false);
			document.setGenerarCsv(false);
			notificacio.setDocument(document);
			notificacio.setCodiProcediment("APB");
			List<Enviament> enviaments = new ArrayList<Enviament>();
			if (ambEnviamentPostal) {
				PagadorPostal pagadorPostal = new PagadorPostal();
				pagadorPostal.setDir3Codi("A04013511");
				pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
				pagadorPostal.setContracteNum("pccNum_" + notificacioId);
				pagadorPostal.setContracteDataVigencia(new Date(0));
//				notificacio.setPagadorPostal(pagadorPostal);
				PagadorCie pagadorCie = new PagadorCie();
				pagadorCie.setDir3Codi("A04013511");
				pagadorCie.setContracteDataVigencia(new Date(0));
//				notificacio.setPagadorCie(pagadorCie);
			}
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
					entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
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
					entregaPostal.setProvinciaCodi("07");
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
//			ParametresSeu parametresSeu = new ParametresSeu();
//			parametresSeu.setExpedientSerieDocumental(
//					"0000S");
//			parametresSeu.setExpedientUnitatOrganitzativa(
//					"00000000T");
//			parametresSeu.setExpedientIdentificadorEni(
//					"seuExpedientIdentificadorEni_" + notificacioId);
//			parametresSeu.setExpedientTitol(
//					"seuExpedientTitol_" + notificacioId);
//			parametresSeu.setRegistreOficina(
//					"seuRegistreOficina_" + notificacioId);
//			parametresSeu.setRegistreLlibre(
//					"seuRegistreLlibre_" + notificacioId);
//			parametresSeu.setIdioma(
//					"seuIdioma_" + notificacioId);
//			parametresSeu.setAvisTitol(
//					"seuAvisTitol_" + notificacioId);
//			parametresSeu.setAvisText(
//					"seuAvisText_" + notificacioId);
//			parametresSeu.setAvisTextMobil(
//					"seuAvisTextMobil_" + notificacioId);
//			parametresSeu.setOficiTitol(
//					"seuOficiTitol_" + notificacioId);
//			parametresSeu.setOficiText(
//					"seuOficiText_" + notificacioId);
//			notificacio.setParametresSeu(parametresSeu);
			return notificacio;
		}

		private InputStream getContingutNotificacioAdjunt() {
			return getClass().getResourceAsStream(
					"/es/caib/notib/core/notificacio_adjunt.pdf");
		}

		
		private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		}

	}


