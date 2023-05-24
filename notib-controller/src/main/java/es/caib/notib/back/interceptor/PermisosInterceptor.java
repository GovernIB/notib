/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.back.helper.OrganGestorHelper;
import es.caib.notib.back.helper.PermisosHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosInterceptor implements AsyncHandlerInterceptor {

	@Autowired @Lazy
	private ProcedimentService procedimentService;
	@Autowired @Lazy
	private ServeiService serveiService;
	@Autowired @Lazy
	private PermisosService permisosService;
	@Autowired @Lazy
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		PermisosHelper.comprovarPermisosProcedimentsUsuariActual(request, permisosService, aplicacioService);
		if (RolHelper.isUsuariActualAdministradorEntitat(request) || RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			OrganGestorHelper.setOrgansProcedimentsNoSincronitzats(request, procedimentService);
			OrganGestorHelper.setOrgansServeisNoSincronitzats(request, serveiService);
		}
		return true;
	}

}
