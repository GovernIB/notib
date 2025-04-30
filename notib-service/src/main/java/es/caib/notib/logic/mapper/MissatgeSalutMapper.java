package es.caib.notib.logic.mapper;

import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.notib.persist.entity.AvisEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MissatgeSalutMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "dataInici", target = "data")
    @Mapping(source = "avisNivell", target = "nivell")
    @Mapping(source = "missatge", target = "missatge")
    public MissatgeSalut toMissatgeSalut(AvisEntity avisEntity);

}
