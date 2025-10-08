package es.caib.notib.back.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utilitat per sumar dies de caducitat a la data de caducitat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class CaducitatHelper {

	private CaducitatHelper() {
		throw new IllegalStateException("CaducitatHelper no es pot instanciar");
	}

	public static Date sumarDiesLaborals(Date dataCaducitat, int diesCaducitat) {

		var diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
		try {
			int diesASumar = 1;
			Calendar diaSeguent;
			for (var dia = 1; dia <= diesCaducitat; dia++) {
				diaSeguent = Calendar.getInstance();
				diaSeguent.setTime(dataCaducitat);
				diaSeguent.add(Calendar.DAY_OF_YEAR, diesASumar);
				if ((diaSeguent.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)) {
					diesASumar++;
					diaActual.add(Calendar.DAY_OF_YEAR, 1);
					continue;
				}
				if (diaSeguent.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
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

	public static int getDiesEntreDates(Date fi) {
		return getDiesEntreDates(new Date(), fi);
	}

	public static int getDiesEntreDates(Date inici, Date fi) {

		var diff = fi.getTime() - inici.getTime();
		return (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) +1);
	}

}
