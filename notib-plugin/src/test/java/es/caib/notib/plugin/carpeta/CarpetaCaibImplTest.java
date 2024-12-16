package es.caib.notib.plugin.carpeta;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        CarpetaCaibImpl carpetaCaibImpl = new CarpetaCaibImpl(properties, true);
        carpetaCaibImpl.client = mockClient;

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
        // Arrange
        Properties properties = mock(Properties.class);
        Client mockClient = mock(Client.class);
        WebResource mockWebResource = mock(WebResource.class);
        ClientResponse mockResponse = mock(ClientResponse.class);

        when(properties.getProperty("es.caib.notib.plugin.carpeta.url")).thenReturn("http://test-url.com");
        when(mockClient.resource("http://test-url.com")).thenReturn(mockWebResource);
        when(mockWebResource.queryParams(any(MultivaluedMap.class))).thenReturn(mockWebResource);
        when(mockWebResource.type(MediaType.APPLICATION_JSON_TYPE)).thenReturn(mockWebResource);
        when(mockWebResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(mockWebResource);
        when(mockWebResource.get(ClientResponse.class)).thenThrow(new RuntimeException("Error"));

        CarpetaCaibImpl carpetaCaibImpl = new CarpetaCaibImpl(properties, true);
        carpetaCaibImpl.client = mockClient;

        // Act
        EstatSalut estatSalut = carpetaCaibImpl.getEstatPlugin();

        // Assert
        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
        assertEquals(0, estatSalut.getLatencia());
    }
}