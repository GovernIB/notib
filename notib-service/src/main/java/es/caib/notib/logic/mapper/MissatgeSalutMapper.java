package es.caib.notib.logic.mapper;

import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.comanda.ms.salut.model.SalutNivell;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.persist.entity.AvisEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper
public interface MissatgeSalutMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "dataInici", target = "data")
    @Mapping(source = "avisNivell", target = "nivell")
    @Mapping(source = "missatge", target = "missatge")
    MissatgeSalut toMissatgeSalut(AvisEntity avisEntity);

    @ValueMappings({
        @ValueMapping(source = "INFO", target = "INFO"),
        @ValueMapping(source = "WARNING", target = "WARN"),
        @ValueMapping(source = "ERROR", target = "ERROR")
    })
    SalutNivell avisNivellToSalutNivell(AvisNivellEnumDto nivell);
}
