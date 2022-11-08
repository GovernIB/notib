package es.caib.notib.logic.intf.dto.organisme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrganGestorCacheDto implements Serializable {
	
	private Long id;
	private String codi;
	private String nom;
	private OrganGestorEstatEnum estat = null;
	private boolean actiu;
//	private boolean entregaCieActiva;
	private String oficina;

	public void setOrganGestorEstatEnum(OrganGestorEstatEnum estat) {
		this.estat = estat;
		actiu = estat != null && OrganGestorEstatEnum.V.equals(estat);
	}

	public String getOrganGestorDesc() {
		if (nom != null && !nom.isEmpty())
			return codi + " - " + nom;
		return codi;
	}
	
	private static final long serialVersionUID = -2393511650074099319L;
}
