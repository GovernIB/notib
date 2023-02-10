package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.notenviament.EnviamentDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class NotTableUpdate {

    private Long id;
    private NotificacioEstatEnumDto estat;
    private Date estatDate;
    private Date estatProcessatDate;

}
