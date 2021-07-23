package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.procediment.ProcedimentSimpleDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Utilitat per a gestionar els permisos de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosHelper {

	
	public static void comprovarPermisosProcedimentsUsuariActual(
			HttpServletRequest request,
			ProcedimentService procedimentService,
			NotificacioService notificacioService,
			AplicacioService aplicacioService) { 
		
		if (RolHelper.isUsuariActualUsuari(request)) {
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			UsuariDto usuariActual = aplicacioService.getUsuariActual();

			if (entitatActual != null && usuariActual != null && RolHelper.isUsuariActualUsuari(request)) {
				List<ProcedimentSimpleDto> procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO);
				request.setAttribute("permisNotificacio", !procedimentsDisponibles.isEmpty());
			}
		}
	}
	
	public static void comprovarPermisosEntitatsUsuariActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		//Comprovar si té permisos sobre alguna entitat
		Map<RolEnumDto, Boolean> permisos = entitatService.getPermisosEntitatsUsuariActual();
		request.setAttribute(
				"permisUsuariEntitat",
				permisos.get(RolEnumDto.tothom));
		request.setAttribute(
				"permisAdminEntitat",
				permisos.get(RolEnumDto.NOT_ADMIN));
		request.setAttribute(
				"permisAplicacioEntitat",
				permisos.get(RolEnumDto.NOT_APL));
		request.setAttribute(
				"permisAdminOrgan",
				permisos.get(RolEnumDto.NOT_ADMIN_ORGAN));

	}
}
