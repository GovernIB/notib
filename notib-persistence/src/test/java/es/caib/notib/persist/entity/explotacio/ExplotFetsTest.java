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
                0L,                          // pendent
                0L,                          // regEnviamentError
                0L,                          // registrada
                0L,                          // regAcceptada
                0L,                          // regRebutjada
                0L,                          // notEnviamentError
                0L,                          // notEnviada
                0L,                          // notNotificada
                0L,                          // notRebutjada
                0L,                          // notExpirada
                0L,                          // cieEnviamentError
                0L,                          // cieEnviada
                0L,                          // cieNotificada
                0L,                          // cieRebutjada
                0L,                          // cieError
                0L                           // processada
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
                0L,                          // pendent
                0L,                          // regEnviamentError
                0L,                          // registrada
                0L,                          // regAcceptada
                0L,                          // regRebutjada
                0L,                          // notEnviamentError
                0L,                          // notEnviada
                0L,                          // notNotificada
                0L,                          // notRebutjada
                0L,                          // notExpirada
                0L,                          // cieEnviamentError
                0L,                          // cieEnviada
                0L,                          // cieNotificada
                0L,                          // cieRebutjada
                0L,                          // cieError
                0L                           // processada
        );

        // Verify that usuariCodi has the provided value
        assertEquals(expectedUsuariCodi, explotFets.getUsuariCodi());
    }
}
