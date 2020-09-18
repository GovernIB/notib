/**
 * 
 */
package es.caib.notib.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.RolHelper;

/**
 * Interceptor per controlar l'accés a la gestió d'òrgans gestors només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesOrgansInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		if (!RolHelper.isUsuariActualAdministradorEntitat(request) &&
				!RolHelper.isUsuariActualUsuariAdministradorOrgan(request))
			throw new SecurityException("L'usuari actual " + usuariActual.getCodi() + " no pot accedir a la gestió d'òrgans gestors", null);
		
		return true;
	}

}
