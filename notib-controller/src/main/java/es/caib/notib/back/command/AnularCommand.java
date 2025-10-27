package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Getter
@Setter
public class AnularCommand {

    private Long notificacioId;
    private Long enviamentId;
    @NotEmpty
    @Size(max=250)
    private String motiu;
    private List<Long> notificacionsId;
    private List<Long> enviamentsId;
    private boolean massiu;
    private SeleccioTipus seleccioTipus;


    public int getMotiuDefaultSize() {

        int motiuSize = 0;
        try {
            Field motiu = this.getClass().getDeclaredField("motiu");
            motiuSize = motiu.getAnnotation(Size.class).max();
        } catch (Exception ex) {
            log.error("No s'ha pogut recuperar la longitud de principal: " + ex.getMessage());
        }
        return motiuSize;
    }
}
