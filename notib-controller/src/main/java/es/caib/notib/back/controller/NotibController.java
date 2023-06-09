/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.AjaxHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.ModalHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.error.ErrorController;
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

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Controlador amb utilitats per a l'aplicació NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
public class NotibController implements ErrorController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	BuildProperties buildProperties;
	@Autowired
	SessionScopedContext sessionScopedContext;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(HttpServletRequest request) {

		var rolActual = sessionScopedContext.getRolActual();
		return RolHelper.ROLE_SUPER.equals(rolActual) ? "redirect:/integracio" :
				RolHelper.ROLE_APLICACIO.equals(rolActual) ? "redirect:/api/rest" : "redirect:/notificacio";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
			return "redirect:integracio";
		}
		if (RolHelper.isUsuariActualAplicacio(sessionScopedContext.getRolActual())) {
			return "redirect:api/rest";
		}
		if (RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual())) {
			return "redirect:notificacio";
		}
		var entitat = sessionScopedContext.getEntitatActual();
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual())) {
			return "redirect:notificacio";
		}
		if (RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {
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

	@RequestMapping(value = "/log/download", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> logDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getLogFile(request, response, null);
	}

	@RequestMapping(value = "/log/download/{dia}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> logDownload(HttpServletRequest request, @PathVariable String dia, HttpServletResponse response) throws Exception {
		return getLogFile(request, response, dia);
	}

	private ResponseEntity getLogFile(HttpServletRequest request, HttpServletResponse response, String dia) throws IOException {

		var thisPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (thisPath.startsWith("file:")) {
			thisPath = thisPath.substring(5);
		}
		var rootPath = Paths.get(thisPath).getParent().getParent().getParent();
		var logPath = aplicacioService.propertyGet("es.caib.notib.log.path", Paths.get(rootPath.toString(), "log", "server.log").toString());
		if (dia != null) {
			logPath = logPath.substring(0, logPath.length() - 4) + "." + dia + logPath.substring(logPath.length() - 4);
		}
		var logFile = new File(logPath);
		var logExist = logFile.exists();
		var fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "");
		if (logExist && request.isUserInRole("ROLE_ADMIN")) {
			fileName = "server." + (dia != null ? dia : fileName) + ".log";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			return new ResponseEntity(new FileSystemResource(logFile), HttpStatus.OK);
		}
		fileName = "no." + (dia != null ? dia : fileName) + ".log";
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.getOutputStream().write(new byte[0]);
		return null;
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
		public String getThrowableCauseClassName() {
			return throwable != null && throwable.getCause() != null ? throwable.getCause().getClass().getName() : "";
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
			var rootCause = ExceptionUtils.getRootCause(throwable);
			return rootCause != null ? rootCause.getMessage() : throwable.getMessage();
		}
	}


	@PostConstruct
	public void propagateDbProperties() {
		aplicacioService.propagateDbProperties();
//		aplicacioService.restartSchedulledTasks();
		MissatgesHelper.manifestAtributsMap = getManifestAttributes();
	}


	private Map<String, Object> getManifestAttributes() {
		Map<String, Object> manifestAtributsMap = null;

		try {
			var is = servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME);
			if (is != null) {
				manifestAtributsMap = new HashMap<>();
				var manifest = new Manifest(is);
				var manifestAtributs = manifest.getMainAttributes();
				for (var key : new HashMap(manifestAtributs).keySet()) {
					manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
				}
				aplicacioService.setAppVersion((String) manifestAtributsMap.get("Implementation-Version"));
				manifestAtributsMap.put("Build-Timestamp", formatBuildTimestamp(manifestAtributsMap.get("Build-Timestamp").toString()));
			} else if (buildProperties != null){
				manifestAtributsMap = new HashMap<>();
				manifestAtributsMap.put("Build-Timestamp", formatBuildTimestamp(buildProperties.get("Build-Timestamp")));
				manifestAtributsMap.put("Implementation-Vendor", buildProperties.get("Implementation-Vendor"));
				manifestAtributsMap.put("Implementation-Version", buildProperties.get("Implementation-Version"));
				manifestAtributsMap.put("Implementation-SCM-Branch", buildProperties.get("Implementation-SCM-Branch"));
				manifestAtributsMap.put("Implementation-SCM-Revision", buildProperties.get("Implementation-SCM-Revision"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm").withZone(ZoneId.systemDefault());
				manifestAtributsMap.put("Version", buildProperties.getVersion());
				manifestAtributsMap.put("Time", formatter.format(buildProperties.getTime()));
				aplicacioService.setAppVersion((String) buildProperties.get("Implementation-Version"));
			}
		} catch (Exception ex) {
			log.error("Error obtenint els atributs del manifest per a mostrar el numero de versió", ex);
		}
		return manifestAtributsMap;
	}

	private String formatBuildTimestamp(String timestamp) throws ParseException {
		var ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		var simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_ZERO_OFFSET);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		var dateFormat = new SimpleDateFormat("YYYY.MM.dd HH:mm", new Locale("ca", "ES"));
		var date = simpleDateFormat.parse(timestamp);
		return dateFormat.format(date) + "h";
	}
}
