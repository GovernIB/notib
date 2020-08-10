package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class GrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private Long entitatId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
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
