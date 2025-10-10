package es.caib.notib.logic.intf.dto.adviser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
@Setter
public class ResultatExecucio {
    private String codi;
    private String descripcio;
    private boolean error = false;
    private String errorDescripcio = "";

    public void setError(String errorDescripcio, Exception ex) {

        if (!StringUtils.isEmpty(errorDescripcio)) {
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