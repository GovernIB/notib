package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class OrganGestorDto extends AuditoriaDto implements Serializable {
	
	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private Long entitatId;
	private String entitatNom;
	private String llibre;
	private String llibreNom;
	private String oficinaNom;
	private List<PermisDto> permisos;
	
	public String getLlibreCodiNom() {
		if (llibre != null)
			return llibre + " " + (llibreNom != null ? llibreNom : "");
		return "";
	}
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	
	public String getOrganGestorDesc() {
		if (nom != null && !nom.isEmpty())
			return codi + " - " + nom;
		return codi;
	}
	
	private static final long serialVersionUID = -2393511650074099319L;
}
