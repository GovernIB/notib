/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.back.helper.PermisosHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosEntitatInterceptor implements AsyncHandlerInterceptor {

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
