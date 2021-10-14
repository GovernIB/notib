package es.caib.notib.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.service.AvisService;

/**
 * Utilitat per obtenir els avisos de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";
	private static final String JAVA_SERVLET_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";


	@SuppressWarnings("unchecked")
	public static void findAvisos(
			HttpServletRequest request, 
			AvisService avisService) {
		
		List<AvisDto> avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		if (avisos == null && !isError(request) && avisService != null) {
			avisos = avisService.findActive();
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	
	private static boolean isError(
			HttpServletRequest request) {
		return request.getAttribute(JAVA_SERVLET_ERROR_REQUEST_URI) != null;
	}

}
