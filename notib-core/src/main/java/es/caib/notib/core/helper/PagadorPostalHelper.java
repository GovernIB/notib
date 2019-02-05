package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.entity.PagadorPostalEntity;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PagadorPostalHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public PagadorPostalDto toPagadorPostalDto(
			PagadorPostalEntity pagadorPostal) {
		
		PagadorPostalDto dto = new PagadorPostalDto();
		
		dto.setDir3codi(pagadorPostal.getDir3codi());
		dto.setContracteNum(pagadorPostal.getContracteNum());
		dto.setContracteDataVig(pagadorPostal.getContracteDataVig());
		dto.setFacturacioClientCodi(pagadorPostal.getFacturacioClientCodi());
		dto.setId(pagadorPostal.getId());
		
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
