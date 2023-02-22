package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.service.AvisService;
import es.caib.notib.war.helper.AvisHelper;
import es.caib.notib.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Interceptor per a comptar els elements pendents de les bústies
 * de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class AvisosInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AvisService avisService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		log.debug("[AVISOS] Executant interceptor - " + request.getRequestURI());
		List<String> rols = RolHelper.getRolsUsuariActual(request);
		// Si es un usuari que no només té accés d'aplicació
		if (rols.contains(RolHelper.ROLE_USUARI) ||
				rols.contains(RolHelper.ROLE_ADMIN_ENTITAT) ||
				rols.contains(RolHelper.ROLE_ADMIN_ORGAN) ||
				rols.contains(RolHelper.ROLE_SUPER))
			AvisHelper.findAvisos(
					request,
					avisService);
		return true;
	}

}

