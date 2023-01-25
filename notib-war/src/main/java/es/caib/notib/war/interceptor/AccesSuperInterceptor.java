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
 * Interceptor per controlar l'accés als pagadors postals/cie només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesSuperInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		if (!RolHelper.isUsuariActualAdministrador(request))
			throw new SecurityException("Es necessari el rol de superusuari per accedir a aquesta página.", null); // L'usuari actual " + usuariActual.getCodi() + " no té el rol.", null);
		
		return true;
	}

}