/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.helper.PermisosHelper;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosEntitatInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EntitatService entitatService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		PermisosHelper.comprovarPermisosEntitatsUsuariActual(
				request,
				entitatService);
		return true;
	}

}
