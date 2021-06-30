package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class ProcedimentSimpleDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private boolean agrupar;
	private List<GrupDto> grups;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;
	private String organGestor;
	private String organGestorNom;
	private String tipusAssumpte;
	private String tipusAssumpteNom;
	private String codiAssumpte;
	private String codiAssumpteNom;
	private boolean comu;
	private Date ultimaActualitzacio;
//
//	private List<PermisDto> permisos;
//	private boolean usuariActualRead;
//	private boolean usuariActualProcessar;
//	private boolean usuariActualNotificacio;
//	private boolean usuariActualAdministration;
//
//	private int retard;
//	private int caducitat;
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getDescripcio() {
		return codi + " - " + nom;
	}


//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ProcedimentSimpleDto other = (ProcedimentSimpleDto)obj;
//		if (codi == null) {
//			if (other.codi != null)
//				return false;
//		} else if (!codi.equals(other.codi))
//			return false;
//		return true;
//	}
//
//
//	private static final long serialVersionUID = 6058789232924135932L;

}
