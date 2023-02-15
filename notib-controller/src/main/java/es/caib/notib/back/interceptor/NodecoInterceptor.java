/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.notib.back.helper.NodecoHelper;

/**
 * Interceptor per a redirigir les peticions a finestres sense
 * decoració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NodecoInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return NodecoHelper.comprovarNodecoInterceptor(request, response);
	}

}