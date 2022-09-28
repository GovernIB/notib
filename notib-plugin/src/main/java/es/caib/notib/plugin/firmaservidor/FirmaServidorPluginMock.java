/**
 * 
 */
package es.caib.notib.plugin.firmaservidor;

import es.caib.notib.plugin.SistemaExternException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementació del plugin de firma en servidor emprant PortaFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginMock implements FirmaServidorPlugin {

	public FirmaServidorPluginMock(Properties properties) {

	}

	@Override
	public byte[] firmar(
			String nom,
			String motiu,
			byte[] contingut,
			TipusFirma tipusFirma,
			String idioma) throws SistemaExternException {
		if (motiu != null && "e".equals(motiu)) {
			// Cas per provocar una excepció
			String errMsg = "Excepció provocada per paràmetre a SignaturaPluginMock";
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg);
			throw new SistemaExternException(errMsg);
		}
		// Retorna una firma falsa
		byte[] firmaContingut = null;
		try {
			firmaContingut = IOUtils.toByteArray(
					this.getClass().getResourceAsStream("/es/caib/ripea/plugin/firmaservidor/firma_document_mock.xml"));
		} catch (IOException e) {
			String errMsg = "Error llegint el fitxer mock de firma XAdES: " + e.getMessage();
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg, e);
			e.printStackTrace();
		}
		return firmaContingut;
	}

}
