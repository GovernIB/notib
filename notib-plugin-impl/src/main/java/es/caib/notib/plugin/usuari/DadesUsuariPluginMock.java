/**
 * 
 */
package es.caib.notib.plugin.usuari;

import java.util.List;

import es.caib.notib.plugin.SistemaExternException;

/**
 * Implementació de test del plugin de consulta de dades d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginMock implements DadesUsuariPlugin {

	@Override
	public DadesUsuari consultarAmbCodi(
			String usuariCodi) {
		DadesUsuari dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(usuariCodi);
		dadesUsuari.setNomSencer(usuariCodi);
		dadesUsuari.setEmail(usuariCodi + "@aqui.es");
		return dadesUsuari;
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(
			String grupCodi) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}

	@Override
	public List<String> consultarRolsAmbCodi(
			String usuariCodi) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}

}
