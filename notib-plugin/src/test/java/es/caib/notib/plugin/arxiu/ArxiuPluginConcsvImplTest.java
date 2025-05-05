package es.caib.notib.plugin.arxiu;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class ArxiuPluginConcsvImplTest {

    @Test
    void testGetEstatPluginUp() {
        // Arrange
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.uuid", "https://dev.caib.es/concsvapi/interna/printable");
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.usuari", "$ripea_concsv");
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.contrasenya", "ripea_concsv");
        ArxiuPluginConcsvImpl plugin = new ArxiuPluginConcsvImpl("test", properties, true);

        // Act
        EstatSalut estatSalut = plugin.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertTrue(estatSalut.getLatencia() >= 0);
    }

    @Test
    void testGetEstatPluginDown() {
        // Arrange
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.uuid", "https://fakeUrl/concsvapi/interna/printable");
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.usuari", "$ripea_concsv");
        properties.setProperty("es.caib.notib.plugin.arxiu.caib.conversio.imprimible.contrasenya", "ripea_concsv");
        ArxiuPluginConcsvImpl plugin = new ArxiuPluginConcsvImpl("test", properties, true);

        // Act
        EstatSalut estatSalut = plugin.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
    }

}