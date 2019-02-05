/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		if (RolHelper.isUsuariActualUsuari(request)) {
			request.setAttribute(
					"permisConsulta", 
					entityComprovarHelper.hasPermisConsultaProcediment());
			request.setAttribute(
					"permisNotificacio", 
					entityComprovarHelper.hasPermisNotificacioProcediment());
		}
		return true;
	}

}
