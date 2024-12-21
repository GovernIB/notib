package es.caib.notib.plugin.unitat;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class UnitatsOrganitzativesPluginDir3Test {

    @Test
    void testGetEstatPluginReturnsUp() {
        // Setup
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.unitats.dir3.url", "https://proves.caib.es/dir3caib");
        UnitatsOrganitzativesPluginDir3 plugin = new UnitatsOrganitzativesPluginDir3(properties, false);

        // Execute
        EstatSalut result = plugin.getEstatPlugin();

        // Assert
        assertNotNull(result);
        assertEquals(EstatSalutEnum.UP, result.getEstat());
        assertTrue(result.getLatencia() > 0);
    }

    @Test
    void testGetEstatPluginReturnsDown() {
        // Setup
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.unitats.dir3.url", "https://fakeUrl/dir3caib");
        UnitatsOrganitzativesPluginDir3 plugin = new UnitatsOrganitzativesPluginDir3(properties, false);

        // Execute
        EstatSalut result = plugin.getEstatPlugin();

        // Assert
        assertNotNull(result);
        assertEquals(EstatSalutEnum.DOWN, result.getEstat());
    }

}