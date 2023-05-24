package es.caib.notib.logic.intf.dto.mime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MimeType {

    private String extensio;
    private String mimeType;
    private String base64;
}