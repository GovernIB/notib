package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcedimentFiltreDto extends AuditoriaDto implements Serializable {
	
	
	private String id;
	private String codi;
	private String nom;
	private String organGestor;
	private Long entitatId;
	private Boolean comu;
	
	private static final long serialVersionUID = -2393511650074099319L;
}
