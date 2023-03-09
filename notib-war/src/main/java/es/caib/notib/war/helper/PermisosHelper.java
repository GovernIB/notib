package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.PermisosService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		UsuariDto usuariActual = aplicacioService.getUsuariActual();

		if (entitatActual == null || usuariActual == null || !RolHelper.isUsuariActualUsuari(request)) {
			return;
		}
		request.setAttribute("permisNotificacioMenu", permisosService.hasPermisNotificacio(entitatActual.getId(), usuariActual.getCodi()));
		request.setAttribute("permisComunicacioMenu", permisosService.hasPermisComunicacio(entitatActual.getId(), usuariActual.getCodi()));
		request.setAttribute("permisComunicacioSirMenu", permisosService.hasPermisComunicacioSir(entitatActual.getId(), usuariActual.getCodi()));
	}
	
	public static void comprovarPermisosEntitatsUsuariActual(HttpServletRequest request, EntitatService entitatService) {

		//Comprovar si té permisos sobre alguna entitat
		Map<RolEnumDto, Boolean> permisos = entitatService.getPermisosEntitatsUsuariActual();
		request.setAttribute("permisUsuariEntitat", permisos.get(RolEnumDto.tothom));
		request.setAttribute("permisAdminEntitat", permisos.get(RolEnumDto.NOT_ADMIN));
		request.setAttribute("permisAplicacioEntitat", permisos.get(RolEnumDto.NOT_APL));
		request.setAttribute("permisAdminOrgan", permisos.get(RolEnumDto.NOT_ADMIN_ORGAN));
	}
}
