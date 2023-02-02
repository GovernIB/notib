/**
 * 
 */
package es.caib.notib.back.helper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilitat per a finestres sense decoració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NodecoHelper {

	private static final String PREFIX_NODECO = "/nodeco";
	private static final String REQUEST_ATTRIBUTE_NODECO = "NodecoHelper.Nodeco";
	private static final String SESSION_ATTRIBUTE_REQUESTPATHSMAP = "NodecoHelper.RequestPathsMap";


	public static boolean isNodeco(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_NODECO) != null;
	}
	public static boolean comprovarNodecoInterceptor(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (isRequestPathNodeco(request)) {
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
			marcarNodeco(request);
		}
		return true;
	}

	private static boolean isRequestPathNodeco(HttpServletRequest request) {

		var servletPath = request.getServletPath();
		return servletPath.startsWith(PREFIX_NODECO);
	}

	private static String getUriSensePrefix(HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_NODECO.length());
	}

	private static Set<String> getRequestPathsMap(HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		var requestPathsMap = (Set<String>)request.getSession().getAttribute(SESSION_ATTRIBUTE_REQUESTPATHSMAP);
		if (requestPathsMap != null) {
			return requestPathsMap;
		}
		requestPathsMap = new HashSet<>();
		request.getSession().setAttribute(SESSION_ATTRIBUTE_REQUESTPATHSMAP, requestPathsMap);
		return requestPathsMap;
	}

	private static void marcarNodeco(HttpServletRequest request) {
		request.setAttribute(REQUEST_ATTRIBUTE_NODECO, new Boolean(true));
	}

}
