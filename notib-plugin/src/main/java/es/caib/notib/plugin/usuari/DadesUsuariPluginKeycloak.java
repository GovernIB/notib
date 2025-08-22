package es.caib.notib.plugin.usuari;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import io.micrometer.core.instrument.MeterRegistry;
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


	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {
		super(propertyKeyBase, properties);
		salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.KEYCLOAK")));
	}


	public DadesUsuariPluginKeycloak(String propertyKeyBase) {
		super(propertyKeyBase);
	}


	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {

		logger.info("[Keycloak] Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			var rolesInfo = getRolesByUsername(usuariCodi);
			logger.info("[Keycloak] Rols de l'usuari" + usuariCodi + " " + rolesInfo);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return rolesInfo != null && rolesInfo.getRoles() != null ? new ArrayList<>(Arrays.asList(rolesInfo.getRoles())) : new ArrayList<>();
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException {

		logger.info("[Keycloak] Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			var userInfo = getUserInfoByUserName(usuariCodi);
			logger.info("[Keycloak] Dades de l'usuari" + usuariCodi + " " + userInfo);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			if (userInfo == null) {
				return null;
			}
			return DadesUsuari.builder().codi(userInfo.getUsername()).nom(userInfo.getName()).llinatges(userInfo.getSurname1())
					.nif(userInfo.getAdministrationID()).email(userInfo.getEmail()).build();
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			throw new SistemaExternException("Error al consultar les dades de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {
		
		logger.info("[Keycloak] Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
            long startTime = System.currentTimeMillis();
			var usuariCodis = getUsernamesByRol(grupCodi);
			logger.info("[Keycloak] Usuaris del grup " + grupCodi + " " + Arrays.toString(usuariCodis));
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
			if (usuariCodis == null || usuariCodis.length == 0) {
				return new ArrayList<>();
			}
			return Arrays.stream(usuariCodis).map(u -> DadesUsuari.builder().codi(u).build()).collect(Collectors.toList());
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			throw new SistemaExternException("[Keycloak] Error al consultar les dades dels usuaris amb grup (grup=" + grupCodi + ")", ex);
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

		Set<String> usernamesClientApp = null;
		Set<String> usernamesClientPersons = null;
		Set<String> usersRealm = null;
		int numExcepcions = 0;

        long startTime = System.currentTimeMillis();
		try {
			String appClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id");
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
			logger.info("[Keycloak] Usuaris pel rol " + rol + " amb el client d'aplicacio " + appClient + " : " + usernamesClientApp);
		} catch (Exception ex) {
			numExcepcions++;
			logger.error("[Keycloak] No s'han obtingut usuaris per client d'aplicació amb el rol " + rol, ex);
		}
		try {
			String personsClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id_for_user_autentication");
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
			logger.info("[Keycloak] Usuaris pel rol " + rol + " amb el client de persones " + personsClient + " : " + usernamesClientPersons);
		} catch (Exception ex) {
			numExcepcions++;
			logger.error("No s'han obtingut usuaris per client de persones amb el rol " + rol, ex);
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
			logger.info("[Keycloak] Usuaris del realm pel rol " + rol + " : " + usersRealm);
		} catch (Exception ex) {
			numExcepcions++;
			log.error("[Keycloak] No s'han obtingut usuaris per realm", ex);
		}

        if (numExcepcions > 0) {
            salutPluginComponent.incrementarOperacioError();
        } else {
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
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


    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }

}
