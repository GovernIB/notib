/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.helper.RolHelper;
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
public class AccesSuperInterceptor implements AsyncHandlerInterceptor {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!RolHelper.isUsuariActualAdministrador(request)) {
			throw new SecurityException("Es necessari el rol de superusuari per accedir a aquesta página.", null);
		}
		return true;
	}

}
