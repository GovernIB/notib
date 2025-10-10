/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.NotificacioServiceWs;
import es.caib.notib.logic.intf.util.UtilitatsNotib;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controlador base del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Deprecated
public abstract class NotificacioApiRestBaseController extends BaseController {

	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected NotificacioServiceWs notificacioServiceWs;


	protected String getErrorDescripcio(Exception e) {

		String errorDescripcio;
		if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
			return "L'usuari " + getCodiUsuariActual() + " no té els permisos necessaris: " + e.getMessage();
		}
		errorDescripcio = UtilitatsNotib.getMessageExceptionOrCauseInstanceOf(e, EJBAccessException.class);
		return StringUtils.isEmpty(errorDescripcio) ? e.getMessage() : errorDescripcio;
	}

	protected String extractIdentificador(HttpServletRequest request) {

		var url = request.getRequestURL().toString();
		var urlArr = url.split("/consultaEstatNotificacio|/consultaEstatEnviament|/consultaJustificantNotificacio");
		return urlArr.length > 1 ? urlArr[1].substring(1) : "";
	}

	public RespostaConsultaJustificantEnviament consultaJustificant(HttpServletRequest request) {

		var referencia = extractIdentificador(request);
		try {
			if (!referencia.isEmpty()) {
				return notificacioServiceWs.consultaJustificantEnviament(referencia);
			}
			var errorDesc = "No s'ha informat cap referència de l'enviament";
			return RespostaConsultaJustificantEnviament.builder().error(true).errorDescripcio(errorDesc).errorData(new Date()).build();
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
