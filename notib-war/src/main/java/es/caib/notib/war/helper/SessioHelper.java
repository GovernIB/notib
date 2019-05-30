/**
 * 
 */
package es.caib.notib.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioHelper {

	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	public static final String SESSION_ATTRIBUTE_CONTENIDOR_VISTA = "SessioHelper.contenidorVista";
	public static final String SESSION_ATTRIBUTE_USUARI_ACTUAL = "SessioHelper.usuariActual";


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
		
		request.getSession().setAttribute(
				"SessionHelper.capsaleraCapLogo", 
				aplicacioService.propertyFindByPrefix("es.caib.notib.capsalera.logo"));
		request.getSession().setAttribute(
				"SessionHelper.capsaleraPeuLogo", 
				aplicacioService.propertyFindByPrefix("es.caib.notib.peu.logo"));
		request.getSession().setAttribute(
				"SessionHelper.capsaleraColorFons", 
				aplicacioService.propertyFindByPrefix("es.caib.notib.capsalera.color.fons"));
		request.getSession().setAttribute(
				"SessionHelper.capsaleraColorLletra", 
				aplicacioService.propertyFindByPrefix("es.caib.notib.capsalera.color.lletra"));
		
	}
	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}
	
	public static void setUsuariActual(HttpServletRequest request, UsuariDto usuari) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, usuari);
	}

}
