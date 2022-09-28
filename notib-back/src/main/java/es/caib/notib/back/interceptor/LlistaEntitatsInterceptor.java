/**
 * 
 */
package es.caib.notib.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.notib.logic.intf.dto.EntitatDto;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.back.helper.ContingutEstaticHelper;
import es.caib.notib.back.helper.EntitatHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.security.Principal;

/**
 * Interceptor per a gestionar la llista d'entitats a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LlistaEntitatsInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntitatService entitatService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			EntitatHelper.findEntitatsAccessibles(request, aplicacioService, entitatService);
			EntitatHelper.processarCanviEntitats(request, aplicacioService, entitatService);
		}
		EntitatDto entitatDto = EntitatHelper.getEntitatActual(request);
		if (entitatDto != null) {
			entitatService.setConfigEntitat(entitatDto);
		}
		return true;
	}

}
