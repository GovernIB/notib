package es.caib.notib.logic.utils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        var cal = Calendar.getInstance();
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

    public static boolean isNowAfterDate(LocalDateTime data, int llindar) {
        return isNowAfterDate(Timestamp.valueOf(data), llindar);
    }

    public static boolean isNowAfterDate(Date data, int llindar) {

        var ara = new Date();
        var cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DATE, llindar);
        return ara.after(cal.getTime());
    }

    public static Date toDate(XMLGregorianCalendar calendar) {
        return calendar != null ? calendar.toGregorianCalendar().getTime() : null;
    }


    public static final String FORMAT1 = "dd/MM/yyyy";
    public static final String FORMAT2 = "dd-MM-yyyy";
    public static final String FORMAT3 = "dd/MM/yy";
    public static final String FORMAT4 = "dd-MM-yy";
    public static final String FORMAT5 = "yyyy/MM/dd";
    public static final String FORMAT6 = "yyyy-MM-dd";

    public static Date checkDate(Object dateObj, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT1);
        Date dataDate = null;
        if (dateObj instanceof Date) {
            dataDate = (Date) dateObj;
        } else if (dateObj instanceof String) {
            // Validar format data
            String dataText = (String) dateObj;
            try {
                String[] formats = {format, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6};
                if (format != null && !format.isEmpty()) {
                    dataDate = parseDate(dataText, FORMAT1, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6, format);
                } else {
                    dataDate = parseDate(dataText, FORMAT1, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6);
                }
            } catch (Exception ex) {
                dataDate = null;
            }
        }

        return dataDate;
    }

    public static Date parseDate(String dateString, String... formatStrings) {
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
