package es.caib.notib.back.command;

import es.caib.notib.back.controller.BaseController;
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

        if (data == null) {
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

//    public String getErrorMsg(HttpServletRequest request) {
//
//        StringBuilder msg = new StringBuilder();
//        for (var error : errors) {
//            msg.append("<div>");
//            msg.append(getMessage(request, error.getAtribut())).append(" - ").append(getMessage(request, error.getError()));
//            msg.append("</div>");
//        }
//        return msg.toString();
//    }
}
