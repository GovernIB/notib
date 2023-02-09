/**
 *
 */
package es.caib.notib.war.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Proveeix els detalls de preautenticació per a Spring Security
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class RolesBasedPreAuthenticatedWebAuthenticationDetailsSource extends J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource {

	MappableAttributesRetriever mappableAttributesRetriever;

	public RolesBasedPreAuthenticatedWebAuthenticationDetailsSource() {
		super();
	}

	@Override
	protected Collection<String> getUserRoles(HttpServletRequest request) {
		j2eeMappableRoles = mappableAttributesRetriever.getMappableAttributes();
		Set<String> j2eeUserRolesList = new HashSet<String>();
        for (String role: j2eeMappableRoles) {
        	if (request.isUserInRole(role)) {
            	j2eeUserRolesList.add(role);
            }
        }
		String rolsStr = Arrays.toString(j2eeMappableRoles.toArray());
//		log.info("Rols de l'aplicació: " + rolsStr);

		rolsStr = Arrays.toString(j2eeUserRolesList.toArray());
//		log.info(String.format("Rols accessibles per l'usuari (%s): %s",
//				request.getUserPrincipal().getName(), rolsStr));
        return j2eeUserRolesList;
    }
	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
		this.mappableAttributesRetriever = mappableAttributesRetriever;
		this.j2eeMappableRoles = new HashSet<String>();
	}

}
