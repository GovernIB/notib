package es.caib.notib.logic.mapper;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommonConversor.class})
public abstract class NotificacioMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "enviamentTipus", target = "enviamentTipus")
    @Mapping(source = "concepte", target = "concepte")
    @Mapping(source = "procediment", target = "procediment", qualifiedByName = "procedimentCodiNom")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "optionalUser")
    @Mapping(source = "createdDate", target = "createdDate", qualifiedByName = "optionalDate")
    public abstract NotificacioDto toErrorRegistreDto(NotificacioEntity notificacio);


    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "enviamentTipus", target = "enviamentTipus")
    @Mapping(source = "concepte", target = "concepte")
    @Mapping(source = "procediment", target = "procediment", qualifiedByName = "procedimentCodiNom")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "optionalUser")
    @Mapping(source = "createdDate", target = "createdDate", qualifiedByName = "optionalDate")
    @Mapping(source = "estatDate", target = "estatDate")
    @Mapping(source = "estat", target = "estat")
//    @Mapping(source = "notificaErrorData", target = "notificaErrorData")
//    @Mapping(source = "notificaErrorDescripcio", target = "notificaErrorDescripcio")
    public abstract NotificacioDto toCallbackErrorDto(NotificacioEntity notificacio);

}
