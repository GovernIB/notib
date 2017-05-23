/**
 * 
 */
package es.caib.notib.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioHelper {

	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	public static final String SESSION_ATTRIBUTE_CONTENIDOR_VISTA = "SessioHelper.contenidorVista";

	public static final String SESSION_EN_CONSULTA = "en_consulta";
	public static final String SESSION_EN_ENTITAT = "en_entitat";

	public static boolean esEnConsulta( HttpServletRequest request ) {
		return (boolean) request.getSession().getAttribute( SESSION_EN_CONSULTA );
	}
	public static void setEnConsulta( HttpServletRequest request ) {
		request.getSession().setAttribute( SESSION_EN_CONSULTA, true );
		request.getSession().setAttribute( SESSION_EN_ENTITAT, false );
	}

	public static boolean esEnEntitat( HttpServletRequest request ) {
		return (boolean) request.getSession().getAttribute( SESSION_EN_ENTITAT );
	}
	public static void setEnEntitat( HttpServletRequest request ) {
		request.getSession().setAttribute( SESSION_EN_ENTITAT, true );
		request.getSession().setAttribute( SESSION_EN_CONSULTA, false );
	}

	public static void processarAutenticacio(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		if (request.getUserPrincipal() != null) {
			Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			if (autenticacioProcessada == null) {
				aplicacioService.processarAutenticacioUsuari();
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_AUTH_PROCESSADA,
						new Boolean(true));
			}
		}
	}
	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}

}
