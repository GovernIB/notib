package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.entity.PagadorCieEntity;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PagadorCieHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public PagadorCieDto toPagadorCieDto(
			PagadorCieEntity pagadorCie) {
		
		PagadorCieDto dto = new PagadorCieDto();
		
		dto.setDir3codi(pagadorCie.getDir3codi());
		dto.setContracteDataVig(pagadorCie.getContracteDataVig());
		dto.setId(pagadorCie.getId());
		
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
