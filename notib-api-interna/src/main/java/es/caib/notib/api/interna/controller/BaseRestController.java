/**
 * 
 */
package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.core.api.exception.PluginException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.util.UtilitatsNotib;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.RequestContext;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJBAccessException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Controlador base del servei REST per a la gestio de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class BaseRestController implements MessageSourceAware {

	@Autowired
	protected AplicacioService aplicacioService;
	@Autowired
	protected NotificacioServiceWsV2 notificacioServiceWsV2;

	MessageSource messageSource;

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(HttpMessageNotReadableException ex) {
		log.error("Retornant HTTP 400 Bad Request", ex);
		throw ex;
	}

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

	public String getCodiUsuariActual() {

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			return auth != null ? auth.getName() : null;
		} catch (Exception ex) {
			return null;
		}
	}

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		if (fileContent == null) {
			throw new PluginException("No s'ha pogut descarregar el fitxer");
		}
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty())
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
