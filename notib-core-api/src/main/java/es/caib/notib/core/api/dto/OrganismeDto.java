package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrganismeDto implements Serializable{

	private String codi;
	private String nom;
	private String pare;
	private List<String> fills = new ArrayList<String>();
	
	public String getNomComplet() {
		return codi + " - " + nom;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrganismeDto other = (OrganismeDto)obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -3831959843313056718L;
	
}
