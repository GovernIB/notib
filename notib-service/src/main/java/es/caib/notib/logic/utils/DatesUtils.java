package es.caib.notib.logic.utils;

import java.util.Calendar;
import java.util.Date;

public class DatesUtils {

    public static Date incrementarDataFi(Date dataFi) {

        if (dataFi == null) {
            return dataFi;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataFi);
        cal.add(Calendar.HOUR_OF_DAY, 23);
        cal.add(Calendar.MINUTE, 59);
        cal.add(Calendar.SECOND, 59);
        return cal.getTime();
    }
}