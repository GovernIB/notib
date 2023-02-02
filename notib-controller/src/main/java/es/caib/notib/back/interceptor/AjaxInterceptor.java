/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.notib.back.helper.AjaxHelper;

/**
 * Interceptor per a redirigir les peticions fetes amb AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AjaxInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return AjaxHelper.comprovarAjaxInterceptor(request, response);
	}

}
