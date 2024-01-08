package es.caib.notib.back.command;

import com.google.common.base.Strings;
import es.caib.notib.back.controller.BaseController;
import es.caib.notib.back.helper.MessageHelper;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class FiltreCommand {

    private List<AtributError> errors = new ArrayList<>();


    public void validarData(String data, String atribut) {

        if (Strings.isNullOrEmpty(data)) {
            return;
        }
        try {
            var df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(data, df);
        } catch (DateTimeParseException e) {
            errors.add(AtributError.builder().atribut(atribut).error("filtre.error.data.format.invalid").build());
        }
    }

    public void validarData(Date date, String atribut) {

        if (date == null) {
            return;
        }
        try {
            var data = String.format("%td/%tm/%tY", date, date, date);
            var df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(data, df);
        } catch (DateTimeParseException e) {
            errors.add(AtributError.builder().atribut(atribut).error("filtre.error.data.format.invalid").build());
        }
    }
}
