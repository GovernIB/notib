/**
 * 
 */
package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesUsuariInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (RolHelper.isUsuariActualUsuari(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			throw new SecurityException("No es pot accedir a la página amb el rol usuari. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té cap rol requerit.", null);
		}
		return true;
	}

}
