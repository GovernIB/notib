package es.caib.notib.logic.intf.dto.escaneig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class DigitalitzacioResultat implements Serializable {

    private static final long serialVersionUID = 8625490921780066599L;
    private boolean error;
    private String errorDescripcio;
    private DigitalitzacioEstat estat;
    private byte[] contingut;
    private String nomDocument;
    private String mimeType;
    private String eniTipoFirma;
    private Integer resolucion;
    private String idioma;
    private String usuari; //Usuari que ha iniciat el proces de firma
}
