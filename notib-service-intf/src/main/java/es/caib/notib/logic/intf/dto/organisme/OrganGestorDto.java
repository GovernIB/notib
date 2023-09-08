package es.caib.notib.logic.intf.dto.organisme;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class OrganGestorDto extends AuditoriaDto implements Serializable {

	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String codiPare;
	private String nomPare;
	private String nom;
	private String nomEs;
	private Long entitatId;
	private String entitatNom;
	private String llibre;
	private String llibreNom;
	private String oficinaNom;
	private List<PermisDto> permisos;
	private OficinaDto oficina;
	private OrganGestorEstatEnum estat = null;

	private Boolean sir;
	private String cif;
	private boolean actiu;

	private String nomCodi;

	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;

	private String estatTraduccio;

	public void setOrganGestorEstatEnum(OrganGestorEstatEnum estat) {
		this.estat = estat;
		actiu = estat != null && OrganGestorEstatEnum.V.equals(estat);
	}

	public String getNomCodi() {
		return nom + " (" + codi + ")";
	}

	public String getCodiNom() {
		return codi + " - " + nom;
	}

	public String getLlibreCodiNom() {
		if (llibre != null)
			return llibre + " " + (llibreNom != null ? llibreNom : "");
		return "";
	}
	public String getOficinaCodiNom() {
		if (oficina != null)
			return oficina.getCodi() + " " + (oficina.getNom() != null ? oficina.getNom() : "");
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
