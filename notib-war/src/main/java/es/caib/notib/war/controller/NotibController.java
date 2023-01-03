/**
 * 
 */
package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.AjaxHelper;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.ModalHelper;
import es.caib.notib.war.helper.RolHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador amb utilitats per a l'aplicació NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class NotibController {

	@Autowired
	private AplicacioService aplicacioService;
	
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		
		if (RolHelper.isUsuariActualAdministrador(request)) {
			return "redirect:integracio";
		} else if (RolHelper.isUsuariActualAplicacio(request)) {
			return "redirect:api/rest";
		} else if (RolHelper.isUsuariActualUsuari(request)) {
			return "redirect:notificacio";
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			if (entitat == null)
				throw new SecurityException("No te cap entitat assignada");
			if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
				return "redirect:notificacio";
			} else if (RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
				return "redirect:notificacio";
			} else {
				return "index";
			}
		}
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
	public String error(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"errorObject",
				new ErrorObject(request));
		return "util/error";
	}

	@RequestMapping(value = "/api")
	public String api(
			HttpServletRequest request,
			Model model) {
		return "redirect:/api/rest";
	}

	@RequestMapping(value = "/log/download", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> logDownload(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return getLogFile(request, response, null);
	}

	@RequestMapping(value = "/log/download/{dia}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> logDownload(
			HttpServletRequest request,
			@PathVariable String dia,
			HttpServletResponse response) throws Exception {

		return getLogFile(request, response, dia);
	}

	private ResponseEntity getLogFile(HttpServletRequest request, HttpServletResponse response, String dia) throws IOException {
		String thisPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (thisPath.startsWith("file:")) {
			thisPath = thisPath.substring(5);
		}
		Path rootPath = Paths.get(thisPath).getParent().getParent().getParent();
		String logPath = aplicacioService.propertyGet("es.caib.notib.log.path", Paths.get(rootPath.toString(), "log", "server.log").toString());
		if (dia != null) {
			logPath = logPath.substring(0, logPath.length() - 4) + "." + dia + logPath.substring(logPath.length() - 4);
		}
		File logFile = new File(logPath);

		boolean logExist = logFile.exists();

		String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String contentType = "text/plain";

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "");

		if (logExist && request.isUserInRole("ROLE_ADMIN")) {
			fileName = "server." + (dia != null ? dia : fileName) + ".log";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			return new ResponseEntity(new FileSystemResource(logFile), HttpStatus.OK);
		} else {
			fileName = "no." + (dia != null ? dia : fileName) + ".log";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.getOutputStream().write(new byte[0]);
			return null;
		}
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
			if (requestUri == null) 
				requestUri = "Desconeguda";
			message = 
					"Retornat codi d'error " + statusCode + " "
					+ "per al recurs " + requestUri + " "
					+ "amb el missatge: " + exceptionMessage;
		}
		public Integer getStatusCode() {
			return statusCode;
		}
		public Throwable getThrowable() {
			return throwable;
		}
		public String getThrowableClassName() {
			return throwable.getClass().getName();
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
			return ExceptionUtils.getStackTrace(throwable);
		}
		public String getFullStackTrace() {
			return ExceptionUtils.getFullStackTrace(throwable);
		}
		private String getExceptionMessage(Throwable throwable, Integer statusCode) {
			if (throwable != null) {
				Throwable rootCause = ExceptionUtils.getRootCause(throwable);
				if (rootCause != null)
					return rootCause.getMessage();
				else
					return throwable.getMessage();
			} else {
				HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
				return httpStatus.getReasonPhrase();
			}
		}
	}

}
