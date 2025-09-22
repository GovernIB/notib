package es.caib.notib.plugin.firmaservidor;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
class FirmaServidorPluginPortafibTest {

    @Test
    void testGetEstatPlugin_StatusUp() throws Exception {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url", "https://proves.caib.es/portafib/ws/v1/PortaFIBPassarelaDeFirmaEnServidor");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username", "$notib_portafib_dev");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password", "notib_portafib_dev");
        FirmaServidorPluginPortafib firmaServidorPlugin = new FirmaServidorPluginPortafib(properties, false, null);

        // Act
        EstatSalut estatSalut = firmaServidorPlugin.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertNotNull(estatSalut.getLatencia());
    }

    @Test
    void testGetEstatPlugin_StatusDownOnException() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.endpoint", "http://fakeUrl");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.username", "user");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.password", "pass");
        FirmaServidorPluginPortafib firmaServidorPlugin = new FirmaServidorPluginPortafib(properties, false, null);

        // Act
        EstatSalut estatSalut = firmaServidorPlugin.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
    }
}