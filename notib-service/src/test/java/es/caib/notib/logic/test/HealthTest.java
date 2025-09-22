package es.caib.notib.logic.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.utils.CustomHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

public class HealthTest {

    private AplicacioService aplicacioService;
    private CustomHealthIndicator customHealthIndicator;

    @BeforeEach
    public void setUp() {

        aplicacioService = mock(AplicacioService.class);
        customHealthIndicator = new CustomHealthIndicator(aplicacioService);
    }

    @Test
    public void testHealthDown() {
        // Simulate the service being down
        when(aplicacioService.existeixUsuariNotib("test")).thenThrow(new RuntimeException("Service down"));

        Health health = customHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("Error"));
        assertEquals("Servei caigut", health.getDetails().get("Error"));
    }
}
