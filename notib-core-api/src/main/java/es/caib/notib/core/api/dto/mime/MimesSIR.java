package es.caib.notib.core.api.dto.mime;

import lombok.Getter;
import sun.net.www.content.image.jpeg;
import sun.net.www.content.image.png;
import sun.net.www.content.text.plain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MimesSIR {

    public static final List<String> formats = new ArrayList<>(Arrays.asList(
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

}
