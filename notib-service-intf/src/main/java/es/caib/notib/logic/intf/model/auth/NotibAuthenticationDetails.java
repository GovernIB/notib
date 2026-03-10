package es.caib.notib.logic.intf.model.auth;

/**
 * Interfície amb els detalls de l'autenticació.
 *
 * @author Límit Tecnologies
 */
public interface NotibAuthenticationDetails {

	String getJwtToken();
	String getPreferredUsername();
	String getName();
	String getEmail();
	String getNif();
	String[] getOriginalRoles();

}
