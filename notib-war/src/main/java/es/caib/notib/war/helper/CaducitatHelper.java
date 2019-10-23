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
		diaActual.add(Calendar.DATE, 1);
		
		try {
			for (int dia = 1; dia <= diesCaducitat; dia++) {
				Calendar diaSeguent = Calendar.getInstance();
				diaSeguent.setTime(new Date());
				diaSeguent.add(Calendar.DAY_OF_YEAR, dia);
				
				if ((diaSeguent.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)) {
					diaActual.add(Calendar.DAY_OF_YEAR, 1);
				} else if (diaSeguent.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
					dia = dia + 2;
					diaActual.add(Calendar.DAY_OF_YEAR, 3);
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
