package es.caib.notib.core.helper;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.entity.GrupEntity;
/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class GrupHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public GrupDto toGrupDto(
			GrupEntity grup) {
		
		GrupDto dto = new GrupDto();
		
		dto.setId(grup.getId());
		dto.setCodi(grup.getCodi());
		dto.setNom(grup.getNom());
		
		/*dto.setCreatedBy(
				conversioTipusHelper.convertir(
				pagadorPostal.getCreatedBy(),
				UsuariDto.class));
		dto.setCreatedDate(pagadorPostal.getCreatedDate().toDate());
		dto.setLastModifiedBy(
				conversioTipusHelper.convertir(
						pagadorPostal.getLastModifiedBy(),
						UsuariDto.class));
		dto.setLastModifiedDate(pagadorPostal.getLastModifiedDate().toDate());*/
		
		return dto;
	}
	
}
