/**
 * 
 */
package es.caib.notib.back.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitat per a gestionar els objectes de la sessió de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RequestSessionHelper {

	private RequestSessionHelper() {
		throw new IllegalStateException("RequestSessionHelper no es instanciable");
	}

	public static Object obtenirObjecteSessio(HttpServletRequest request, String clau) {
		return request.getSession().getAttribute(clau);
	}
	public static void actualitzarObjecteSessio(HttpServletRequest request, String clau, Object valor) {
		request.getSession().setAttribute(clau, valor);
	}
	public static void esborrarObjecteSessio(HttpServletRequest request, String clau) {
		request.getSession().removeAttribute(clau);
	}
	public static boolean existeixObjecteSessio(HttpServletRequest request, String clau) {
		return request.getSession().getAttribute(clau) != null;
	}
	public static boolean isError(HttpServletRequest request) {
		return request.getAttribute("javax.servlet.error.request_uri") != null;
	}

	public static String getJsonSession(HttpServletRequest request) throws IOException {
		Map<String, Object> sessionData = new HashMap<>();
		Enumeration<String> attributeNames = request.getSession().getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			sessionData.put(attributeName, request.getSession().getAttribute(attributeName));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper.writeValueAsString(sessionData);
	}
}