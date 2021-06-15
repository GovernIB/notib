package es.caib.notib.core.api.dto.organisme;

import es.caib.notib.core.api.dto.AuditoriaDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreDto extends AuditoriaDto implements Serializable {
	
	private String codi;
	private String nom;
	private String oficina;
	private OrganGestorEstatEnum estat;
	
	private static final long serialVersionUID = -2393511650074099319L;
}
