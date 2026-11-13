package es.caib.notib.logic.mapper;

import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SalutNivell;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.persist.entity.AvisEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper
public interface MissatgeSalutMapper {

	@BeanMapping(ignoreByDefault = true)
	@Mapping(source = "dataInici", target = "data")
	@Mapping(source = "avisNivell", target = "nivell")
	@Mapping(target = "missatge", expression = "java((avisEntity.getEntitat() != null ? avisEntity.getEntitat().getDir3Codi() + \" - \" : \"\") + avisEntity.getMissatge())")
	MissatgeSalut toMissatgeSalut(AvisEntity avisEntity);

	@ValueMappings({
			@ValueMapping(source = "INFO", target = "INFO"),
			@ValueMapping(source = "WARNING", target = "WARN"),
			@ValueMapping(source = "ERROR", target = "ERROR")
	})
	SalutNivell avisNivellToSalutNivell(AvisNivellEnumDto nivell);


	default Date toLocalDateTime(OffsetDateTime offsetDateTime) {
		return Date.from(offsetDateTime.toInstant());
	}

	default OffsetDateTime toOffsetDateTime(Date data) {
		return new java.util.Date(data.getTime())
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toOffsetDateTime();
	}
}
