/**
 * 
 */
package es.caib.notib.war.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

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
	private static final String SESSION_ATTRIBUTE_IDIOMA_USUARI = "SessionHelper.idiomaUsuari";

	public static void processarAutenticacio(
			HttpServletRequest request,
			HttpServletResponse response,
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
		UsuariDto usuari = aplicacioService.getUsuariActual();
		String idioma_usuari = usuari.getIdioma();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        
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
		
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_IDIOMA_USUARI, 
				idioma_usuari);
		request.getSession().setAttribute(
				"dadesUsuariActual", 
				usuari);
		
        localeResolver.setLocale(
        		request, 
        		response, 
        		StringUtils.parseLocaleString(
        				(String)request.getSession().getAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI))
        		);
	}
	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}
	
	public static void setUsuariActual(HttpServletRequest request, UsuariDto usuari) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, usuari);
	}

}
