package es.caib.notib.plugin.digitalitzacio;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioEstat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
//public class DigitalitzacioPluginMock extends RipeaAbstractPluginProperties implements DigitalitzacioPlugin {
public class DigitalitzacioPluginMock extends AbstractSalutPlugin implements DigitalitzacioPlugin {


    private final Properties properties;
    private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public DigitalitzacioPluginMock(Properties properties) {
	    this.properties = properties;
    }

	public DigitalitzacioPluginMock(Properties properties, boolean configuracioEspecifica) {

        this.properties = properties;
        salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.DIGITALITZACIO")));
	}

	@Override
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(String idioma) throws SistemaExternException {

		List<DigitalitzacioPerfil> perfilsDisponibles = new ArrayList<>();
		var dp1 = new DigitalitzacioPerfil();
		dp1.setCodi("DP1");dp1.setNom("Perfil digitalització 1");dp1.setDescripcio("Descripció del perfil digitalització 1");dp1.setTipus(1);
		var dp2 = new DigitalitzacioPerfil();
		dp2.setCodi("DP2");dp2.setNom("Perfil digitalització 2");dp2.setDescripcio("Descripció del perfil digitalització 2");dp2.setTipus(2);
		var dp3 = new DigitalitzacioPerfil();
		dp3.setCodi("DP3");dp3.setNom("Perfil digitalització 3");dp3.setDescripcio("Descripció del perfil digitalització 3");dp3.setTipus(3);
		var dp4 = new DigitalitzacioPerfil();
		dp4.setCodi("DP4");dp4.setNom("Perfil digitalització 4");dp4.setDescripcio("Descripció del perfil digitalització 4");dp4.setTipus(4);
		var dp5 = new DigitalitzacioPerfil();
		dp5.setCodi("DP5");dp5.setNom("Perfil digitalització 5");dp5.setDescripcio("Descripció del perfil digitalització 5");dp5.setTipus(5);
		perfilsDisponibles.add(dp1);
		perfilsDisponibles.add(dp2);
		perfilsDisponibles.add(dp3);
		perfilsDisponibles.add(dp4);
		perfilsDisponibles.add(dp5);
		return perfilsDisponibles;
	}

	@Override
	public DigitalitzacioTransaccioResposta iniciarProces(String codiPerfil, String idioma, UsuariDto funcionari, String returnUrl) throws SistemaExternException {

		var resposta = new DigitalitzacioTransaccioResposta();
		var idTransaccio = String.valueOf(System.currentTimeMillis());
		resposta.setIdTransaccio(idTransaccio);
		resposta.setReturnScannedFile(true);
		resposta.setReturnSignedFile(false);
		var partes = returnUrl.split("/");
		var ultimaParte = partes[partes.length - 1];
		resposta.setUrlRedireccio("http://localhost:8080/ripeaback/modal/digitalitzacio/mock?idExpedient="+ultimaParte+"&idTransaccio="+idTransaccio);
		return resposta;
	}

	@Override
	public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) throws SistemaExternException {

		var resultat = new DigitalitzacioResultat();
		resultat.setError(false);
		resultat.setEstat(DigitalitzacioEstat.FINAL_OK);
		resultat.setMimeType("text/plain");
		resultat.setContingut("Hola, simulo ser un fichero escaneado.".getBytes());
		resultat.setIdioma("es");
		resultat.setNomDocument("ScannedFile.txt");
		resultat.setResolucion(600);
		return resultat;
	}

	@Override
	public void tancarTransaccio(String idTransaccio) throws SistemaExternException {}


    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin, String codiEntiat) {
        salutPluginComponent.init(registry, codiPlugin, codiEntiat);
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }
}