/**
 * 
 */
package es.caib.notib.logic.helper;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import es.caib.notib.logic.intf.dto.ExcepcioLogDto;

/**
 * Mètodes per a la gestió del log d'excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExcepcioLogHelper {

	public static final int DEFAULT_MAX_EXCEPCIONS = 20;

	private LinkedList<ExcepcioLogDto> excepcions = new LinkedList<>();

	public List<ExcepcioLogDto> findAll() {

		int index = 0;
		for (var excepcio: excepcions) {
			excepcio.setIndex(new Long(index++));
		}
		return excepcions;
	}

	public void addExcepcio(Throwable exception) {

		while (excepcions.size() >= DEFAULT_MAX_EXCEPCIONS) {
			excepcions.remove(excepcions.size() - 1);
		}
		excepcions.add(0, new ExcepcioLogDto(exception));
	}
}
