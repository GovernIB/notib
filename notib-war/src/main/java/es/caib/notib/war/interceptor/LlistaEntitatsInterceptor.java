/**
 * 
 */
package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.helper.ContingutEstaticHelper;
import es.caib.notib.war.helper.EntitatHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la llista d'entitats a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaEntitatsInterceptor extends HandlerInterceptorAdapter {

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
			entitatService.setConfigEntitat(entitatDto.getCodi());
		}
		return true;
	}

}
