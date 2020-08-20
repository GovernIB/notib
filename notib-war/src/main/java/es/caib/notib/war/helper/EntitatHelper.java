/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.service.EntitatService;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatHelper {

	private static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	private static final String REQUEST_ATTRIBUTE_ENTITATS = "EntitatHelper.entitats";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL = "EntitatHelper.entitatActual";
	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";



	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request) {
		return findEntitatsAccessibles(request, null);
	}
	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request,
			EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getAttribute(
				REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			String rolActual = RolHelper.getRolActual(request);
			if (rolActual == null)
				rolActual = "";
			entitats = entitatService.findAccessiblesUsuariActual(rolActual);
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		//Si només hi ha una entitat
		if (entitats != null && entitats.size() == 1) {
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_ENTITAT_ACTUAL,
					entitats.get(0));
		}
		return entitats;
	}
	public static void processarCanviEntitats(
			HttpServletRequest request,
			EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
				for (EntitatDto entitat: entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, entitat);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		} else if (canviRol != null && canviRol.length() > 0) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
			EntitatDto entitatActual = getEntitatActual(request);
			if (!entitats.contains(entitatActual)) {
				canviEntitatActual(request, entitats.get(0));
			}
		}
	}

	public static EntitatDto getEntitatActual(
			HttpServletRequest request) {
		return getEntitatActual(request, null);
	}
	public static EntitatDto getEntitatActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		EntitatDto entitatActual = (EntitatDto)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual == null) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
			if (entitats != null && entitats.size() > 0) {
				entitatActual = entitats.get(0);
				canviEntitatActual(request, entitatActual);
			}
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}



	private static void canviEntitatActual(
			HttpServletRequest request,
			EntitatDto entitatActual) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL,
				entitatActual);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
