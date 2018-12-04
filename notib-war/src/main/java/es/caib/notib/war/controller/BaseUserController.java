/**
 * 
 */
package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.war.helper.EntitatHelper;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisos( 
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (!entitat.isUsuariActualAdministradorEntitat()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari entitat");
		}
		return entitat;
	}

}
