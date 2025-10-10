package es.caib.notib.logic.intf.dto.accioMassiva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccioMassivaElementDto implements Serializable  {

    private Long id;
    private AccioMassivaDto accioMassiva;
    private SeleccioTipus seleccioTipus;
    private Long elementId;
    private Date dataExecucio;
    private String errorDescripcio;
    private String excepcioStackTrace;

    public boolean isPendent() {
        return dataExecucio == null && StringUtils.isEmpty(errorDescripcio);
    }
}
