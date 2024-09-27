package es.caib.notib.logic.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    public static boolean compareDatesWithoutTime(Date date1, Date date2) {

        var cal1 = Calendar.getInstance();
        var cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return cal1.getTime().equals(cal2.getTime());
    }

    public static int getDifferenceInDays(Date date1, Date date2) {

        var diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Date convertir(Optional<LocalDateTime> optionalLocalDateTime) {

        if (optionalLocalDateTime.isEmpty()) {
            return null; // or handle the absence of value as needed
        }
        var localDateTime = optionalLocalDateTime.get();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
