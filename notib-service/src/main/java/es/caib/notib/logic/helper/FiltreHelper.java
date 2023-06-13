package es.caib.notib.logic.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

public class FiltreHelper {

    public static Date toIniciDia(Date data) {

        if (data == null) {
            return data;
        }
        var cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date toFiDia(Date data) {

        if (data == null) {
            return data;
        }
        var cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
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
            return isNull != null ? isNull : field == null;
        }
    }

    public static class StringField extends FiltreField<String>{

        public StringField(String field) {
            super(field == null ? "" : field);
        }

        @Override
        public boolean isNull() {
            return field.isEmpty();
        }
    }

}
