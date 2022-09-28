/**
 * 
 */
package es.caib.notib.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.back.helper.AjaxHelper;
import es.caib.notib.back.helper.EntitatHelper;
import es.caib.notib.back.helper.ModalHelper;
import es.caib.notib.back.helper.RolHelper;

/**
 * Controlador amb utilitats per a l'aplicació NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class NotibController implements ErrorController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		if (RolHelper.isUsuariActualAdministrador(request)) {
			return "redirect:integracio";
		}
		if (RolHelper.isUsuariActualAplicacio(request)) {
			return "redirect:api/rest";
		}
		if (RolHelper.isUsuariActualUsuari(request)) {
			return "redirect:notificacio";
		}
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			return "redirect:notificacio";
		}
		if (RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			return "redirect:notificacio";
		}
		return "index";
	}

	@RequestMapping(value = ModalHelper.ACCIO_MODAL_TANCAR, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}

	@RequestMapping(value = AjaxHelper.ACCIO_AJAX_OK, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}

	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "util/missatges";
	}

	@RequestMapping(value = "/desenv/usuariActual", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto desenvUsuariActual() { 
		return aplicacioService.getUsuariActual();
	}

	@RequestMapping(value = "/error")
	public String error(HttpServletRequest request, Model model) {
		model.addAttribute("errorObject", new ErrorObject(request));
		return "util/error";
	}

	@RequestMapping(value = "/api")
	public String api(HttpServletRequest request, Model model) {
		return "redirect:/api/rest";
	}

	public static class ErrorObject {
		Integer statusCode;
		Throwable throwable;
		String exceptionMessage;
		String requestUri;
		String message;

		public ErrorObject(HttpServletRequest request) {

			statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
			throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
			exceptionMessage = getExceptionMessage(throwable, statusCode);
			requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
			if (requestUri == null) {
				requestUri = "Desconeguda";
			}
			message = "Retornat codi d'error " + statusCode + " per al recurs " + requestUri + " amb el missatge: " + exceptionMessage;
		}

		public Integer getStatusCode() {
			return statusCode;
		}
		public Throwable getThrowable() {
			return throwable;
		}
		public String getThrowableClassName() {
			return throwable != null ? throwable.getClass().getName() : "";
		}
		public String getExceptionMessage() {
			return exceptionMessage;
		}
		public String getRequestUri() {
			return requestUri;
		}
		public String getMessage() {
			return message;
		}
		public String getStackTrace() {
			return throwable != null ? ExceptionUtils.getStackTrace(throwable) : "";
		}
		public String getFullStackTrace() {
			return throwable != null ? ExceptionUtils.getStackTrace(throwable) : "";
		}
		private String getExceptionMessage(Throwable throwable, Integer statusCode) {

			if (throwable == null) {
				HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
				return httpStatus.getReasonPhrase();
			}
			Throwable rootCause = ExceptionUtils.getRootCause(throwable);
			return rootCause != null ? rootCause.getMessage() : throwable.getMessage();
		}
	}
}
