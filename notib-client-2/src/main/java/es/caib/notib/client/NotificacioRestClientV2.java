/**
 * 
 */
package es.caib.notib.client;

;import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.client.domini.AppInfo;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.IdiomaEnumDto;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client REST v2 per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class NotificacioRestClientV2 extends NotificacioBaseRestClient {

	private static final String NOTIFICACIOV2_SERVICE_PATH = "/interna/notificacio/v2";
	private static final String CONSULTAV2_SERVICE_PATH = "/interna/consulta/v2";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v2 de NOTIB.
	 * <p>El client creat amb aquest constructor utilitzarà autenticació tipus form (per defecte en entorn CAIB),
	 * un timeout de connexió de 20s i un timeout de lectura de 120s</p>
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v2 de NOTIB.
	 * <p>El client creat amb aquest constructor utilitzarà autenticació tipus form (per defecte en entorn CAIB)</p>
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param connecTimeout Timeout de connexio en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password, int connecTimeout, int readTimeout) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
	}

	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v2 de NOTIB.
	 * <p>El client creat amb aquest constructor utilitzarà un timeout de connexió de 20s i un timeout de lectura de 120s</p>
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param autenticacioBasic Indica si utilitzar autenticació tipus basic. Si té el valor false, utilitzarà autenticació tipus Form (per defecte en entorn CAIB)
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password, boolean autenticacioBasic) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
	}

	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v2 de NOTIB.
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param autenticacioBasic Indica si utilitzar autenticació tipus basic. Si té el valor false, utilitzarà autenticació tipus Form (per defecte en entorn CAIB)
	 * @param connecTimeout Timeout de connexio en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password, boolean autenticacioBasic, int connecTimeout, int readTimeout) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
	}

	public AppInfo getAppInfo() {

		String urlAmbMetode = baseUrl + "/api/rest/appinfo";
		try {
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, AppInfo.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// NOTIFICACIÓ

	/**
	 * Mètode per a donar d'alta una Notificació/Comunicació a Notib
	 *
	 * @param notificacio Objecte amb tota la informació necessària per donar d'alta la notificació (veure documentació)
	 * @return L'estat de la notificació/comunicació creada, o informació de l'error en cas que no s'hagi pogut crear
	 */
	public RespostaAltaV2 alta(NotificacioV2 notificacio) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/alta";
			ObjectMapper mapper  = getMapper();
			String body = mapper.writeValueAsString(notificacio);
			jerseyClient = generarClient(urlAmbMetode);
			log.debug("Missatge REST enviat: " + body);
			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(RespostaAltaV2.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
//		} catch (UniformInterfaceException ue) {
//			RespostaAltaV2 respostaAlta = new RespostaAltaV2();
//			ClientResponse response = ue.getResponse();
//
//			if (response != null && response.getStatus() == 401) {
//				respostaAlta.setError(true);
//				respostaAlta.setErrorDescripcio("[CLIENT] Hi ha hagut un problema d'autenticació: "  + ue.getMessage());
//				return respostaAlta;
//			}
//			throw new RuntimeException(ue);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Mètode per a consultar la informació d'una notificació/comunicació
	 *
	 * @param identificador Identificador de la notificació retornat per el màtode d'alta
	 * @return Informació de la nottificació/comunicació
	 */
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacio(String identificador) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/consultaEstatNotificacio/" + identificador;
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaEstatNotificacioV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Mètode per a consultar la informació d'un enviament d'una notificació/comunicació
	 *
	 * @param referencia referència de l'enviament retornat per el màtode d'alta
	 * @return Informació de l'enviament
	 */
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviament(String referencia) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/consultaEstatEnviament/" + referencia;
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaEstatEnviamentV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Mètode per a consultar les dades del registre de sortida de la notificació/comunicació, o enviament. Pot incloure el justificant de registre
	 *
	 * @param dadesConsulta Objecte on es pot indicar l'indentificador de la notificació/comunicació a consultar, i si es vol obtenir el justificant de registre
	 * @return Dades del registre i opcionalment el justificant
	 */
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistre(DadesConsulta dadesConsulta) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/consultaDadesRegistre";
			ObjectMapper mapper  = getMapper();
			String body = mapper.writeValueAsString(dadesConsulta);
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(RespostaConsultaDadesRegistreV2.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Mètode per a obtenir el justificant d'enviament de la notificació/comunicació
	 *
	 * @param identificador Identificador de la notificació retornat per el màtode d'alta
	 * @return Justificant d'enviament
	 */
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador) {
		return consultaJustificantEnviament(identificador, NOTIFICACIOV2_SERVICE_PATH);
	}

	/**
	 * Métode per a donar o treure el permís de consulta sobre un procediment a un usuari
	 *
	 * @param permisConsulta Objecte amb les dades necessàries per a donar el permís
	 * @return True si se li ha donat el permís
	 */
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		return donarPermisConsulta(permisConsulta, NOTIFICACIOV2_SERVICE_PATH);
	}


	// CONSULTA

	public RespostaConsultaV2 comunicacionsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular;
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular;
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 comunicacionsPendentsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular + "/pendents";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsPendentsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular + "/pendents";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 comunicacionsLlegidesByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular + "/llegides";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsLlegidesByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular + "/llegides";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String getConsultaJsonString(Date dataInicial, Date dataFinal, Boolean visibleCarpeta, IdiomaEnumDto lang, Integer pagina, Integer mida, String urlAmbMetode) throws Exception {

		jerseyClient = generarClient(urlAmbMetode);
		var wt = jerseyClient.target(urlAmbMetode);
		wt.queryParam("dataInicial", dataInicial != null ? sdf.format(dataInicial) : "").
		queryParam("dataFinal", dataInicial != null ? sdf.format(dataFinal) : "").
		queryParam("visibleCarpeta", visibleCarpeta != null ? (visibleCarpeta ? "si" : "no") : "").
		queryParam("lang", lang != null ? lang.name() : "").
		queryParam("pagina", pagina != null ? pagina.toString() : "").
		queryParam("mida", mida != null ? mida.toString() : "");
		var response = wt.request(MediaType.APPLICATION_JSON).get(String.class);
		return response;
	}

}
