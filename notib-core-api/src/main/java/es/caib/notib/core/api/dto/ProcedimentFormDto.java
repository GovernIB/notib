package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProcedimentFormDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String entitatNom;
	private String organGestor;
	private String organGestorNom;
	private String pagadorpostal;
	private String pagadorcie;
	private boolean agrupar;
	private Integer retard;
	private List<PermisDto> permisos;
	private List<GrupDto> grups;
	
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	public int getGrupsCount() {
		if  (grups == null)
			return 0;
		else
			return grups.size();
	}
	
	public String getOrganGestorDesc() {
		return organGestor + " - " + organGestorNom!=null?organGestorNom:"";
	}


	private static final long serialVersionUID = 6058789232924135932L;

}
