/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.RolEnumDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class RolHelper {

	public static final String ROLE_SUPER = RolEnumDto.NOT_SUPER.name(); 				// "NOT_SUPER";
	public static final String ROLE_ADMIN_ENTITAT = RolEnumDto.NOT_ADMIN.name(); 		// "NOT_ADMIN";
	public static final String ROLE_USUARI = RolEnumDto.tothom.name(); 					// "tothom";
	public static final String ROLE_APLICACIO = RolEnumDto.NOT_APL.name(); 				// "NOT_APL";
	public static final String ROLE_ADMIN_ORGAN = RolEnumDto.NOT_ADMIN_ORGAN.name(); 	// "NOT_ADMIN_ORGAN";

	public static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	public static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	public static final String REQUEST_PARAMETER_CANVI_ORGAN= "canviOrgan";


	public static boolean isUsuariActualAdministrador(String rolActual) {
		return rolActual != null && ROLE_SUPER.equals(rolActual);
	}
	public static boolean isUsuariActualAdministradorEntitat(String rolActual) {
		return rolActual != null && ROLE_ADMIN_ENTITAT.equals(rolActual);
	}
	public static boolean isUsuariActualUsuari(String rolActual) {
		return rolActual != null && ROLE_USUARI.equals(rolActual);
	}
	public static boolean isUsuariActualAplicacio(String rolActual) {
		return rolActual != null && ROLE_APLICACIO.equals(rolActual);
	}
	public static boolean isUsuariActualUsuariAdministradorOrgan(String rolActual) {
		return rolActual != null && ROLE_ADMIN_ORGAN.equals(rolActual);
	}

}
