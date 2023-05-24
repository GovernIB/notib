package es.caib.notib.plugin.usuari;

import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginKeycloak extends KeyCloakUserInformationPlugin implements DadesUsuariPlugin, IUserInformationPlugin {


	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
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
//			var llinatges = userInfo.getSurname1();
//			if (!Strings.isNullOrEmpty(userInfo.getSurname2())) {
//				llinatges += " " + userInfo.getSurname2();
//			}
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
			var roleres = this.getKeyCloakConnectionForRoles();
			var userRep = roleres.get(grupCodi).getRoleUserMembers();
			var usuariCodis = getUsernamesByRol(grupCodi);
			if (usuariCodis == null || usuariCodis.length == 0) {
				return new ArrayList<>();
			}
			return Arrays.stream(usuariCodis).map(u -> DadesUsuari.builder().codi(u).build()).collect(Collectors.toList());
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar les dades dels usuaris amb grup (grup=" + grupCodi + ")", ex);
		}
	}

}
