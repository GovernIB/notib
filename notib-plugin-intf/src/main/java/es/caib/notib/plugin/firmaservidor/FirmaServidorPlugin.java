package es.caib.notib.plugin.firmaservidor;

import es.caib.notib.plugin.SalutPlugin;
import es.caib.notib.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents en servidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaServidorPlugin extends SalutPlugin {

	public byte[] firmar(String nom, String motiu, byte[] contingut, TipusFirma tipusFirma, String idioma) throws SistemaExternException;

	public static enum TipusFirma {
		PADES,
		CADES,
		XADES
	}

}
