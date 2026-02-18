package es.caib.notib.client;

import es.caib.notib.client.domini.AppInfo;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.client.domini.consulta.Arxiu;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client REST v2 per al servei de notificacions de NOTIB (JAX-RS).
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
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password, boolean debug) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.debug = debug;
	}

	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v2 de NOTIB.
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param connecTimeout Timeout de connexio en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @version 2.0
	 */
	public NotificacioRestClientV2(String baseUrl, String username, String password, int connecTimeout, int readTimeout, boolean debug) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
		this.debug = debug;
	}

	public AppInfo getAppInfo() {
		String urlAmbMetode = baseUrl + "/interna/api/rest/appinfo";
		try {
			return clientGet(urlAmbMetode, AppInfo.class);
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
			return clientPost(urlAmbMetode, notificacio, RespostaAltaV2.class);
		} catch (Exception ex) {
			log.error("Error en alta de notificació V2", ex);
			RespostaAltaV2 respostaAlta = new RespostaAltaV2();
			respostaAlta.setError(true);
			respostaAlta.setErrorDescripcio("[CLIENT] Error en alta V2: "  + ex.getMessage());
			return respostaAlta;
		}
	}

	public RespuestaAmpliarPlazoOE ampliarPlazoOE(AmpliarPlazoOE ampliarPlazo) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/ampliarPlazo";
			return clientPost(urlAmbMetode, ampliarPlazo, RespuestaAmpliarPlazoOE.class);
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
			return clientGet(urlAmbMetode, RespostaConsultaEstatNotificacioV2.class);
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
			return clientGet(urlAmbMetode, RespostaConsultaEstatEnviamentV2.class);
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
			return clientPost(urlAmbMetode, dadesConsulta, RespostaConsultaDadesRegistreV2.class);
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

	public RespostaConsultaV2 comunicacionsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular;
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular;
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 comunicacionsPendentsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular + "/pendents";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsPendentsByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular + "/pendents";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 comunicacionsLlegidesByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/comunicacions/" + dniTitular + "/llegides";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaV2 notificacionsLlegidesByTitular(String dniTitular, Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/notificacions/" + dniTitular + "/llegides";
			String json = getConsultaJsonString(dataInicial, dataFinal, visibleCarpeta, lang, pagina, mida, urlAmbMetode);
			return getMapper().readValue(json, RespostaConsultaV2.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Arxiu getDocument(String notificacioId) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/document/" + (notificacioId != null ? notificacioId : "");
			return clientGet(urlAmbMetode, Arxiu.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Arxiu getCertificacio(String enviamentId) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/certificacio/" + (enviamentId != null ? enviamentId : "");
			return clientGet(urlAmbMetode, Arxiu.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Arxiu getJustificant(String enviamentId) {
		try {
			String urlAmbMetode = baseUrl + CONSULTAV2_SERVICE_PATH + "/justificant/" + (enviamentId != null ? enviamentId : "");
			return clientGet(urlAmbMetode, Arxiu.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String getConsultaJsonString(Date dataInicial, Date dataFinal, Boolean visibleCarpeta, Idioma lang, Integer pagina, Integer mida, String urlAmbMetode) throws Exception {
		WebTarget target = generarClient().target(urlAmbMetode)
				.queryParam("dataInicial", dataInicial != null ? sdf.format(dataInicial) : "")
				.queryParam("dataFinal", dataFinal != null ? sdf.format(dataFinal) : "") // Arreglat error en original: dataInicial -> dataFinal
				.queryParam("visibleCarpeta", visibleCarpeta != null ? (visibleCarpeta ? "si" : "no") : "")
				.queryParam("lang", lang != null ? lang.name() : "")
				.queryParam("pagina", pagina != null ? pagina.toString() : "")
				.queryParam("mida", mida != null ? mida.toString() : "");

		Response response = target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getBasicAuthHeader())
				.get();

		if (response.getStatus() >= 400) {
			throw new RuntimeException("Error en crida REST GET " + urlAmbMetode + ". Status: " + response.getStatus());
		}

		return response.readEntity(String.class);
	}
}
