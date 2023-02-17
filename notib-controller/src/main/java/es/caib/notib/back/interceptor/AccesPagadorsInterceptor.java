/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.notib.back.helper.RolHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Interceptor per controlar l'accés als pagadors postals/cie només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesPagadorsInterceptor implements AsyncHandlerInterceptor {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
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
