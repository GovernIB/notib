package es.caib.notib.logic.statemachine.mappers;

import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultaNotificaMapper {

    @Mapping(source = "notificacio.entitat.codi", target = "entitatCodi")
    @Mapping(source = "notificacio.entitat.apiKey", target = "entitatApiKey")
    @Mapping(source = "notificacio.deleted", target = "deleted")
    @Mapping(source = "notificaReferencia", target = "uuid")
    @Mapping(source = "notificaEstat", target = "estat")
    public ConsultaNotificaDto toDto(NotificacioEnviamentEntity enviament);

}
