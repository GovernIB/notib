/**
 * 
 */
package es.caib.notib.back.helper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper per a mostrar missatges d'alerta o informació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MissatgesHelper {

	public static final String SESSION_ATTRIBUTE_ERROR = "MissatgesHelper.Error";
	public static final String SESSION_ATTRIBUTE_WARNING = "MissatgesHelper.Warning";
	public static final String SESSION_ATTRIBUTE_SUCCESS = "MissatgesHelper.Success";
	public static final String SESSION_ATTRIBUTE_INFO = "MissatgesHelper.Info";

	public static Map<String, Object> manifestAtributsMap;


	public static void error(HttpServletRequest request, String text) {
		newAlert(request, SESSION_ATTRIBUTE_ERROR, text);
	}
	public static void warning(HttpServletRequest request, String text) {
		newAlert(request, SESSION_ATTRIBUTE_WARNING, text);
	}
	public static void success(HttpServletRequest request, String text) {
		newAlert(request, SESSION_ATTRIBUTE_SUCCESS, text);
	}
	public static void info(HttpServletRequest request, String text) {
		newAlert(request, SESSION_ATTRIBUTE_INFO, text);
	}

	public List<String> getErrors(HttpServletRequest request, boolean delete) {
		return getAlerts(request, SESSION_ATTRIBUTE_ERROR, delete);
	}
	public List<String> getWarnings(HttpServletRequest request, boolean delete) {
		return getAlerts(request, SESSION_ATTRIBUTE_WARNING, delete);
	}
	public List<String> getSuccesses(HttpServletRequest request, boolean delete) {
		return getAlerts(request, SESSION_ATTRIBUTE_SUCCESS, delete);
	}
	public List<String> getInfos(HttpServletRequest request, boolean delete) {
		return getAlerts(request, SESSION_ATTRIBUTE_INFO, delete);
	}

	@SuppressWarnings("unchecked")
	private static void newAlert(HttpServletRequest request, String attributeName, String text) {

		var session = request.getSession();
		var alerts = (List<String>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(text);
	}

	@SuppressWarnings("unchecked")
	private static List<String> getAlerts(HttpServletRequest request, String attributeName, boolean delete) {

		var session = request.getSession();
		var alerts = (List<String>)session.getAttribute(attributeName);
		if (delete) {
			session.removeAttribute(attributeName);
		}
		return alerts;
	}

}
