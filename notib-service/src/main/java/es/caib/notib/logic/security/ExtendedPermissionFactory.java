/**
 * 
 */
package es.caib.notib.logic.security;

import es.caib.notib.logic.intf.acl.ExtendedPermission;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.model.Permission;

/**
 * Factory per a la instanciació de permisos
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermissionFactory extends DefaultPermissionFactory {

	public ExtendedPermissionFactory() {
        registerPublicPermissions(ExtendedPermission.class);
    }

    public ExtendedPermissionFactory(Class<? extends Permission> permissionClass) {
        registerPublicPermissions(permissionClass);
    }

}
