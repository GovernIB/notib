package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorDto extends AuditoriaDto implements Serializable {
	
	private Long id;
	private String codi;
	private String nom;
	private Long entitatId;
	private String entitatNom;
	private List<PermisDto> permisos;
	
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	
	private static final long serialVersionUID = -2393511650074099319L;
}
