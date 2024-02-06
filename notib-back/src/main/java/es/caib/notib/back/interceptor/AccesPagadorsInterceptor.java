/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés als pagadors postals/cie només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesPagadorsInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private SessionScopedContext sessionScopedContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual())) {
			return true;
		}
		var name = "";
		try {
			name = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ex) {
			name = "AUTH_ERROR";
		}
		throw new SecurityException("L'usuari actual " + name + " no pot accedir a la gestió de pagadors", null);
	}

}
