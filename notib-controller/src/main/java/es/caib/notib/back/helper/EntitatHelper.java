/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class EntitatHelper {

	private static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	private static final String REQUEST_ATTRIBUTE_ENTITATS = "EntitatHelper.entitats";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL = "EntitatHelper.entitatActual";
	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";


	public static List<EntitatDto> findEntitatsAccessibles(HttpServletRequest request) {
		return findEntitatsAccessibles(request, null, null);
	}

	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(HttpServletRequest request, AplicacioService aplicacioService, EntitatService entitatService) {

		var entitats = (List<EntitatDto>)request.getAttribute(REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			PermisosHelper.comprovarPermisosEntitatsUsuariActual(request, entitatService);
			var rolActual = RolHelper.getRolActual(request, aplicacioService);
			if (rolActual == null) {
				rolActual = "";
			}
			entitats = entitatService.findAccessiblesUsuariActual(rolActual);
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		//Si només hi ha una entitat
		if (entitats != null && entitats.size() == 1) {
			canviEntitatActual(request, aplicacioService, entitats.get(0));
		}
		return entitats;
	}
	public static void processarCanviEntitats(HttpServletRequest request, AplicacioService aplicacioService, EntitatService entitatService) {

		var canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		var canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			log.debug("Processant canvi entitat (id=" + canviEntitat + ")");
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
			return;
		}
		if (canviRol == null || canviRol.isEmpty()) {
			return;
		}
		var entitats = findEntitatsAccessibles(request, aplicacioService, entitatService);
		var entitatActual = getEntitatActual(request);
		if (!entitats.isEmpty() && !entitats.contains(entitatActual)) {
			canviEntitatActual(request, aplicacioService, entitats.get(0));
		}
	}

	public static EntitatDto getEntitatActual(HttpServletRequest request) {
		return getEntitatActual(request, null, null);
	}

	public static EntitatDto getEntitatActual(HttpServletRequest request, AplicacioService aplicacioService, EntitatService entitatService) {

		var entitatActual = (EntitatDto)request.getSession().getAttribute(SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual != null) {
			return entitatActual;
		}
		var entitats = findEntitatsAccessibles(request, aplicacioService, entitatService);
		if (entitats != null && aplicacioService != null) {
			var ultimaEntitat = aplicacioService.getUsuariActual().getUltimaEntitat();
			for (var entitat: entitats) {
				if (entitat.getId().equals(ultimaEntitat)) {
					entitatActual = entitat;
					canviEntitatActual(request, aplicacioService, entitatActual);
					break;
				}
			}
		}
		if (entitatActual != null) {
			return entitatActual;
		}
		if (entitats != null && entitats.size() > 0) {
			entitatActual = entitats.get(0);
			canviEntitatActual(request, aplicacioService, entitatActual);
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}

	public static void actualitzarEntitatActualEnSessio(HttpServletRequest request, AplicacioService aplicacioService, EntitatService entitatService) {

		// És necessari tornar a consultar la informació de les entitats de la BBDD
		request.removeAttribute(REQUEST_ATTRIBUTE_ENTITATS);
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		getEntitatActual(request, aplicacioService, entitatService);
	}

	private static void canviEntitatActual(HttpServletRequest request, AplicacioService aplicacioService, EntitatDto entitatActual) {

		request.getSession().setAttribute(SESSION_ATTRIBUTE_ENTITAT_ACTUAL, entitatActual);
		if (aplicacioService != null && entitatActual != null) {
			aplicacioService.updateEntitatUsuariActual(entitatActual.getId());
		}
	}

}
