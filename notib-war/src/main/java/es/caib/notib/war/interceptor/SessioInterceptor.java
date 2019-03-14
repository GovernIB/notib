/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.helper.SessioHelper;

/**
 * Interceptor per a les accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	EntitatService entitatService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		SessioHelper.processarAutenticacio(
				request, 
				aplicacioService);
		return true;
	}

}
