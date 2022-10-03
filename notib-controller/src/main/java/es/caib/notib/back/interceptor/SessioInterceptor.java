/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.back.helper.SessioHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Interceptor per a les accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class SessioInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	AplicacioService aplicacioService;
	@Autowired
	EntitatService entitatService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService);
		return true;
	}

}
