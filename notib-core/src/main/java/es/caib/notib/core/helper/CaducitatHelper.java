package es.caib.notib.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Utilitat per sumar dies de caducitat a la data de caducitat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CaducitatHelper {

	public static Date sumarDiesLaborals(
			Date dataCaducitat,
			int diesCaducitat) {
		Calendar diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
//		diaActual.add(Calendar.DATE, 1); La data de caducitat comtempla tot el dia final, fins a última hora.
		
		try {
			int diesASumar = 1;
			for (int dia = 1; dia <= diesCaducitat; dia++) {
				Calendar diaSeguent = Calendar.getInstance();
				diaSeguent.setTime(dataCaducitat);
				diaSeguent.add(Calendar.DAY_OF_YEAR, diesASumar);
				
				if ((diaSeguent.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)) {
					diesASumar++;
					diaActual.add(Calendar.DAY_OF_YEAR, 1);
				} else if (diaSeguent.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
					diesASumar += 3;
					diaActual.add(Calendar.DAY_OF_YEAR, 3);
				}
			}
		} catch (Exception ex) {
			String errorMessage = "Error sumant dies a la data de caducitat: ";
			LOGGER.error(errorMessage + ex.getMessage());
		}
		return diaActual.getTime();
	}

	public static Date sumarDiesNaturals(
			int diesCaducitat) {
		return sumarDiesNaturals(new Date(), diesCaducitat);
	}

	public static Date sumarDiesNaturals(
			Date dataCaducitat,
			int diesCaducitat) {
		Calendar diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
		diaActual.add(Calendar.DATE, diesCaducitat);
		return diaActual.getTime();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CaducitatHelper.class);

}
