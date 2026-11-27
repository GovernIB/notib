/**
 *
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.client.domini.*;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.intf.dto.anular.Anulacio;
import es.caib.notib.logic.intf.dto.anular.RespostaAnular;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsException;

/**
 * Servei per a l'enviament i consulta de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioServiceWs {

	/**
	 * Dona d'alta una notificació.
	 *
	 * @param notificacio Dades per a donar d'alta la notificació.
	 * @return la llista de referencies generades per NOTIB (una per enviament)).
	 */
	RespostaAlta alta(Notificacio notificacio) throws NotificacioServiceWsException;

    RespostaAltaV2 altaV2(Notificacio notificacio) throws NotificacioServiceWsException;

    /**
	 * Dona permís de consulta sobre un procediment.
	 *
	 * @param permisConsulta Dades per a donar d'alta la notificació.
	 * @return la llista de referencies generades per NOTIB (una per enviament)).
	 */
	boolean donarPermisConsulta(PermisConsulta permisConsulta);
	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 *
	 * @param identificador Referència de l'enviament a consultar.
	 * @return la informació de la notificació amb l'enviament especificat.
	 */
	RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador);
	RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador);

	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 *
	 * @param referencia Referència de l'enviament a consultar.
	 * @return la informació de la notificació amb l'enviament especificat.
	 */
	RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia);
	RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException;

	/**
	 * Consulta l'estat d'un enviament d'una notificació.
	 *
	 * @param dadesConsulta Dades necessàries per realitzar la consulta.
	 * @return la informació de la notificació amb l'enviament especificat.
	 */
	RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta);
	RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta);

	/**
	 * Consulta el justificant de l'enviament d'una notificació.
	 *
	 * @param identificador Identificador de la notificació consultada.
	 * @return la informació de la notificació amb l'enviament especificat.
	 */
	RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador);


	/**
	 * Amplia el plaç d'expiracio de Notific@ de les remeses
	 * @param ampliarPlazo objecte amb les dades per ampliar el plaç d'una o més remeses
	 * @Return la informació de l'ampliació
	 */
    RespuestaAmpliarPlazoOE ampliarPlazo(AmpliarPlazoOE ampliarPlazo);

	/**
	 * Amplia el plaç d'expiracio de Notific@ dels enviaments
	 * @param anulacio objecte amb els identificadors de Notifica dels enviaments a anular i el motiu de l'anulació
	 * @Return la informació de l'anulacio
	 */
	RespostaAnular anular(Anulacio anulacio);
}
