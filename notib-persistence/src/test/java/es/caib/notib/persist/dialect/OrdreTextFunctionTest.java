package es.caib.notib.persist.dialect;

import org.hibernate.dialect.function.SQLFunction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * La funció ordre_text dels dialectes ha de generar exactament l'expressió dels índexs d'ordenació de
 * not_notificacio_table (changelog 2_1_1_000, propietats ordreTextInici/ordreTextFi): si no, Oracle no els fa servir.
 */
class OrdreTextFunctionTest {

	@Test
	void oracleShouldRenderNlssortGenericM() {
		SQLFunction funcio = new OracleCaibDialect().getFunctions().get(OracleCaibDialect.FUNCIO_ORDRE_TEXT);
		assertNotNull(funcio);
		assertEquals("NLSSORT(t.concepte, 'NLS_SORT=GENERIC_M')", funcio.render(null, List.of("t.concepte"), null));
	}

	@Test
	void postgresShouldRenderColumn() {
		SQLFunction funcio = new PostgreSqlCaibDialect().getFunctions().get(OracleCaibDialect.FUNCIO_ORDRE_TEXT);
		assertNotNull(funcio);
		assertEquals("t.concepte", funcio.render(null, List.of("t.concepte"), null));
	}

}
