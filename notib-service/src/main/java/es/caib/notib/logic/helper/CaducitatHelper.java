package es.caib.notib.logic.helper;

import lombok.extern.slf4j.Slf4j;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilitat per sumar dies de caducitat a la data de caducitat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */@Slf4j
public class CaducitatHelper {

	public static Date sumarDiesLaborals(Date dataCaducitat, int diesCaducitat) {

		var diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
//		diaActual.add(Calendar.DATE, 1); La data de caducitat comtempla tot el dia final, fins a última hora.
		
		try {
			var diesASumar = 1;
			Calendar diaSeguent;
			for (var dia = 1; dia <= diesCaducitat; dia++) {
				diaSeguent = Calendar.getInstance();
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
			var errorMessage = "Error sumant dies a la data de caducitat: ";
			log.error(errorMessage + ex.getMessage());
		}
		return diaActual.getTime();
	}

	public static Date sumarDiesNaturals(int diesCaducitat) {
		return sumarDiesNaturals(new Date(), diesCaducitat);
	}

	public static Date sumarDiesNaturals(Date dataCaducitat, int diesCaducitat) {

		var diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
		diaActual.add(Calendar.DATE, diesCaducitat);
		return diaActual.getTime();
	}
}
