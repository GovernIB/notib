package es.caib.notib.plugin.usuari;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.SistemaExternException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginJdbc implements DadesUsuariPlugin {

	private final Properties properties;
//	public DadesUsuariPluginJdbc(Properties properties) {
//		this.properties = properties;
//	}
	public DadesUsuariPluginJdbc(Properties properties, boolean configuracioEspecifica) {
		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
	}


	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			var result = consultaRolsUsuariUnic(getLdapFiltreRolsCodi(), "codi", usuariCodi);
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}
	
	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			var result = consultaDadesUsuariUnic(getJdbcQueryUsuariCodi(), "codi", usuariCodi);
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw ex;
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {

		log.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			var result = consultaDadesUsuari(getJdbcQueryUsuariGrup(), "grup", grupCodi);
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw ex;
		}
	}


	private List<String> consultaRolsUsuariUnic(String sqlQuery, String paramName, String paramValue) throws SistemaExternException {

		List<String> rols = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		try {
			var ds = getDataSource();
			con = ds.getConnection();
			if (sqlQuery.contains("?")) {
				ps = con.prepareStatement(sqlQuery);
				ps.setString(1, paramValue);
			} else if (sqlQuery.contains(":" + paramName)) {
				ps = con.prepareStatement(sqlQuery.replace(":" + paramName, "'" + paramValue + "'"));
			} else {
				ps = con.prepareStatement(sqlQuery);
			}
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					rols.add(rs.getString(1));
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (Exception ex) {
				log.error("Error al tancar el PreparedStatement", ex);
			}
			try {
				if (con != null) con.close();
			} catch (Exception ex) {
				log.error("Error al tancar la connexió", ex);
			}
		}
		return rols;
	}

	private DadesUsuari consultaDadesUsuariUnic(String sqlQuery, String paramName, String paramValue) throws SistemaExternException {

		var llista = consultaDadesUsuari(sqlQuery, paramName, paramValue);
		return !llista.isEmpty() ? llista.get(0) : null;
	}

	private List<DadesUsuari> consultaDadesUsuari(String sqlQuery, String paramName, String paramValue) throws SistemaExternException {

		List<DadesUsuari> llistaUsuaris = new ArrayList<>();
		PreparedStatement ps = null;
		try {
			var ds = getDataSource();
			try (var con = ds.getConnection()) {
				if (sqlQuery.contains("?")) {
					ps = con.prepareStatement(sqlQuery);
					ps.setString(1, paramValue);
				} else if (sqlQuery.contains(":" + paramName)) {
					ps = con.prepareStatement(sqlQuery.replace(":" + paramName, "'" + paramValue + "'"));
				} else {
					ps = con.prepareStatement(sqlQuery);
				}
				try (var rs = ps.executeQuery()) {
					while (rs.next()) {
						DadesUsuari dadesUsuari = new DadesUsuari();
						dadesUsuari.setCodi(rs.getString(1));
						dadesUsuari.setNom(rs.getString(2));
						dadesUsuari.setNif(rs.getString(3));
						dadesUsuari.setEmail(rs.getString(4));
						llistaUsuaris.add(dadesUsuari);
					}
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ex) {
				log.error("Error al tancar el PreparedStatement", ex);
			}
		}
		return llistaUsuaris;
	}

	protected DataSource getDataSource() throws NamingException {
		var initContext = new InitialContext();
		var ds = (DataSource)initContext.lookup(getDatasourceJndiName());
		return ds;
	}

	private String getDatasourceJndiName() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.jdbc.datasource.jndi.name");
	}
	
	private String getJdbcQueryUsuariCodi() {
		String query = properties.getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query");
		if (query == null || query.isEmpty())
			query = properties.getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query.codi");
		return query;
	}
	
	private String getLdapFiltreRolsCodi() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(cn=XXX))
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query.rols");
	}
	
	private String getJdbcQueryUsuariGrup() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query.grup");
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
