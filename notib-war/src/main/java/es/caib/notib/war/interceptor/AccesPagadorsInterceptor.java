/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.RolHelper;

/**
 * Interceptor per controlar l'accés als pagadors postals/cie només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesPagadorsInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!RolHelper.isUsuariActualAdministradorEntitat(request)) {
			String name = "";
			try {
				name = SecurityContextHolder.getContext().getAuthentication().getName();
			} catch (Exception ex) {
				name = "AUTH_ERROR";
			}
			throw new SecurityException("L'usuari actual " + name + " no pot accedir a la gestió de pagadors", null);
		}
		return true;
	}

}
