/**
 * 
 */
package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
		return findEntitatsAccessibles(request, null, null);
	}
	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getAttribute(
				REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			PermisosHelper.comprovarPermisosEntitatsUsuariActual(request, entitatService);
			String rolActual = RolHelper.getRolActual(request, aplicacioService);
			if (rolActual == null)
				rolActual = "";
			entitats = entitatService.findAccessiblesUsuariActual(rolActual);
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		//Si només hi ha una entitat
		if (entitats != null && entitats.size() == 1) {
			canviEntitatActual(request, aplicacioService, entitats.get(0));
		}
		return entitats;
	}
	public static void processarCanviEntitats(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = new Long(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, aplicacioService, entitatService);
				for (EntitatDto entitat: entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, aplicacioService, entitat);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		} else if (canviRol != null && canviRol.length() > 0) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, aplicacioService, entitatService);
			EntitatDto entitatActual = getEntitatActual(request);
			if (!entitats.isEmpty() && !entitats.contains(entitatActual)) {
				canviEntitatActual(request, aplicacioService, entitats.get(0));
			}
		}
	}

	public static EntitatDto getEntitatActual(
			HttpServletRequest request) {
		return getEntitatActual(request, null, null);
	}
	public static EntitatDto getEntitatActual(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			EntitatService entitatService) {
		EntitatDto entitatActual = (EntitatDto)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual == null) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, aplicacioService, entitatService);
			if (entitats != null && aplicacioService != null) {
				Long ultimaEntitat = aplicacioService.getUsuariActual().getUltimaEntitat();
				for (EntitatDto entitat: entitats) {
					if (entitat.getId().equals(ultimaEntitat)) {
						entitatActual = entitat;
						canviEntitatActual(request, aplicacioService, entitatActual);
						break;
					}
				}
			}
			if (entitatActual == null) {
				if (entitats != null && entitats.size() > 0) {
					entitatActual = entitats.get(0);
					canviEntitatActual(request, aplicacioService, entitatActual);
				}
			}
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}



	private static void canviEntitatActual(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			EntitatDto entitatActual) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL,
				entitatActual);
		if (aplicacioService != null && entitatActual != null)
			aplicacioService.updateEntitatUsuariActual(entitatActual.getId());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
