/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	public static void processarAutenticacio(HttpServletRequest request, HttpServletResponse response, AplicacioService aplicacioService, EntitatService entitatService) {

		HttpSession session =  request.getSession();
		if (request.getUserPrincipal() != null) {
			Boolean autenticacioProcessada = (Boolean) session.getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			if (autenticacioProcessada == null) { // Authenticam usuari
				aplicacioService.processarAutenticacioUsuari();
				session.setAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA, Boolean.TRUE);
			}
		}
		UsuariDto usuari = aplicacioService.getUsuariActual();
		String idioma_usuari = usuari.getIdioma();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

		session.setAttribute("SessionHelper.capsaleraCapLogo", aplicacioService.propertyGet("es.caib.notib.capsalera.logo"));
		session.setAttribute("SessionHelper.capsaleraPeuLogo", aplicacioService.propertyGet("es.caib.notib.peu.logo"));
		session.setAttribute("SessionHelper.capsaleraColorFons", aplicacioService.propertyGet("es.caib.notib.capsalera.color.fons"));
		session.setAttribute("SessionHelper.capsaleraColorLletra", aplicacioService.propertyGet("es.caib.notib.capsalera.color.lletra"));
		session.setAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI, idioma_usuari);
		session.setAttribute("dadesUsuariActual", usuari);
		// Assegurem que l'entitat i rol actual s'hagin carregat correctament
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, aplicacioService, entitatService);
		aplicacioService.actualitzarEntiatThreadLocal(entitat);
		RolHelper.getRolActual(request, aplicacioService);
        localeResolver.setLocale(request, response, StringUtils.parseLocaleString((String)request.getSession().getAttribute(SESSION_ATTRIBUTE_IDIOMA_USUARI)));
	}

	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}
	
	public static void setUsuariActual(HttpServletRequest request, UsuariDto usuari) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE_USUARI_ACTUAL, usuari);
	}
	
	public static String getIdioma(AplicacioService aplicacioService) {
		return aplicacioService.getUsuariActual().getIdioma();
	}

}
