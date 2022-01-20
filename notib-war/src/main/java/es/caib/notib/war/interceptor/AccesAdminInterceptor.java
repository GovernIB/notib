/**
 * 
 */
package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat d'administradors.
 * Un administrador pot ser d'entitat, d'òrgan gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesAdminInterceptor extends HandlerInterceptorAdapter {

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
