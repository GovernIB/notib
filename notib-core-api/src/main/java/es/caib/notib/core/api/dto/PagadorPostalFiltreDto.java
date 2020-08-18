package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PagadorPostalFiltreDto {
	
	private String dir3codi;
	private String contracteNum;
	private Long organGestorId;
	
}
