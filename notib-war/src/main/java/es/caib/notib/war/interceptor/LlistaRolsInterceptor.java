/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.helper.ContingutEstaticHelper;
import es.caib.notib.war.helper.OrganGestorHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Interceptor per a gestionar la llista de rols a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaRolsInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private OrganGestorService organGestorService;
	
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			RolHelper.processarCanviRols(
					request);
			OrganGestorHelper.getOrgansGestorsUsuariActual(
					request, 
					organGestorService);
		}
		return true;
	}

}
