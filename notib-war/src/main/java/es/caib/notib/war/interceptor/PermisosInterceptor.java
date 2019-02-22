/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.helper.PermisosHelper;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private ProcedimentService procedimentService; 
	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		PermisosHelper.comprovarPermisosUsuariActual(
				request,
				procedimentService,
				aplicacioService);
		
		return true;
	}

}
