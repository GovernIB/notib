/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.back.helper.ContingutEstaticHelper;
import es.caib.notib.back.helper.OrganGestorHelper;
import es.caib.notib.back.helper.RolHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Interceptor per a gestionar la llista de rols a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LlistaRolsInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			RolHelper.processarCanviRols(
					request,
					aplicacioService);
			OrganGestorHelper.getOrgansGestorsUsuariActual(
					request,
					organGestorService);
		}
		return true;
	}

}
