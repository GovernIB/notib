package es.caib.notib.logic.helper;

import org.springframework.stereotype.Component;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.persist.entity.GrupEntity;
/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class GrupHelper {
	
	public GrupDto toGrupDto(GrupEntity grup) {
		
		var dto = new GrupDto();
		dto.setId(grup.getId());
		dto.setCodi(grup.getCodi());
		dto.setNom(grup.getNom());
		return dto;
	}
	
}
