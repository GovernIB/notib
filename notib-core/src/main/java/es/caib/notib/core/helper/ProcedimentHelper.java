package es.caib.notib.core.helper;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public ProcedimentDto toProcedimentDto(
			ProcedimentEntity procediment) {
		
		ProcedimentDto dto = new ProcedimentDto();
		
		dto.setCodi(procediment.getCodi());
		dto.setNom(procediment.getNom());
		dto.setCodisia(procediment.getCodisia());
		dto.setEntitat(
				conversioTipusHelper.convertir(
						procediment.getEntitat(), 
						EntitatDto.class));
		dto.setPagadorpostal(
				conversioTipusHelper.convertir(
						procediment.getPagadorpostal(), 
						PagadorPostalDto.class));
		dto.setPagadorcie(
				conversioTipusHelper.convertir(
						procediment.getPagadorcie(), 
						PagadorCieDto.class));
		
		dto.setAgrupar(procediment.isAgrupar());
		
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
