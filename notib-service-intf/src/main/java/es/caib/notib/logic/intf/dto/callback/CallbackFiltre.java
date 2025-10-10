package es.caib.notib.logic.intf.dto.callback;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.SiNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    private String dataIniciUltimIntent;
    private String dataFiUltimIntent;
    private CallbackEstatEnumDto estat;
    private SiNo fiReintents;
    private int maxReintents;

    public boolean usuariCodiNull() {
        return StringUtils.isEmpty(usuariCodi);
    }

    public boolean estatNull() {
        return estat == null;
    }

    public boolean referenciaRemesaNull() {
        return StringUtils.isEmpty(referenciaRemesa);
    }

    public boolean dataIniciNull() {
        return StringUtils.isEmpty(dataInici);
    }

    public boolean dataFiNull() {
        return StringUtils.isEmpty(dataFi);
    }

    public boolean dataIniciUltimIntentNull() {
        return StringUtils.isEmpty(dataIniciUltimIntent);
    }

    public boolean dataFiUltimIntentNull() {
        return StringUtils.isEmpty(dataFiUltimIntent);
    }

    public boolean fiReintentsNull() {
        return fiReintents == null;
    }

    public int fiReintentsInt() {
        return fiReintents != null ? fiReintents.getValor() : 0;
    }

    public Date dataIniciDate() {

        if (StringUtils.isEmpty(dataInici)) {
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

        if (StringUtils.isEmpty(dataFi)) {
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

    public Date dataIniciUltimIntentDate() {

        if (StringUtils.isEmpty(dataIniciUltimIntent)) {
            return null;
        }
        try {
            var data = new SimpleDateFormat("dd/MM/yyyy").parse(dataIniciUltimIntent);
            var cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            log.error("[CallbackFiltre] dataIniciDate, error parsejant la data : " + dataIniciUltimIntent, e);
            return null;
        }
    }

    public Date dataFiUltimIntentDate() {

        if (StringUtils.isEmpty(dataFiUltimIntent)) {
            return null;
        }
        try {
            var data = new SimpleDateFormat("dd/MM/yyyy").parse(dataFiUltimIntent);
            var cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            return cal.getTime();
        } catch (Exception e) {
            log.error("[CallbackFiltre] dataFiDate, error parsejant la data : " + dataFiUltimIntent, e);
            return null;
        }
    }

}

