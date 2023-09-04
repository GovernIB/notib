/**
 * 
 */
package es.caib.notib.logic.intf.dto.organisme;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UnitatOrganitzativaDto implements Serializable {

	private String codi;
	private String denominacio;
	private String oldDenominacio;
	private String denominacioCooficial;
	private String oldDenominacioCooficial;
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio
	private String arrel;
	private String superior;
	private String localitat;
	private String idPare;
	private String cif;
	private Boolean tieneOficinaSir = false;

	private List<UnitatOrganitzativaDto> lastHistoricosUnitats;

	public String getNom() {
		return denominacioCooficial != null && denominacioCooficial.length() > 0 ? denominacioCooficial : denominacio + " (" + this.codi + ")";
	}

	private static final long serialVersionUID = -5602898182576627524L;

}