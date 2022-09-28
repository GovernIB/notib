package es.caib.notib.logic.intf.dto.cie;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OperadorPostalFiltreDto {
	
	private String organismePagador;
	private String contracteNum;
	private Long organGestorId;
	
}
