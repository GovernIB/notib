package es.caib.notib.logic.intf.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgresActualitzacioDtoTest {

	@Test
	void addInfoShouldInvokeOnInfoHookWhenSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		List<String> received = new ArrayList<>();
		progres.setOnInfo(entry -> received.add(entry.getText()));
		// when
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, "hola");
		// then
		assertEquals(List.of("hola"), received);
	}

	@Test
	void addInfoShouldNotThrowWhenNoHookSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		// when / then
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, "hola");
		assertEquals(1, progres.getInfo().size());
	}

	@Test
	void incrementOperacionsRealitzadesShouldInvokeOnProgressChangedHookWhenSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		progres.setNumOperacions(10);
		List<Integer> received = new ArrayList<>();
		progres.setOnProgressChanged(received::add);
		// when
		progres.incrementOperacionsRealitzades(5);
		// then
		assertEquals(List.of(50), received);
	}

	@Test
	void incrementOperacionsRealitzadesShouldNotThrowWhenNoHookSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		progres.setNumOperacions(10);
		// when / then
		progres.incrementOperacionsRealitzades(5);
		assertTrue(progres.getProgres() == 50);
	}

}
