package es.caib.notib.plugin.imprimible;

import java.util.Map;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.plugins.arxiu.api.Document;

public interface ImprimiblePlugin {
	
	/**
	 * Obté el document enviat prèviament al gestor d'arxius remot
	 * 
	 * @param arxiuUuid
	 *            identificador UUID del document creat o enviat previament
	 * @param versio
	 *            versió del document a obtenir
	 * @param ambContingut
	 *            indica si es vol obtenir el contingut (byte[]) del document en qüestió
	 * @param ambVersioImprimible
	 *            indica si es vol obtenir la versió PDF imprimible del document sol·licitat
	 * @return Document sol·licitat
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public Document documentDescarregar(
			String identificador,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException;
	
	public static interface IntegracioManager {
		public void addAccioOk(
				String integracioCodi,
				String descripcio,
				Map<String, String> parametres,
				long tempsResposta);
		public void addAccioError(
				String integracioCodi,
				String descripcio,
				Map<String, String> parametres,
				long tempsResposta,
				String errorDescripcio,
				Throwable throwable);
	}
}
