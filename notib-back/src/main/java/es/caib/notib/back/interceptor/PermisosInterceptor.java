/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.back.helper.OrganGestorHelper;
import es.caib.notib.back.helper.PermisosHelper;
import es.caib.notib.back.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		PermisosHelper.comprovarPermisosProcedimentsUsuariActual(request, procedimentService, organGestorService, aplicacioService);

		if (RolHelper.isUsuariActualAdministradorEntitat(request) || RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			OrganGestorHelper.setOrgansProcedimentsNoSincronitzats(request, procedimentService);
			OrganGestorHelper.setOrgansServeisNoSincronitzats(request, serveiService);
		}
		return true;
	}

}
