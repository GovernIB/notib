/**
 * 
 */
package es.caib.notib.war.interceptor;

import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.ServeiService;
import es.caib.notib.war.helper.OrganGestorHelper;
import es.caib.notib.war.helper.PermisosHelper;
import es.caib.notib.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosInterceptor extends HandlerInterceptorAdapter {

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

		if (RolHelper.isUsuariActualAdministrador(request) || RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			OrganGestorHelper.setOrgansProcedimentsNoSincronitzats(request, procedimentService);
			OrganGestorHelper.setOrgansServeisNoSincronitzats(request, serveiService);
		}
		return true;
	}

}
