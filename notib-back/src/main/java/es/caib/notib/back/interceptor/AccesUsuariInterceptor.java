/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesUsuariInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private AplicacioService aplicacioService;
	@Autowired
	private SessionScopedContext sessionScopedContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual())) {
			return true;
		}
		var name = "";
		try {
			name = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ex) {
			name = "AUTH_ERROR";
		}
		throw new SecurityException("No es pot accedir a la página amb el rol usuari. L'usuari actual " + name + " no té cap rol requerit.", null);
	}

}
