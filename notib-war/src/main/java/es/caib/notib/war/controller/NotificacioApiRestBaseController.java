/**
 * 
 */
package es.caib.notib.war.controller;

import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.util.UtilitatsNotib;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ejb.EJBAccessException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Controlador base del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class NotificacioApiRestBaseController extends BaseController {

	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected NotificacioServiceWsV2 notificacioServiceWsV2;

	protected String getErrorDescripcio(Exception e) {
		String errorDescripcio;
		if (UtilitatsNotib.isExceptionOrCauseInstanceOf(e, EJBAccessException.class)) {
			errorDescripcio = "L'usuari " + getCodiUsuariActual() + " no té els permisos necessaris: " + e.getMessage();
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
		String referencia = extractIdentificador(request);
		try {
			if (referencia.isEmpty()) {
				return RespostaConsultaJustificantEnviament.builder()
						.error(true)
						.errorDescripcio("No s'ha informat cap referència de l'enviament")
						.errorData(new Date())
						.build();
			}
			return notificacioServiceWsV2.consultaJustificantEnviament(referencia);
		} catch (Exception e) {
			return RespostaConsultaJustificantEnviament.builder()
					.error(true)
					.errorDescripcio(getErrorDescripcio(e))
					.errorData(new Date())
					.build();
		}
	}

	public String donarPermisConsulta(PermisConsulta permisConsulta) {
		String resposta = null;
		try {
			if (notificacioServiceWsV2.donarPermisConsulta(permisConsulta)) {
				resposta = "OK";
			}
		} catch (Exception e) {
			resposta = getErrorDescripcio(e);
		}
		return resposta;
	}

	protected void logout(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(false);
		SecurityContextHolder.clearContext();
		// Només per Jboss
		if (session != null) {
			// Esborrar la sessió
			session.invalidate();
		}
		// Es itera sobre totes les cookies
		for(Cookie c : request.getCookies()) {
			// Es sobre escriu el valor de cada cookie a NULL
			Cookie ck = new Cookie(c.getName(), null);
			ck.setPath(request.getContextPath());
			response.addCookie(ck);
		}
	}
}
