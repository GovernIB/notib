/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.NotificacioServiceWs;
import es.caib.notib.logic.intf.util.UtilitatsNotib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controlador base del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class NotificacioApiRestBaseController {

	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected NotificacioServiceWs notificacioServiceWs;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(HttpMessageNotReadableException ex) {
		log.error("Retornant HTTP 400 Bad Request", ex);
		throw ex;
	}

	protected String getErrorDescripcio(Exception e) {

		String errorDescripcio;
		if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
			var usr = aplicacioService.getUsuariActual();
			errorDescripcio = "L'usuari " + (usr != null ? usr.getCodi() : "") + " no té els permisos necessaris: " + e.getMessage();
			return errorDescripcio;
		}
		errorDescripcio = UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(e, EJBAccessException.class);
		if (errorDescripcio == null || errorDescripcio.isEmpty()) {
			errorDescripcio = e.getMessage();
		}
		return errorDescripcio;
	}

	protected String extractIdentificador(HttpServletRequest request) {

		var url = request.getRequestURL().toString();
		var urlArr = url.split("/consultaEstatNotificacio|/consultaEstatEnviament|/consultaJustificantNotificacio");
		return urlArr.length > 1 ? urlArr[1].substring(1) : "";
	}

	public RespostaConsultaJustificantEnviament consultaJustificant(HttpServletRequest request) {

		try {
			var referencia = extractIdentificador(request);
			if (!referencia.isEmpty()) {
				return notificacioServiceWs.consultaJustificantEnviament(referencia);
			}
			var msg = "No s'ha informat cap referència de l'enviament";
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
