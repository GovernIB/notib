/**
 * 
 */
package es.caib.notib.war.helper;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import es.caib.notib.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioHelper {

	public static final String APPLICATION_ATTRIBUTE_VERSIO_ACTUAL = "AplicacioHelper.versioActual";



	public static void comprovarVersioActual(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String versioActual = (String)request.getSession().getServletContext().getAttribute(APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
		if (versioActual == null) {
			versioActual = aplicacioService.getVersioActual();
			request.getSession().getServletContext().setAttribute(
					APPLICATION_ATTRIBUTE_VERSIO_ACTUAL,
					versioActual);
		}
	}
	public static String getVersioActual(HttpServletRequest request) {
		return (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
	}
	public static String getVersioMajorActual(HttpServletRequest request) {
		String versio_actual = (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
		return versio_actual.substring(0,StringUtils.ordinalIndexOf(versio_actual, ".", 2));
	
	}
	
}
