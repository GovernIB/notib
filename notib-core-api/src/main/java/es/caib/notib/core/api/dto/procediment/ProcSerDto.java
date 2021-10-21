package es.caib.notib.core.api.dto.procediment;

import es.caib.notib.core.api.dto.PermisDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ProcSerDto extends ProcSerDataDto {

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualProcessar;
	private boolean usuariActualNotificacio;
	private boolean usuariActualAdministration;

	private boolean entregaCieActivaAlgunNivell;

	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getDescripcio() {
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
		ProcSerDto other = (ProcSerDto)obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
}
