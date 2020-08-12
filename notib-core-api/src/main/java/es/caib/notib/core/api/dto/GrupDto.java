package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private Long entitatId;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		GrupDto other = (GrupDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
	 	} else if (id.equals(other.id))
			return true;
		return true;
	}

	private static final long serialVersionUID = 7999677809220395478L;

}
