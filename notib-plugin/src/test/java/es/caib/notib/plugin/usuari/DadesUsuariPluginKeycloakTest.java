package es.caib.notib.plugin.usuari;

import es.caib.comanda.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test case for {@link DadesUsuariPluginKeycloak} class.
 */
@Disabled
public class DadesUsuariPluginKeycloakTest {

    private DadesUsuariPluginKeycloak keycloak;

    @BeforeEach
    public void setUp() {
        // Given
        String propertyKeyBase = "es.caib.notib.plugin.dades.usuari.";
        Properties properties = new Properties();
        // Local 7.3
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl", "http://localhost:8081/auth/");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm", "GOIB");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id", "goib-ws");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication", "goib-ws");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret", "d5c15941-64f1-4097-9da1-3d447fa37ab7");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID", "NIF");
        properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug", "true");
        // PRE
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl", "https://loginpre.caib.es/auth/");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm", "webpre");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id", "goib-ws");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication", "goib-ws");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret", "********-****-****-****-************");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID", "nif");
//    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug", "true");

        keycloak = new DadesUsuariPluginKeycloak(propertyKeyBase, properties, false);
    }

    /**
     * Test case for {@link DadesUsuariPluginKeycloak#getUsernamesByRol} method.
     */
    @Test
    public void testGetUsernamesByRol() {

        // Given
        String rol = "NOT_APL";

        // When
        try {
            var usuaris = keycloak.getUsernamesByRol(rol);

            assertTrue(usuaris != null && usuaris.length > 0);
        } catch (Exception ex) {
            // then
            fail("Exception in testGetUsernamesByRol");
        }
    }

    /**
     * Test case for {@link DadesUsuariPluginKeycloak#getEstatPlugin} method when the plugin is operational.
     */
    @Test
    public void testGetEstatPluginUp() {

        try {
            // When
            var estat = keycloak.getEstatPlugin();
            // Then
            assertTrue(estat != null && estat.getEstat() == EstatSalutEnum.UP);
        } catch (Exception ex) {
            fail("Exception in testGetEstatPlugin");
        }
    }

    /**
     * Test case for {@link DadesUsuariPluginKeycloak#getEstatPlugin} method when the plugin is not operational.
     */
    @Test
    public void testGetEstatPluginDown() {

        // Simulate an error scenario
        keycloak = new DadesUsuariPluginKeycloak("invalidPropertyKeyBase", new Properties(), false);

        try {
            // When
            var estat = keycloak.getEstatPlugin();
            // Then
            assertTrue(estat != null && estat.getEstat() == EstatSalutEnum.DOWN);
        } catch (Exception ex) {
            fail("Exception in testGetEstatPlugin_Error");
        }
    }

    @AfterEach
    public void tearDown() {
        keycloak = null;
    }

}