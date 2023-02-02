/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.back.helper.RolHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Interceptor per controlar l'accés als pagadors postals/cie només per administradors d'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesPagadorsInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		var usuariActual = aplicacioService.getUsuariActual();
		if (!RolHelper.isUsuariActualAdministradorEntitat(request)) {
			throw new SecurityException("L'usuari actual " + usuariActual.getCodi() + " no pot accedir a la gestió de pagadors", null);
		}
		return true;
	}

}
