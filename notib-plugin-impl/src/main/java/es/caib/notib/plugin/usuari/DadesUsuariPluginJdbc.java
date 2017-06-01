/**
 * 
 */
package es.caib.notib.plugin.usuari;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginJdbc implements DadesUsuariPlugin {

	@Override
	public DadesUsuari consultarAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		return consultaDadesUsuariUnic(
				getJdbcQueryUsuariCodi(),
				"codi",
				usuariCodi);
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		return consultaDadesUsuari(
				getJdbcQueryUsuariGrup(),
				"grup",
				grupCodi);
	}



	private DadesUsuari consultaDadesUsuariUnic(
			String sqlQuery,
			String paramName,
			String paramValue) throws SistemaExternException {
		List<DadesUsuari> llista = consultaDadesUsuari(
				sqlQuery,
				paramName,
				paramValue);
		if (llista.size() > 0)
			return llista.get(0);
		else
			return null;
	}

	private List<DadesUsuari> consultaDadesUsuari(
			String sqlQuery,
			String paramName,
			String paramValue) throws SistemaExternException {
		List<DadesUsuari> llistaUsuaris = new ArrayList<DadesUsuari>();
		Connection con = null;
		PreparedStatement ps = null;
		try {
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(getDatasourceJndiName());
			con = ds.getConnection();
			if (sqlQuery.contains("?")) {
				ps = con.prepareStatement(sqlQuery);
				ps.setString(1, paramValue);
			} else if (sqlQuery.contains(":" + paramName)) {
				ps = con.prepareStatement(
						sqlQuery.replace(":" + paramName, "'" + paramValue + "'"));
			} else {
				ps = con.prepareStatement(sqlQuery);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DadesUsuari dadesUsuari = new DadesUsuari();
				dadesUsuari.setCodi(rs.getString(1));
				dadesUsuari.setNomSencer(rs.getString(2));
				dadesUsuari.setEmail(rs.getString(3));
				llistaUsuaris.add(dadesUsuari);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar el PreparedStatement", ex);
			}
			try {
				if (con != null) con.close();
			} catch (Exception ex) {
				LOGGER.error("Error al tancar la connexió", ex);
			}
		}
		return llistaUsuaris;
	}

	private String getDatasourceJndiName() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.jdbc.datasource.jndi.name");
	}
	private String getJdbcQueryUsuariCodi() {
		String query = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query");
		if (query == null || query.isEmpty())
			query = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query.codi");
		return query;
	}
	private String getJdbcQueryUsuariGrup() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.jdbc.query.grup");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginJdbc.class);

}
