package es.caib.notib.plugin.registre;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class RegistrePluginRegweb3ImplTest {

    @Test
    public void testGetEstatPluginReturnsUpWhenServiceIsAvailable() throws Exception {
        // Arrange
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.registre.url", "https://dev.caib.es/regweb3/ws/v3");
        properties.setProperty("es.caib.notib.plugin.registre.usuari", "$notib_regweb");
        properties.setProperty("es.caib.notib.plugin.registre.contrasenya", "notib_regweb");
        properties.setProperty("es.caib.notib.plugin.registre.namespaceuri", "urn:es:caib:regweb:ws:v1:services");
        properties.setProperty("es.caib.notib.plugin.registre.service.name", "RegwebFacadeService");
        properties.setProperty("es.caib.notib.plugin.registre.port.name", "RegwebFacade");
        RegistrePluginRegweb3Impl registrePlugin = new RegistrePluginRegweb3Impl(properties, "A04003003", false);

        // Invoke the method
        EstatSalut estatSalut = registrePlugin.getEstatPlugin();

        // Verify and assert the result
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertTrue(estatSalut.getLatencia() >= 0);
    }

    @Test
    public void testGetEstatPluginReturnsDownWhenExceptionIsThrown() throws Exception {
        // Arrange
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.registre.url", "https://fakeUrl/regweb3/ws/v3");
        properties.setProperty("es.caib.notib.plugin.registre.usuari", "$notib_regweb");
        properties.setProperty("es.caib.notib.plugin.registre.contrasenya", "notib_regweb");
        RegistrePluginRegweb3Impl registrePlugin = new RegistrePluginRegweb3Impl(properties, "A04003003", false);

        // Invoke the method
        EstatSalut result = registrePlugin.getEstatPlugin();

        // Verify and assert the result
        assertEquals(EstatSalutEnum.DOWN, result.getEstat());
        assertNull(result.getLatencia());
    }

}