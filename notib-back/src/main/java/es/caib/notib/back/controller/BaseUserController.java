/**
 * 
 */
package es.caib.notib.back.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.back.helper.EntitatHelper;
import es.caib.notib.back.helper.OrganGestorHelper;
import es.caib.notib.back.helper.RolHelper;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisos(HttpServletRequest request) {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
//		boolean administradorEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		boolean administradorOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
//		if (administradorEntitat && !entitat.isUsuariActualAdministradorEntitat()) {
//			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador de entitat");
//		}
		if (administradorOrgan && !entitat.isUsuariActualAdministradorOrgan()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador de òrgan");
		} else if (!entitat.isUsuariActualAdministradorEntitat()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari entitat");
		}
		return entitat;
	}
	
	public OrganGestorDto getOrganGestorActual(HttpServletRequest request) {

		OrganGestorDto organGestor = null;
		boolean administradorOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		if (administradorOrgan) {
			organGestor = OrganGestorHelper.getOrganGestorUsuariActual(request);
		}
		return organGestor;
	}
	
	public Long getOrganGestorActualId(HttpServletRequest request) {
		OrganGestorDto organGestor = getOrganGestorActual(request); 
		return organGestor!=null ? organGestor.getId() : null;
	}
}
