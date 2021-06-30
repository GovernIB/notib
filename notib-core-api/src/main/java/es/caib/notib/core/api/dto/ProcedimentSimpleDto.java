package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class ProcedimentSimpleDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private boolean agrupar;
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
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getDescripcio() {
		return codi + " - " + nom;
	}

}
