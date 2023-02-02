package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.PermisosService;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a gestionar els permisos de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosHelper {


	public static void comprovarPermisosProcedimentsUsuariActual(HttpServletRequest request, PermisosService permisosService, AplicacioService aplicacioService) {

		if (!RolHelper.isUsuariActualUsuari(request)) {
			return;
		}
		var entitatActual = EntitatHelper.getEntitatActual(request);
		var usuariActual = aplicacioService.getUsuariActual();

		if (entitatActual == null || usuariActual == null || !RolHelper.isUsuariActualUsuari(request)) {
			return;
		}
		request.setAttribute("permisNotificacioMenu", permisosService.hasPermisNotificacio(entitatActual.getId(), usuariActual.getCodi()));
		request.setAttribute("permisComunicacioMenu", permisosService.hasPermisComunicacio(entitatActual.getId(), usuariActual.getCodi()));
		request.setAttribute("permisComunicacioSirMenu", permisosService.hasPermisComunicacioSir(entitatActual.getId(), usuariActual.getCodi()));
	}
	
	public static void comprovarPermisosEntitatsUsuariActual(HttpServletRequest request, EntitatService entitatService) {

		//Comprovar si té permisos sobre alguna entitat
		var permisos = entitatService.getPermisosEntitatsUsuariActual();
		request.setAttribute("permisUsuariEntitat", permisos.get(RolEnumDto.tothom));
		request.setAttribute("permisAdminEntitat", permisos.get(RolEnumDto.NOT_ADMIN));
		request.setAttribute("permisAplicacioEntitat", permisos.get(RolEnumDto.NOT_APL));
		request.setAttribute("permisAdminOrgan", permisos.get(RolEnumDto.NOT_ADMIN_ORGAN));
	}
}
