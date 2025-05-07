package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import es.caib.notib.client.domini.EnviamentTipus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExplotFets entity.
 * Tests the default value behavior of the usuariCodi field.
 */
public class ExplotFetsTest {

    /**
     * Test that verifies when usuariCodi is null, it defaults to "DESCONEGUT".
     */
    @Test
    public void testUsuariCodiDefaultValue() {
        // Create an ExplotFets instance with usuariCodi = "DESCONEGUT" (using constructor)
        // Note: When using constructor directly, we need to provide the default value explicitly
        ExplotFets explotFets = new ExplotFets(
                1L,                         // entitatId
                2L,                         // procedimentId
                "ORG001",                   // organCodi
                null,                       // usuariCodi (default value)
                EnviamentTipus.NOTIFICACIO, // tipus
                EnviamentOrigen.WEB,        // origen
                0,                          // pendent
                0,                          // regEnviamentError
                0,                          // registrada
                0,                          // regAcceptada
                0,                          // regRebutjada
                0,                          // notEnviamentError
                0,                          // notEnviada
                0,                          // notNotificada
                0,                          // notRebutjada
                0,                          // notExpirada
                0,                          // cieEnviamentError
                0,                          // cieEnviada
                0,                          // cieNotificada
                0,                          // cieRebutjada
                0,                          // cieError
                0                           // processada
        );

        // Verify that usuariCodi has the default value "DESCONEGUT"
        assertEquals("DESCONEGUT", explotFets.getUsuariCodi());
    }

    /**
     * Test that verifies when usuariCodi is provided, the provided value is used.
     */
    @Test
    public void testUsuariCodiProvidedValue() {
        // Create an ExplotFets instance with a specific usuariCodi value
        String expectedUsuariCodi = "USER123";
        ExplotFets explotFets = new ExplotFets(
                1L,                         // entitatId
                2L,                         // procedimentId
                "ORG001",                   // organCodi
                expectedUsuariCodi,         // usuariCodi (provided value)
                EnviamentTipus.NOTIFICACIO, // tipus
                EnviamentOrigen.WEB,        // origen
                0,                          // pendent
                0,                          // regEnviamentError
                0,                          // registrada
                0,                          // regAcceptada
                0,                          // regRebutjada
                0,                          // notEnviamentError
                0,                          // notEnviada
                0,                          // notNotificada
                0,                          // notRebutjada
                0,                          // notExpirada
                0,                          // cieEnviamentError
                0,                          // cieEnviada
                0,                          // cieNotificada
                0,                          // cieRebutjada
                0,                          // cieError
                0                           // processada
        );

        // Verify that usuariCodi has the provided value
        assertEquals(expectedUsuariCodi, explotFets.getUsuariCodi());
    }
}
