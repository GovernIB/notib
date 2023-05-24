package es.caib.notib.logic.intf.dto.notificacio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class NotTableUpdate {

    private Long id;
    private NotificacioEstatEnumDto estat;
    private Date estatDate;
    private Date estatProcessatDate;
    private Integer reintentsRegistre;

}
