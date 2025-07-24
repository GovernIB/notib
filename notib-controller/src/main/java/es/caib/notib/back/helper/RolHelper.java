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

	private RolHelper() {
		throw new IllegalStateException("RolHelper no pot ser instanciat");
	}

	public static final String ROLE_SUPER = RolEnumDto.NOT_SUPER.name();
	public static final String ROLE_ADMIN_ENTITAT = RolEnumDto.NOT_ADMIN.name();
	public static final String ROLE_ADMIN_LECTURA = RolEnumDto.NOT_ADMIN_LECTURA.name();
	public static final String ROLE_USUARI = RolEnumDto.tothom.name();
	public static final String ROLE_APLICACIO = RolEnumDto.NOT_APL.name();
	public static final String ROLE_ADMIN_ORGAN = RolEnumDto.NOT_ADMIN_ORGAN.name();
	public static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	public static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	public static final String REQUEST_PARAMETER_CANVI_ORGAN= "canviOrgan";


	public static boolean isUsuariActualAdministrador(String rolActual) {
		return ROLE_SUPER.equals(rolActual);
	}
	public static boolean isUsuariActualAdministradorEntitat(String rolActual) {
		return ROLE_ADMIN_ENTITAT.equals(rolActual);
	}
	public static boolean isUsuariActualAdministradorLectura(String rolActual) {
		return ROLE_ADMIN_LECTURA.equals(rolActual);
	}
	public static boolean isUsuariActualUsuari(String rolActual) {
		return ROLE_USUARI.equals(rolActual);
	}
	public static boolean isUsuariActualAplicacio(String rolActual) {
		return ROLE_APLICACIO.equals(rolActual);
	}
	public static boolean isUsuariActualUsuariAdministradorOrgan(String rolActual) {
		return ROLE_ADMIN_ORGAN.equals(rolActual);
	}

}
