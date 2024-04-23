package es.caib.notib.logic.intf.dto.mime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mimes {

    private Mimes() {
        throw new IllegalStateException("Mimes no pot ser instanciat");
    }

    private static final List<String> formatsSIR = new ArrayList<>(Arrays.asList(
            "image/jpeg", "application/vnd.oasis.opendocument.text",
            "application/vnd.oasis.opendocument.presentation",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.graphics",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "image/png",
            "application/pdf",
            "application/rtf",
            "image/svg+xml",
            "image/tiff",
            "text/plain",
            "application/xml",
            "xsig" // TODO FALTA DETERMINAR EL MIME TYPE PER UN XSIG
    ));

    private static final List<String> formatsNoSIR = new ArrayList<>(Arrays.asList(
            "application/pdf",
            "application/zip"
    ));

    public static List<String> getFormatsSIR() {
        return formatsSIR;
    }

    public static List<String> getFormatsNoSIR() {
        return formatsNoSIR;
    }

}