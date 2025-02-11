package es.caib.notib.logic.intf.dto.adviser;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class ResultatExecucio {
    private String codi;
    private String descripcio;
    private boolean error = false;
    private String errorDescripcio = "";

    public void setError(String errorDescripcio, Exception ex) {

        if (!Strings.isNullOrEmpty(errorDescripcio)) {
            this.error = true;
        }
        this.errorDescripcio = errorDescripcio;
        if (ex == null) {
            log.error(errorDescripcio);
        } else {
            log.error(errorDescripcio, ex);
        }
    }
}