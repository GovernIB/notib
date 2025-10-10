package es.caib.notib.logic.intf.dto.accioMassiva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccioMassivaFiltre {

    private Long id;
    private Long entitatId;
    private AccioMassivaTipus tipus;
    private String usuariCodi;
    private String dataInici;
    private String dataFi;
    private AccioMassivaElementEstat estat;
    private String estatString;

    public boolean dataIniciNull() {
        return StringUtils.isEmpty(dataInici);
    }

    public boolean dataFiNull() {
        return StringUtils.isEmpty(dataFi);
    }

    public boolean tipusNull() {
        return tipus == null;
    }

    public boolean usuariCodiNull() {
        return StringUtils.isEmpty(usuariCodi);
    }

    public boolean estatNull() {
        return estat == null;
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
            log.error("[AccioMassivaFiltre] dataIniciDate, error parsejant la data : " + dataInici, e);
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
            log.error("[AccioMassivaFiltre] dataFiDate, error parsejant la data : " + dataFi, e);
            return null;
        }
    }
}
