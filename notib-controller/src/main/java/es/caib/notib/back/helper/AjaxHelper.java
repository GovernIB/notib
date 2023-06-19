/**
 * 
 */
package es.caib.notib.back.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * Utilitat per a marcar peticions AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AjaxHelper {

	private static final String PREFIX_AJAX = "/ajax";
	private static final String REQUEST_ATTRIBUTE_AJAX = "AjaxHelper.Ajax";
	private static final String SESSION_ATTRIBUTE_REQUESTPATHSMAP = "AjaxHelper.RequestPathsMap";
	public static final String ACCIO_AJAX_OK = PREFIX_AJAX + "/ok";

	public static boolean isAjax(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_AJAX) != null;
	}

	public static boolean comprovarAjaxInterceptor(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (isRequestPathAjax(request)) {
			var uriSensePrefix = getUriSensePrefix(request);
			var requestPathsMap = getRequestPathsMap(request);
			requestPathsMap.add(uriSensePrefix);
			var dispatcher = request.getRequestDispatcher(uriSensePrefix);
		    dispatcher.forward(request, response);
		    return false;
		}
		var requestPathsMap = getRequestPathsMap(request);
		var pathComprovacio = request.getServletPath();
		if (requestPathsMap.contains(pathComprovacio)) {
			requestPathsMap.remove(pathComprovacio);
			marcarAjax(request);
		}
		return true;
	}

	public static AjaxFormResponse generarAjaxFormErrors(Object objecte, BindingResult bindingResult) {
		return new AjaxFormResponse(objecte, bindingResult);
	}
	public static AjaxFormResponse generarAjaxFormOk(Object objecte) {
		return new AjaxFormResponse(objecte);
	}
	public static AjaxFormResponse generarAjaxFormOk() {
		return new AjaxFormResponse(null);
	}

	private static boolean isRequestPathAjax(HttpServletRequest request) {String servletPath = request.getServletPath();
		return servletPath.startsWith(PREFIX_AJAX) && !servletPath.startsWith(ACCIO_AJAX_OK);
	}
	private static String getUriSensePrefix(HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_AJAX.length());
	}
	private static Set<String> getRequestPathsMap(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		var requestPathsMap = (Set<String>)request.getSession().getAttribute(SESSION_ATTRIBUTE_REQUESTPATHSMAP);
		if (requestPathsMap == null) {
			requestPathsMap = new HashSet<>();
			request.getSession().setAttribute(SESSION_ATTRIBUTE_REQUESTPATHSMAP, requestPathsMap);
		}
		return requestPathsMap;
	}
	private static void marcarAjax(HttpServletRequest request) {
		request.setAttribute(REQUEST_ATTRIBUTE_AJAX, true);
	}

	@Getter
	public static class AjaxFormResponse {
		private Object objecte;
		private AjaxFormEstatEnum estat;
		private List<AjaxFormError> errorsGlobals;
		private List<AjaxFormError> errorsCamps;

		public AjaxFormResponse(Object objecte, BindingResult bindingResult) {

			super();
			this.objecte = objecte;
			if (bindingResult == null) {
				this.estat = AjaxFormEstatEnum.OK;
				return;
			}
			this.errorsGlobals = new ArrayList<>();
			String msg;
			for (var objectError: bindingResult.getGlobalErrors()) {
				msg = MessageHelper.getInstance().getMessage(objectError.getCode(), objectError.getArguments());
				errorsGlobals.add(new AjaxFormError(objectError.getObjectName(), msg));
			}
			this.errorsCamps = new ArrayList<>();
			for (var fieldError: bindingResult.getFieldErrors()) {
				msg = MessageHelper.getInstance().getMessage(fieldError.getCodes(), fieldError.getArguments(), null);
				errorsCamps.add(new AjaxFormError(fieldError.getField(), msg));
			}
			this.estat = AjaxFormEstatEnum.ERROR;
		}

		public AjaxFormResponse(Object objecte) {

			super();
			this.objecte = objecte;
			this.estat = AjaxFormEstatEnum.OK;
		}
	}

	@Getter
	public static class AjaxFormError {
		private String camp;
		private String missatge;
		public AjaxFormError(String camp, String missatge) {
			this.camp = camp;
			this.missatge = missatge;
		}
	}

	public enum AjaxFormEstatEnum {
		OK,
		ERROR
	}

}
