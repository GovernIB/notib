/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.helper.PermisosHelper;
import es.caib.notib.logic.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosEntitatInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private EntitatService entitatService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		try {
			PermisosHelper.comprovarPermisosEntitatsUsuariActual(request, entitatService);
		} catch (Exception ex) {
			logout(request, response);
			return false;
		}
		return true;
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		SecurityContextHolder.clearContext();
		// Només per Jboss
		if (session != null) {
			// Esborrar la sessió
			session.invalidate();
		}
		// Es itera sobre totes les cookies
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				// Es sobre escriu el valor de cada cookie a NULL
				Cookie ck = new Cookie(c.getName(), null);
				ck.setPath(request.getContextPath());
				response.addCookie(ck);
			}
		}
		response.sendRedirect(request.getContextPath() + "/index");
	}

}
