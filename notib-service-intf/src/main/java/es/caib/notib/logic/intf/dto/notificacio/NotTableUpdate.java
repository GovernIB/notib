package es.caib.notib.logic.intf.dto.notificacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotTableUpdate {

    private Long id;
    private NotificacioEstatEnumDto estat;
    private Date estatDate;
    private Date estatProcessatDate;
    private Integer reintentsRegistre;

}
