package es.caib.notib.logic.intf.dto.procediment;

import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorCacheDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcSerCacheDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private OrganGestorCacheDto organGestor;
	private boolean comu;
	private boolean actiu;
	private ProcSerTipusEnum tipus;
	
	public String getOrganGestorDesc() {
		if (organGestor == null)
			return null;
		if (organGestor.getNom() != null && !organGestor.getNom().isEmpty())
			return organGestor.getCodi() + " - " + organGestor.getNom();
		return organGestor.getCodi();
	}
	
	public String getDescripcio() {
		return codi + " - " + nom;
	}

}
