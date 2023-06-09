/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BaseUserController extends BaseController {

	@Autowired
	protected SessionScopedContext sessionScopedContext;

	public EntitatDto getEntitatActualComprovantPermisos(HttpServletRequest request) {

		var entitat = sessionScopedContext.getEntitatActual();
		var administradorOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (administradorOrgan && !entitat.isUsuariActualAdministradorOrgan()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador de òrgan");
		}
		if (!entitat.isUsuariActualAdministradorEntitat()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari entitat");
		}
		return entitat;
	}
	
	public OrganGestorDto getOrganGestorActual(HttpServletRequest request) {

		var administradorOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		return administradorOrgan ? sessionScopedContext.getOrganActual() : null;
	}
	
	public Long getOrganGestorActualId(HttpServletRequest request) {

		var organGestor = getOrganGestorActual(request);
		return organGestor != null ? organGestor.getId() : null;
	}
}
