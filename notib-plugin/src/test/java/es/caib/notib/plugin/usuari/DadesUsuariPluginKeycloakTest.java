package es.caib.notib.plugin.usuari;

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
  public void setUp(){
    // Given
    String propertyKeyBase = "es.caib.notib.plugin.dades.usuari.";
    Properties properties = new Properties();
    // Local 7.3
    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl", "http://localhost:8080/auth/");
    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm", "GOIB");
    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id", "goib-ws");
    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication", "goib-ws");
    properties.put("es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret", "b6a5b8b4-0ac3-4379-872a-2de828aa9d29");
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

    keycloak = new DadesUsuariPluginKeycloak(propertyKeyBase, properties);
  }

  /**
   * Test case for {@link DadesUsuariPluginKeycloak#getUsernamesByRol} method.
   */
  @Test
  public void testGetUsernamesByRol(){

    // Given
    String rol = "NOT_APL";
    
    // When
    try{
        var usuaris = keycloak.getUsernamesByRol(rol);

        assertTrue(usuaris != null && usuaris.length > 0);
    } catch(Exception ex){
        // then
        fail("Exception in testGetUsernamesByRol");
    }
  }

  @AfterEach
  public void tearDown(){
    keycloak = null;
  }

}