/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.helper.PermisosHelper;
import es.caib.notib.logic.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosEntitatInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private EntitatService entitatService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		try {
			PermisosHelper.comprovarPermisosEntitatsUsuariActual(request, entitatService);
		} catch (Exception ex) {
			throw new SecurityException("No s'han pogut obtenir els permisos de l'usuari", ex);
		}
		return true;
	}

}
