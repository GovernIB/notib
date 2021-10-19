package es.caib.notib.core.api.dto.servei;

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
public class ServeiFiltreDto extends AuditoriaDto implements Serializable {
	private String id;
	private String codi;
	private String nom;
	private String organGestor;
	private Long entitatId;
	private boolean comu;
	private boolean entregaCieActiva;
}
