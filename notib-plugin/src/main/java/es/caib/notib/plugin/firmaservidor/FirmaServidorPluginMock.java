/**
 * 
 */
package es.caib.notib.plugin.firmaservidor;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FirmaServidorPluginMock implements FirmaServidorPlugin {

	public FirmaServidorPluginMock(Properties properties) {

	}

	@Override
	public byte[] firmar(String nom, String motiu, byte[] contingut, TipusFirma tipusFirma, String idioma) throws SistemaExternException {

		if ("e".equals(motiu)) {
			// Cas per provocar una excepció
			var errMsg = "Excepció provocada per paràmetre a SignaturaPluginMock";
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg);
			throw new SistemaExternException(errMsg);
		}
		// Retorna una firma falsa
		byte[] firmaContingut = null;
		try {
			firmaContingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/ripea/plugin/firmaservidor/firma_document_mock.xml"));
		} catch (IOException ex) {
			var errMsg = "Error llegint el fitxer mock de firma XAdES: " ;
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg, ex);
			log.error(errMsg, ex);
		}
		return firmaContingut;
	}

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean teConfiguracioEspecifica() {
		return false;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return EstatSalut.builder().estat(EstatSalutEnum.UP).latencia(1).build();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return IntegracioPeticions.builder().build();
	}

}
