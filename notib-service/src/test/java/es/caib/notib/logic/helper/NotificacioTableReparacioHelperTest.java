package es.caib.notib.logic.helper;

import es.caib.notib.persist.repository.NotificacioRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaris per NotificacioTableReparacioHelper.
 */
class NotificacioTableReparacioHelperTest {

	private final NotificacioRepository notificacioRepository = mock(NotificacioRepository.class);
	private final NotificacioTableHelper notificacioTableHelper = mock(NotificacioTableHelper.class);
	private final NotificacioTableReparacioHelper helper = new NotificacioTableReparacioHelper(notificacioRepository, notificacioTableHelper);

	@Test
	void crearRegistresQueFaltenShouldDoNothing_whenAllHaveRegistre() {
		when(notificacioRepository.findIdsSenseRegistreTaula()).thenReturn(List.of());

		assertTrue(helper.crearRegistresQueFalten());
		verify(notificacioTableHelper, never()).crearRegistreEnTransaccioNova(anyLong());
	}

	@Test
	void crearRegistresQueFaltenShouldContinueAndReportPending_whenOneFails() {
		when(notificacioRepository.findIdsSenseRegistreTaula()).thenReturn(List.of(1L, 2L, 3L));
		doThrow(new RuntimeException("error")).when(notificacioTableHelper).crearRegistreEnTransaccioNova(2L);

		assertFalse(helper.crearRegistresQueFalten());
		verify(notificacioTableHelper).crearRegistreEnTransaccioNova(1L);
		verify(notificacioTableHelper).crearRegistreEnTransaccioNova(3L);
	}

}
