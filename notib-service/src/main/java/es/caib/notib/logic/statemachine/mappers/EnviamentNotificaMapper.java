package es.caib.notib.logic.statemachine.mappers;

import es.caib.notib.logic.intf.statemachine.dto.EnviamentNotificaDto;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface EnviamentNotificaMapper {

    @Mapping(source = "notificacio.entitat.codi", target = "entitatCodi")
    @Mapping(source = "notificacio.referencia", target = "notificacioUuid")
    @Mapping(source = "notificaReferencia", target = "uuid")
    @Mapping(source = "notificacio.enviaments", target = "enviamentsUuid", qualifiedByName = "uuids")
    public EnviamentNotificaDto toDto(NotificacioEnviamentEntity enviament);

    @Named("uuids")
    default List<String> getEnviamentsUuid(Set<NotificacioEnviamentEntity> enviaments) {
        if (enviaments == null || enviaments.isEmpty()) {
            return null;
        }
        return enviaments.stream().map(e -> e.getNotificaReferencia()).collect(Collectors.toList());
    }
}
