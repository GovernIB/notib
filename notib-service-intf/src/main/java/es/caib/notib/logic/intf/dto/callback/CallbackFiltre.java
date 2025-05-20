package es.caib.notib.logic.intf.dto.callback;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.SiNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackFiltre {

    private Long entitatId;
    private String usuariCodi;
    private String referenciaRemesa;
    private String dataInici;
    private String dataFi;
    private CallbackEstatEnumDto estat;
    private SiNo fiReintents;
    private int maxReintents;

    public boolean usuariCodiNull() {
        return Strings.isNullOrEmpty(usuariCodi);
    }

    public boolean referenciaRemesaNull() {
        return Strings.isNullOrEmpty(referenciaRemesa);
    }

    public boolean dataIniciNull() {
        return Strings.isNullOrEmpty(dataInici);
    }

    public boolean dataFiNull() {
        return Strings.isNullOrEmpty(dataFi);
    }

    public boolean fiReintentsNull() {
        return fiReintents == null;
    }

    public int fiReintentsInt() {
        return fiReintents != null ? fiReintents.getValor() : 0;
    }

    public Date dataIniciDate() {

        if (Strings.isNullOrEmpty(dataInici)) {
            return null;
        }
        try {
            var data = new SimpleDateFormat("dd/MM/yyyy").parse(dataInici);
            var cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            log.error("[CallbackFiltre] dataIniciDate, error parsejant la data : " + dataInici, e);
            return null;
        }
    }

    public Date dataFiDate() {

        if (Strings.isNullOrEmpty(dataFi)) {
            return null;
        }
        try {
            var data = new SimpleDateFormat("dd/MM/yyyy").parse(dataFi);
            var cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            return cal.getTime();
        } catch (Exception e) {
            log.error("[CallbackFiltre] dataFiDate, error parsejant la data : " + dataFi, e);
            return null;
        }
    }

}

