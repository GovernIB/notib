/**
 * 
 */
package es.caib.notib.plugin.usuari;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de LDAP. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.pluginib.dades.usuari.pluginsib.userinformation.ldap. :
 * 
 * - serverurl: Url del servidor de keycloak
 * - realm: Realm del keycloak.7
 * - client_id: Client ID del keycloak.
 * - client_id_for_user_autentication: Client ID per autenticació del keycloak.
 * - password_secret: Secret del client de keycloak.
 * - mapping.administrationID: Mapeig del administrationID de keycloak.
 * - debug: Activar el debug del plugin de keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginLdapCaib extends LdapUserInformationPlugin implements DadesUsuariPlugin {

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public DadesUsuariPluginLdapCaib(String propertyKeyBase, Properties properties) {

		super(propertyKeyBase, properties);
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.LDAP")));
	}

	public DadesUsuariPluginLdapCaib(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	public DadesUsuariPluginLdapCaib(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {
		super(propertyKeyBase, properties);
		this.configuracioEspecifica = configuracioEspecifica;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.LDAP")));
	}

	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {

		logger.info("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			var info = getRolesByUsername(usuariCodi);
			incrementarOperacioOk();
			return info != null && info.getRoles() != null ? List.of(info.getRoles()) : new ArrayList<>();
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}
	
	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException {

		logger.info("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			incrementarOperacioOk();
			return toDadesUsuari(userInfo);
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("Error al consultar l'usuari amb codi " + usuariCodi, ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {

		logger.info("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
			List<DadesUsuari> dadesUsuaris = new ArrayList<>();
			String [] codisUsuaris = this.getUsernamesByRol(grupCodi);
			incrementarOperacioOk();
			if (codisUsuaris == null) {
				return dadesUsuaris;
			}
			String codiUsuari;
			DadesUsuari dadaUsuari;
			for (var i = 0; i < codisUsuaris.length; i++) {
				codiUsuari= codisUsuaris[i];
				try {
					dadaUsuari = consultarAmbCodi(codiUsuari);
					if (dadaUsuari != null) {
						dadesUsuaris.add(dadaUsuari);
					}
				} catch(Exception e) {
					logger.error("Error consultant l'usuari amb codi \"" + codiUsuari + "\" en la conslta per rol \"" + grupCodi + "\".", e);
				}
			}
			return dadesUsuaris;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("Error al consultar els usuaris del grup LDAP CAIB " + grupCodi, ex);
		}
	}


	private DadesUsuari toDadesUsuari(UserInfo userInfo) {

		if (userInfo == null) {
			return null;
		}
		DadesUsuari dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(userInfo.getUsername());
		dadesUsuari.setNomSencer(userInfo.getFullName());
		dadesUsuari.setNom(userInfo.getName());
		dadesUsuari.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
		dadesUsuari.setNif(userInfo.getAdministrationID());
		dadesUsuari.setEmail(userInfo.getEmail());
//		dadesUsuari.setActiu(true);
		return dadesUsuari;
	}

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private boolean configuracioEspecifica = false;
	private int operacionsOk = 0;
	private int operacionsError = 0;

	@Synchronized
	private void incrementarOperacioOk() {
		operacionsOk++;
	}

	@Synchronized
	private void incrementarOperacioError() {
		operacionsError++;
	}

	@Synchronized
	private void resetComptadors() {
		operacionsOk = 0;
		operacionsError = 0;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return this.configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		try {
			Instant start = Instant.now();
			consultarAmbCodi("fakeUser");
			return EstatSalut.builder()
					.latencia((int) Duration.between(start, Instant.now()).toMillis())
					.estat(EstatSalutEnum.UP)
					.build();
		} catch (Exception ex) {
			return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
		}
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
				.totalOk(operacionsOk)
				.totalError(operacionsError)
				.build();
		resetComptadors();
		return integracioPeticions;
	}
}
