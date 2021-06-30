package es.caib.notib.core.api.ws.notificacio;

import lombok.Data;

import java.util.Date;

@Data
public class RespostaBase {
    private boolean error;
    private Date errorData;
    private String errorDescripcio;
}
