package es.caib.notib.plugin.gesconadm;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class GestorContingutsAdministratiuPluginRolsacTest {

    @Test
    void testGetEstatPlugin_WhenProcedimentsAreAccessible_ReturnsEstatUp() {
        // Arrange
        Properties mockProperties = new Properties();
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.base.url", "https://dev.caib.es/rolsac");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.username", "$notib_rolsac");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.password", "notib_rolsac");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.basic.authentication", "true");
        GestorContingutsAdministratiuPluginRolsac plugin = new GestorContingutsAdministratiuPluginRolsac(mockProperties, false, "test");

        // Act
        EstatSalut result = plugin.getEstatPlugin();

        // Assert
        assertNotNull(result);
        assertEquals(EstatSalutEnum.UP, result.getEstat());
        assertTrue(result.getLatencia() > 0);
    }

    @Test
    void testGetEstatPlugin_WhenProcedimentsThrowException_ReturnsEstatDown() {
        // Arrange
        Properties mockProperties = new Properties();
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.rolsac.url", "https://dev.caib.es/rolsac");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.rolsac.usuari", "fake_user");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.rolsac.contrasenya", "notib_rolsac");
        mockProperties.setProperty("es.caib.notib.plugin.gesconadm.basic.authentication", "true");
        GestorContingutsAdministratiuPluginRolsac plugin = new GestorContingutsAdministratiuPluginRolsac(mockProperties, false,  null);

        // Act
        EstatSalut result = plugin.getEstatPlugin();

        // Assert
        assertNotNull(result);
        assertEquals(EstatSalutEnum.DOWN, result.getEstat());
    }
}