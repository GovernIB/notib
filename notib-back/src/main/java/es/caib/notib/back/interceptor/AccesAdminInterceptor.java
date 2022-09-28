/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.back.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat d'administradors.
 * Un administrador pot ser d'entitat, d'òrgan gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesAdminInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (RolHelper.isUsuariActualAdministradorEntitat(request) || RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			return true;
		}
		throw new SecurityException(aplicacioService.getMissatgeErrorAccesAdmin(), null);
	}

}
