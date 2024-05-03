package es.caib.notib.logic.statemachine.mappers;

import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class ConsultaSirMapper {

    @Mapping(source = "notificaReferencia", target = "uuid")
    @Mapping(source = "notificaEstat", target = "estat")
    @Mapping(source = "createdBy", target = "usuari", qualifiedByName = "usuariCodi")
    @Mapping(source = "notificacio.entitat.nom", target = "entitatNom")
    @Mapping(source = "notificacio.deleted", target = "deleted")
    @Mapping(source = "notificacio.entitat.dir3Codi", target = "entitatDir3Codi")
    @Mapping(source = "notificacio.id", target = "notificacioId")
    @Mapping(source = "notificacio.concepte", target = "concepte")
    @Mapping(source = "notificacio.estat", target = "notificacioEstat")
    @Mapping(source = "notificacio.tipusUsuari", target = "tipusUsuari")
    @Mapping(source = "notificacio.motiu", target = "motiu")
    @Mapping(source = "notificacio.procediment.nom", target = "procedimentNom")
    public abstract ConsultaSirDto toDto(NotificacioEnviamentEntity enviament);


    @Named("usuariCodi")
    public static String getCreatedBy(Optional<UsuariEntity> usuari) {
        if (usuari == null || !usuari.isPresent()) {
            return null;
        }
        return usuari.get().getCodi();
    }

}
