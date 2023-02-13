/**
 * 
 */
package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesUsuariInterceptor extends HandlerInterceptorAdapter {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (RolHelper.isUsuariActualUsuari(request)) {
			String name = "";
			try {
				name = SecurityContextHolder.getContext().getAuthentication().getName();
			} catch (Exception ex) {
				name = "AUTH_ERROR";
			}
			throw new SecurityException("No es pot accedir a la página amb el rol usuari. " +
					"L'usuari actual " + name + " no té cap rol requerit.", null);
		}
		return true;
	}

}
