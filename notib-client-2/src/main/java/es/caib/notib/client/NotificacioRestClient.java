/**
 * 
 */
package es.caib.notib.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistre;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;
;import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Client REST v1 per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Deprecated
public class NotificacioRestClient extends NotificacioBaseRestClient {

	private static final String NOTIFICACIOV1_SERVICE_PATH = "/interna/notificacio/v1";

	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v1 de NOTIB.
	 * <p>El client creat amb aquest constructor utilitzarà autenticació tipus form (per defecte en entorn CAIB),
	 * un timeout de connexió de 20s i un timeout de lectura de 120s</p>
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @version 1.0
	 */
	public NotificacioRestClient(String baseUrl, String username, String password) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}


	/**
	 * Constructor per a crear un client per a connectar-se amb la API REST v1 de NOTIB.
	 *
	 * @param baseUrl URL de NOTIB al que es vol connectar. Ex. https://notib_server:8080/notib
	 * @param username Nom de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param password Contrassenya de l'usuari de tipus aplicació a utilitzar per a connectar-se a Notib
	 * @param connecTimeout Timeout de connexio en milisegons
	 * @param readTimeout Timeout de lectura en milisegons
	 * @version 1.0
	 */
	public NotificacioRestClient(String baseUrl, String username, String password, int connecTimeout, int readTimeout) {

		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
	}


	/**
	 * Mètode per a donar d'alta una Notificació/Comunicació a Notib
	 *
	 * @param notificacio Objecte amb tota la informació necessària per donar d'alta la notificació (veure documentació)
	 * @return L'estat de la notificació/comunicació creada, o informació de l'error en cas que no s'hagi pogut crear
	 */
	public RespostaAlta alta(NotificacioV2 notificacio) {

		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV1_SERVICE_PATH + "/alta";
			ObjectMapper mapper  = getMapper();
			String body = mapper.writeValueAsString(notificacio);
			jerseyClient = generarClient(urlAmbMetode);
			log.debug("Missatge REST enviat: " + body);

			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(RespostaAlta.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
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
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV1_SERVICE_PATH + "/consultaEstatNotificacio/" + identificador;
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaEstatNotificacio.class);
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
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV1_SERVICE_PATH + "/consultaEstatEnviament/" + referencia;
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaEstatEnviament.class);
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
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV1_SERVICE_PATH + "/consultaDadesRegistre";
			ObjectMapper mapper  = getMapper();
			String body = mapper.writeValueAsString(dadesConsulta);
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(RespostaConsultaDadesRegistre.class);
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
		return consultaJustificantEnviament(identificador, NOTIFICACIOV1_SERVICE_PATH);
	}

	/**
	 * Métode per a donar o treure el permís de consulta sobre un procediment a un usuari
	 *
	 * @param permisConsulta Objecte amb les dades necessàries per a donar el permís
	 * @return True si se li ha donat el permís
	 */
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		return donarPermisConsulta(permisConsulta, NOTIFICACIOV1_SERVICE_PATH);
	}


}
