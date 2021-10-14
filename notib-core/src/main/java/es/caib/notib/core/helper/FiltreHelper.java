package es.caib.notib.core.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

public class FiltreHelper {

    public static Date toIniciDia(Date data) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            data = cal.getTime();
        }
        return data;
    }

    public static Date toFiDia(Date data) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            data = cal.getTime();
        }
        return data;
    }

    @Getter
    @AllArgsConstructor
    public static class FiltreField<T>{
        protected T field;
        private Boolean isNull = null;

        public FiltreField(T field) {
            this.field = field;
        }

        public boolean isNull() {
            if (isNull == null) {
                return field == null;
            }
            return isNull;
        }
    }

    public static class StringField extends FiltreField<String>{

        public StringField(String field) {
            super(field == null ? "" : field);
        }
        public boolean isNull() {
            return field.isEmpty();
        }
    }

}
