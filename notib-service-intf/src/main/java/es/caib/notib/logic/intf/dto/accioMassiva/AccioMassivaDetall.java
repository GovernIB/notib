package es.caib.notib.logic.intf.dto.accioMassiva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccioMassivaDetall {

    private Long id;
    private SeleccioTipus seleccioTipus;
    private String referencia;
    private Date data;
    private String errorDesc;
    private String errorStacktrace;


    public String getDataString() {
        var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return data != null ? df.format(data) : "";
    }

    public boolean isExecutadaOk() {
        return data != null && StringUtils.isEmpty(errorDesc) && StringUtils.isEmpty(errorStacktrace);
    }

    public boolean isPendent() {
        return data == null;
    }
}
