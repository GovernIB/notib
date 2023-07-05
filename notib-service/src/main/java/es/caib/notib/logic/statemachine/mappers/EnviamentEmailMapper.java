package es.caib.notib.logic.statemachine.mappers;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.statemachine.dto.EnviamentEmailDto;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class EnviamentEmailMapper {

    @Mapping(source = "notificaReferencia", target = "uuid")
    @Mapping(source = "notificacio.entitat.codi", target = "entitatCodi")
    @Mapping(source = "notificacio.entitat.nom", target = "entitatNom")
    @Mapping(source = "notificacio.id", target = "notificacioId")
    @Mapping(source = "notificacio.concepte", target = "concepte")
    @Mapping(source = "notificacio.enviamentTipus", target = "tipusEnviament")
    @Mapping(source = "notificacio.descripcio", target = "descripcio")
    @Mapping(source = "notificacio.procediment.nom", target = "procedimentNom")
    @Mapping(source = "notificacio.organGestor.nom", target = "organNomCat")
    @Mapping(source = "notificacio.organGestor", target = "organNomEs", qualifiedByName = "organNomEs")
    @Mapping(source = "titular.nom", target = "titularNom")
    @Mapping(source = "titular.email", target = "titularEmail")
    public abstract EnviamentEmailDto toDto(NotificacioEnviamentEntity enviament);


    @Named("organNomEs")
    public static String getOrganNomEs(OrganGestorEntity organGestor) {
        if (organGestor == null) {
            return null;
        }
        return !Strings.isNullOrEmpty(organGestor.getNomEs()) ? organGestor.getNomEs() : organGestor.getNom();
    }

}
