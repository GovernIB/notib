package es.caib.notib.war.helper;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per sumar dies de caducitat a la data de caducitat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CaducitatHelper {

	public static Date sumarDiesLaborals(int diesCaducitat) {
		Calendar diaActual = Calendar.getInstance();
		diaActual.setTime(new Date());
		try {
			int diesSumats = 0;
			while (diesSumats < diesCaducitat) {
				diaActual.add(Calendar.DAY_OF_YEAR, 1);
		        if ((diaActual.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) &&
		        		diaActual.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
		            ++diesSumats;
		        }
		    }
		} catch (Exception ex) {
			String errorMessage = "Error sumant dies a la data de caducitat: ";
			LOGGER.error(errorMessage + ex.getMessage());
		}
		return diaActual.getTime();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CaducitatHelper.class);

}
