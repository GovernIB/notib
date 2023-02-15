package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.service.AvisService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Utilitat per obtenir els avisos de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";
	private static final String JAVA_SERVLET_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

	@SuppressWarnings("unchecked")
	public static void findAvisos(HttpServletRequest request, AvisService avisService) {
		
		var avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		var canviRol = request.getParameter(RolHelper.getRequestParameterCanviRol()) != null;
		if ((avisos != null || RequestSessionHelper.isError(request) || avisService == null) && !canviRol) {
			return;
		}
		avisos = RolHelper.isUsuariActualAdministradorEntitat(request) ?
				avisService.findActiveAdmin(EntitatHelper.getEntitatActual(request).getId()) : avisService.findActive();
		request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	
	private static boolean isError(HttpServletRequest request) {
		return request.getAttribute(JAVA_SERVLET_ERROR_REQUEST_URI) != null;
	}
}