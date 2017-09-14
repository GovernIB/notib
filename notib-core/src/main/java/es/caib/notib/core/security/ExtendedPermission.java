/**
 * 
 */
package es.caib.notib.core.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Permisos addicionals pel suport d'ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	public static final Permission REPRESENTANT = new ExtendedPermission(1 << 5, 'T'); // 32
	public static final Permission APLICACIO = new ExtendedPermission(1 << 6, 'L'); // 64

	protected ExtendedPermission(int mask) {
		super(mask);
	}
	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
