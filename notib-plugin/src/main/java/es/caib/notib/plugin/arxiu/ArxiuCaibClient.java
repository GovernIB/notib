package es.caib.notib.plugin.arxiu;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.PersonIdentAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ProceedingsAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.PublicServantAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceHeader;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceSecurityInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExceptionResult;
import es.caib.arxiudigital.apirest.CSGD.peticiones.Request;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.plugins.arxiu.caib.ArxiuCaibException;

public class ArxiuCaibClient {


	private static final String SERVEI_VERSIO = "1.0";

	private String url;
	private String aplicacioCodi;
	private String usuariSgd;
	private String contrasenyaSgd;

	private Client jerseyClient;
	private ObjectMapper mapper;



	public ArxiuCaibClient(
			String url,
			String aplicacioCodi,
			String usuariHttp,
			String contrasenyaHttp,
			String usuariSgd,
			String contrasenyaSgd,
			int timeoutConnect,
			int timeoutRead) {
		super();
		if (url.endsWith("/")) {
			this.url = url.substring(0, url.length() - 1);
		} else {
			this.url = url;
		}
		this.aplicacioCodi = aplicacioCodi;
		this.usuariSgd = usuariSgd;
		this.contrasenyaSgd = contrasenyaSgd;
		jerseyClient = new Client();
		jerseyClient.setConnectTimeout(timeoutConnect);
		jerseyClient.setReadTimeout(timeoutRead);
		if (usuariHttp != null) {
			jerseyClient.addFilter(new HTTPBasicAuthFilter(usuariHttp, contrasenyaHttp));
		}
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	public ArxiuCaibClient(
			String url,
			String aplicacioCodi,
			String usuariSgd,
			String contrasenyaSgd,
			int timeoutConnect,
			int timeoutRead) {
		this(
				url,
				aplicacioCodi,
				null,
				null,
				usuariSgd,
				contrasenyaSgd,
				timeoutConnect,
				timeoutRead);
	}



	public <T, U, V> V generarEnviarPeticio(
			String metode,
			Class<T> peticioType,
			GeneradorParam<U> generador,
			Class<U> paramType,
			Class<V> respostaType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		Request<U> request = new Request<U>();
		Capsalera capsalera = generarCapsalera();
		request.setServiceHeader(
				generarServiceHeader(capsalera));
		if (generador != null) {
			request.setParam(generador.generar());
		}
		T peticio = peticioType.newInstance();
		for (Method method: peticioType.getMethods()) {
			if (method.getName().startsWith("set")) {
				method.invoke(peticio, request);
				break;
			}
		}
		JerseyResponse response = enviarPeticioArxiu(
				metode,
				peticio);
		if (response.getStatus() == 200) {
			return mapper.readValue(
					response.getJson(),
					respostaType);
		} else {
			throw generarExcepcioJson(
					metode,
					response);
		}
	}

	public static Capsalera generarCapsalera() {
		Capsalera capsalera = new Capsalera();
		//		header.setInteressatNom(capsalera.getInteressatNom());
		//		header.setInteressatNif(capsalera.getInteressatNif());
		//		header.setFuncionariNom(capsalera.getFuncionariNom());
		//		header.setFuncionariNif(capsalera.getFuncionariNif());
		//		header.setFuncionariOrgan(capsalera.getFuncionariOrgan());
		//		header.setProcedimentId(capsalera.getProcedimentId());
		//		header.setProcedimentNom(capsalera.getProcedimentNom());
		//		header.setExpedientId(capsalera.getExpedientId());
		return capsalera;
	}



	private ServiceHeader generarServiceHeader(Capsalera capsalera) {
		ServiceHeader serviceHeader = new ServiceHeader();
		ServiceAuditInfo auditInfo = null;
		if (capsalera.getInteressatNom() != null || capsalera.getInteressatNif() != null) {
			PersonIdentAuditInfo interessat = new PersonIdentAuditInfo();
			interessat.setName(capsalera.getInteressatNom());
			interessat.setDocument(capsalera.getInteressatNif());
			if (auditInfo == null) {
				auditInfo = new ServiceAuditInfo();
			}
			auditInfo.setApplicant(interessat);
		}
		PublicServantAuditInfo publicServant = null;
		if (capsalera.getFuncionariNom() != null || capsalera.getFuncionariNif() != null) {
			PersonIdentAuditInfo funcionari = new PersonIdentAuditInfo();
			funcionari.setName(capsalera.getFuncionariNom());
			funcionari.setDocument(capsalera.getFuncionariNif());
			if (publicServant == null) {
				publicServant = new PublicServantAuditInfo();
			}
			publicServant.setIdentificationData(funcionari);
		}
		if (capsalera.getFuncionariOrgan() != null) {
			if (publicServant == null) {
				publicServant = new PublicServantAuditInfo();
			}
			publicServant.setOrganization(capsalera.getFuncionariOrgan());
		}
		if (publicServant != null) {
			if (auditInfo == null) {
				auditInfo = new ServiceAuditInfo();
			}
			auditInfo.setPublicServant(publicServant);
		}
		FileAuditInfo expedient = null;
		if (capsalera.getExpedientId() != null) {
			if (expedient == null) {
				expedient = new FileAuditInfo();
			}
			expedient.setId(capsalera.getExpedientId());
		}
		if (capsalera.getProcedimentId() != null || capsalera.getProcedimentNom() != null) {
			ProceedingsAuditInfo procediment = new ProceedingsAuditInfo();
			procediment.setId(capsalera.getProcedimentId());
			procediment.setName(capsalera.getProcedimentNom());
			if (expedient == null) {
				expedient = new FileAuditInfo();
			}
			expedient.setProceedings(procediment);
		}
		if (expedient != null) {
			if (auditInfo == null) {
				auditInfo = new ServiceAuditInfo();
			}
			auditInfo.setFile(expedient);
		}
		if (aplicacioCodi != null) {
			if (auditInfo == null) {
				auditInfo = new ServiceAuditInfo();
			}
			auditInfo.setApplication(aplicacioCodi);
		}
		serviceHeader.setAuditInfo(auditInfo);
		serviceHeader.setServiceVersion(SERVEI_VERSIO);
		if (usuariSgd != null || contrasenyaSgd != null) {
			ServiceSecurityInfo securityInfo = new ServiceSecurityInfo();
			securityInfo.setUser(usuariSgd);
			securityInfo.setPassword(contrasenyaSgd);
			serviceHeader.setSecurityInfo(securityInfo);
		}
		return serviceHeader;
	}

	private ArxiuException generarExcepcioJson(
			String metode,
			JerseyResponse resposta)
		      throws JsonParseException, JsonMappingException, IOException {
		ExceptionResult exceptionResult = mapper.readValue(
				resposta.getJson(),
				ExceptionResult.class);
		String code = exceptionResult.getException().getCode();
		String description = exceptionResult.getException().getDescription();
		if ("COD_021".equals(code) && description.contains("not found")) {
			return new ArxiuNotFoundException();
		} else {
			return new ArxiuCaibException(
					metode,
					resposta.getStatus(),
					code,
					description);
		}
	}

	private JerseyResponse enviarPeticioArxiu(
			String metode,
			Object peticio) throws UniformInterfaceException, ClientHandlerException, JsonProcessingException {
		String urlAmbMetode = url + metode;
		String body = mapper.writeValueAsString(peticio);
		logger.info("Enviant petició HTTP a l'arxiu (" +
				"url=" + urlAmbMetode + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		String json = response.getEntity(String.class);
		logger.info("Rebuda resposta HTTP de l'arxiu (" +
				"status=" + response.getStatus() + ", " +
				"body=" + json + ")");
		return new JerseyResponse(
				json,
				response.getStatus());
	}

	private static class JerseyResponse {
		String json;
		int status;
		public JerseyResponse(String json, int status) {
			this.json = json;
			this.status = status;
		}
		public String getJson() {
			return json;
		}
		public int getStatus() {
			return status;
		}
	}

	@SuppressWarnings("unused")
	private static class Capsalera {
		private String interessatNom;
		private String interessatNif;
		private String funcionariNom;
		private String funcionariNif;
		private String funcionariOrgan;
		private String procedimentId;
		private String procedimentNom;
		private String expedientId;
		public String getInteressatNom() {
			return interessatNom;
		}
		public void setInteressatNom(String interessatNom) {
			this.interessatNom = interessatNom;
		}
		public String getInteressatNif() {
			return interessatNif;
		}
		public void setInteressatNif(String interessatNif) {
			this.interessatNif = interessatNif;
		}
		public String getFuncionariNom() {
			return funcionariNom;
		}
		public void setFuncionariNom(String funcionariNom) {
			this.funcionariNom = funcionariNom;
		}
		public String getFuncionariNif() {
			return funcionariNif;
		}
		public void setFuncionariNif(String funcionariNif) {
			this.funcionariNif = funcionariNif;
		}
		public String getFuncionariOrgan() {
			return funcionariOrgan;
		}
		public void setFuncionariOrgan(String funcionariOrgan) {
			this.funcionariOrgan = funcionariOrgan;
		}
		public String getProcedimentId() {
			return procedimentId;
		}
		public void setProcedimentId(String procedimentId) {
			this.procedimentId = procedimentId;
		}
		public String getProcedimentNom() {
			return procedimentNom;
		}
		public void setProcedimentNom(String procedimentNom) {
			this.procedimentNom = procedimentNom;
		}
		public String getExpedientId() {
			return expedientId;
		}
		public void setExpedientId(String expedientId) {
			this.expedientId = expedientId;
		}
	}

	public interface GeneradorParam<T> {
		public T generar();
	}

	private static final Logger logger = LoggerFactory.getLogger(ArxiuCaibClient.class);
}
