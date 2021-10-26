package es.caib.notib.plugin.firmaservidor;

import es.caib.notib.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents des del servidor de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	public byte[] signar(
            String id,
            String nom,
            String motiu,
            String tipusFirma,
            byte[] contingut,
            String tipusDocumental) throws SistemaExternException;

}
