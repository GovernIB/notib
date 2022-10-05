/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.NotificacioServiceWs;
import es.caib.notib.logic.intf.util.UtilitatsNotib;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controlador base del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class NotificacioApiRestBaseController {

	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected NotificacioServiceWs notificacioServiceWs;

	protected String getErrorDescripcio(Exception e) {

		String errorDescripcio;
		if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
			errorDescripcio = "L'usuari " + aplicacioService.getUsuariActual().getCodi() + " no té els permisos necessaris: " + e.getMessage();
		} else {
			errorDescripcio = UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(e, EJBAccessException.class);
			if (errorDescripcio == null || errorDescripcio.isEmpty()) {
				errorDescripcio = e.getMessage();
			}
		}
		return errorDescripcio;
	}

	protected String extractIdentificador(HttpServletRequest request) {

		String url = request.getRequestURL().toString();
		String[] urlArr = url.split("/consultaEstatNotificacio|/consultaEstatEnviament|/consultaJustificantNotificacio");
		String referencia = urlArr.length > 1 ? urlArr[1].substring(1) : "";
		return referencia;
	}

	public RespostaConsultaJustificantEnviament consultaJustificant(HttpServletRequest request) {

		try {
			String referencia = extractIdentificador(request);
			if (!referencia.isEmpty()) {
				return notificacioServiceWs.consultaJustificantEnviament(referencia);
			}
			String msg = "No s'ha informat cap referència de l'enviament";
			return RespostaConsultaJustificantEnviament.builder().error(true).errorDescripcio(msg).errorData(new Date()).build();
		} catch (Exception e) {
			return RespostaConsultaJustificantEnviament.builder().error(true).errorDescripcio(getErrorDescripcio(e)).errorData(new Date()).build();
		}
	}

	public String donarPermisConsulta(PermisConsulta permisConsulta) {

		try {
			return notificacioServiceWs.donarPermisConsulta(permisConsulta) ? "OK" : null;
		} catch (Exception e) {
			return getErrorDescripcio(e);
		}
	}
}
