/**
 * 
 */
package es.caib.notib.plugin.usuari;

import es.caib.notib.plugin.SistemaExternException;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Implementació de test del plugin de consulta de dades d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginMock implements DadesUsuariPlugin {

	private final Properties properties;

	public DadesUsuariPluginMock(Properties properties) {
		this.properties = properties;
	}

	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) {

		var dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(usuariCodi);
		dadesUsuari.setNomSencer(usuariCodi);
		dadesUsuari.setEmail(usuariCodi + "@aqui.es");
		return dadesUsuari;
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}

	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {
		return Arrays.asList("NOT_SUPER", "NOT_ADMIN", "NOT_CARPETA", "NOT_APL", "tothom");
	}

}
