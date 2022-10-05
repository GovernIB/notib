/**
 * 
 */
package es.caib.notib.logic.intf.acl;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Permisos addicionals pel suport d'ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	public static final Permission USUARI = new ExtendedPermission(1 << 5, 'U'); // 32
	public static final Permission ADMINISTRADOR = new ExtendedPermission(1 << 6, 'M'); // 64
	public static final Permission ADMINISTRADORENTITAT = new ExtendedPermission(1 << 7, 'E'); // 128
	public static final Permission APLICACIO = new ExtendedPermission(1 << 8, 'L'); // 256


	public static final Permission PROCESSAR = new ExtendedPermission(1 << 9, 'P'); // 512
	public static final Permission NOTIFICACIO = new ExtendedPermission(1 << 10, 'N'); // 1024

	public static final Permission COMUNS = new ExtendedPermission(1 << 11, 'O'); // 2048
	public static final Permission COMUNICACIO_SIR = new ExtendedPermission(1 << 12, 'S'); // 4096
	
	protected ExtendedPermission(int mask) {
		super(mask);
	}
	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
