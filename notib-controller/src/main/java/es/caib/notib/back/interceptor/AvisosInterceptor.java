package es.caib.notib.back.interceptor;

import es.caib.notib.logic.intf.service.AvisService;
import es.caib.notib.back.helper.AvisHelper;
import es.caib.notib.back.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

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
@Component
public class AvisosInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private AvisService avisService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		log.info("[AVISOS] Executant interceptor");
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

