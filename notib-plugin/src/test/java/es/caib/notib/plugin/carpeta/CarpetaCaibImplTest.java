package es.caib.notib.plugin.carpeta;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class CarpetaCaibImplTest {

    /**
     * Tests the getEstatPlugin method of the CarpetaCaibImpl class.
     * Use case: The plugin is able to perform the `existeixNif` operation successfully.
     */
    @Test
    void testGetEstatPlugin_WhenPluginIsUp() {
        // Arrange
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.carpeta.url", "https://proves.caib.es/carpetaapi/interna");
        properties.setProperty("es.caib.notib.plugin.carpeta.usuari", "$notib_carpeta");
        properties.setProperty("es.caib.notib.plugin.carpeta.contrasenya", "notib_carpeta");
        CarpetaCaibImpl carpetaCaibImpl = new CarpetaCaibImpl(properties, false, null);

        // Act
        EstatSalut estatSalut = carpetaCaibImpl.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertTrue(estatSalut.getLatencia() > 0);
    }

    /**
     * Tests the getEstatPlugin method of the CarpetaCaibImpl class.
     * Use case: The plugin fails to perform the `existeixNif` operation.
     */
    @Test
    void testGetEstatPlugin_WhenPluginIsDown() {
        var properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.carpeta.url", "https://fakeUrl/carpetaapi/interna");
        properties.setProperty("es.caib.notib.plugin.carpeta.usuari", "$notib_carpeta");
        properties.setProperty("es.caib.notib.plugin.carpeta.contrasenya", "notib_carpeta");
        CarpetaCaibImpl carpetaCaibImpl = new CarpetaCaibImpl(properties, false, null);

        // Act
        EstatSalut estatSalut = carpetaCaibImpl.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
    }
}