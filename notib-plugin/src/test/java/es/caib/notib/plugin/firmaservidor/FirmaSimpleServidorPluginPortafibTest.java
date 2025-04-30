package es.caib.notib.plugin.firmaservidor;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FirmaSimpleServidorPluginPortafibTest {

    @Test
    void testGetEstatPluginReturnsUpWhenProfilesAreAvailable() throws Exception {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.endpoint", "https://dev.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.username", "$notib_portafib_dev");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.password", "notib_portafib_dev");
        FirmaSimpleServidorPluginPortafib plugin = new FirmaSimpleServidorPluginPortafib(properties, false);

        // Act
        EstatSalut estatSalut = plugin.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertTrue(estatSalut.getLatencia() >= 0);
    }

    @Test
    void testGetEstatPluginReturnsDownWhenExceptionOccurs() throws Exception {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.endpoint", "http://fakeUrl");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.username", "user");
        properties.setProperty("es.caib.notib.plugin.firmaservidor.portafib.auth.password", "pass");
        FirmaSimpleServidorPluginPortafib plugin = Mockito.spy(new FirmaSimpleServidorPluginPortafib(properties, false));

        // Act
        EstatSalut estatSalut = plugin.getEstatPlugin();

        // Assert
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
        assertNull(estatSalut.getLatencia());
    }

}