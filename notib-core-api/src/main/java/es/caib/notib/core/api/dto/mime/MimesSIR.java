package es.caib.notib.core.api.dto.mime;

import sun.net.www.content.image.jpeg;
import sun.net.www.content.image.png;
import sun.net.www.content.text.plain;

public enum MimesSIR {

    JPG("image/jpeg"),
    JPEG("image/jpeg"),
    ODT("application/vnd.oasis.opendocument.text"),
    ODP("application/vnd.oasis.opendocument.presentation"),
    ODS("application/vnd.oasis.opendocument.spreadsheet"),
    ODG("application/vnd.oasis.opendocument.graphics"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    PNG("image/png"),
    PDF("application/pdf"),
    RTF("application/rtf"),
    SVG("image/svg+xml"),
    TIFF("image/tiff"),
    TXT("text/plain"),
    XML("application/xml"),
    XSIG("");

    private String tipus;

    MimesSIR(String tipus) {
        this.tipus = tipus;
    }

}
