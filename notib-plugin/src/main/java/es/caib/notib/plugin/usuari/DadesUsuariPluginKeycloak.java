package es.caib.notib.plugin.usuari;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginKeycloak extends KeyCloakUserInformationPlugin implements DadesUsuariPlugin, IUserInformationPlugin {

	private boolean mostrarLogs = false;
	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties) {

		super(propertyKeyBase, properties);
		mostrarLogs = Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.KEYCLOAK"));
	}

	public void logInfo(String msg) {

		try {
			if (mostrarLogs) {
				log.info(msg);
			}
		} catch (Exception ex) {
			log.error("Error creant el log ", ex);
		}
	}

	public void logError(String msg, Exception exception) {

		try {
			if (mostrarLogs) {
				log.error(msg, exception);
			}
		} catch (Exception ex) {
			log.error("Error creant el log ", ex);
		}
	}


	public DadesUsuariPluginKeycloak(String propertyKeyBase) {
		super(propertyKeyBase);
	}


	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			var rolesInfo = getRolesByUsername(usuariCodi);
			return rolesInfo != null && rolesInfo.getRoles() != null ? new ArrayList<>(Arrays.asList(rolesInfo.getRoles())) : new ArrayList<>();
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}
	
	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			var userInfo = getUserInfoByUserName(usuariCodi);
			if (userInfo == null) {
				return null;
			}
			return DadesUsuari.builder().codi(userInfo.getUsername()).nom(userInfo.getName()).llinatges(userInfo.getSurname1())
					.nif(userInfo.getAdministrationID()).email(userInfo.getEmail()).build();
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar les dades de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {
		
		log.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			var usuariCodis = getUsernamesByRol(grupCodi);
//			var usuariCodis = getUsuarisByRol(grupCodi);
			if (usuariCodis == null || usuariCodis.length == 0) {
				return new ArrayList<>();
			}
			return Arrays.stream(usuariCodis).map(u -> DadesUsuari.builder().codi(u).build()).collect(Collectors.toList());
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar les dades dels usuaris amb grup (grup=" + grupCodi + ")", ex);
		}
	}

	private String[] getUsuarisByRol(String rol) throws Exception {
		Keycloak keycloak = this.getKeyCloakConnection();
		var rrs = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).roles();
		try {
			Set<String> users = new HashSet();
			RoleResource rr = rrs.get(rol);
			Set<UserRepresentation> userRep = rr.getRoleUserMembers();
			Iterator var11 = userRep.iterator();

			while(var11.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var11.next();
				users.add(ur.getUsername());
			}
			return !users.isEmpty() ? users.toArray(new String[users.size()]) : null;
		} catch (NotFoundException var13) {
			return null;
		}
	}

	@Override
	public String[] getUsernamesByRol(String rol) throws Exception {

		logInfo("fooooooooooooooooo");
		Set<String> usernamesClientApp = null;
		Set<String> usernamesClientPersons = null;
		Set<String> usersRealm = null;
		try {
			String appClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id");
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
			logInfo("[Keycloak] Usuaris pel rol " + rol + " amb el client d'aplicacio " + appClient + " : " + usernamesClientApp);
		} catch (Exception ex) {
			logError("No s'han obtingut usuaris per client d'aplicació", ex);
//			log.error("No s'han obtingut usuaris per client d'aplicació", ex);
		}
		try {
			String personsClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id_for_user_autentication");
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
			logInfo("[Keycloak] Usuaris pel rol " + rol + " amb el client de persones " + personsClient + " : " + usernamesClientPersons);
		} catch (Exception ex) {
			logError("No s'han obtingut usuaris per client de persones", ex);
//			log.error("No s'han obtingut usuaris per client de persones", ex);
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
			logInfo("[Keycloak] Usuaris del realm pel rol " + rol + " : " + usersRealm);
		} catch (Exception ex) {
			log.error("No s'han obtingut usuaris per realm", ex);
		}
		if (usernamesClientApp == null && usernamesClientPersons == null && usersRealm == null) {
			return null;
		}
		Set<String> users = new TreeSet();
		if (usernamesClientApp != null) {
			users.addAll(usernamesClientApp);
		}

		if (usernamesClientPersons != null) {
			users.addAll(usernamesClientPersons);
		}

		if (usersRealm != null) {
			users.addAll(usersRealm);
		}

		return users.toArray(new String[users.size()]);
	}

	private Set<String> getUsernamesByRolOfRealm(String rol) throws Exception {

		RolesResource roleres = this.getKeyCloakConnectionForRoles();
		try {
			Set<UserRepresentation> userRep = roleres.get(rol).getRoleUserMembers();
			Set<String> users = new HashSet();
			Iterator var5 = userRep.iterator();

			while(var5.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var5.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (NotFoundException var7) {
			return null;
		}
	}

	private Set<String> getUsernamesByRolOfClient(String rol, String client) throws Exception {

		Keycloak keycloak = this.getKeyCloakConnection();
		ClientsResource clientsApi = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).clients();
		List<ClientRepresentation> crList = clientsApi.findByClientId(client);
		if (crList == null || crList.isEmpty()) {
			return null;
		}
		ClientResource c = clientsApi.get((crList.get(0)).getId());
		RolesResource rrs = c.roles();

		try {
			Set<String> users = new HashSet();
			RoleResource rr = rrs.get(rol);
			Set<UserRepresentation> userRep = rr.getRoleUserMembers();
			Iterator var11 = userRep.iterator();

			while(var11.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var11.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (NotFoundException var13) {
			return null;
		}
	}

}
