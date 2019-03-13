/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.helper.AplicacioHelper;
import es.caib.notib.war.helper.PermisosHelper;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntitatService entitatService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		AplicacioHelper.comprovarVersioActual(request, aplicacioService);
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		PermisosHelper.comprovarPermisosEntitatsUsuariActual(
				request,
				entitatService);
		return true;
	}

}
