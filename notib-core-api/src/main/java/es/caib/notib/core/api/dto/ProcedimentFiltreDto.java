package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentFiltreDto extends AuditoriaDto implements Serializable {
	
	
	private String id;
	private String codi;
	private String nom;
	private String codisia;
	private Long entitatId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getCodisia() {
		return codisia;
	}
	public void setCodisia(String codisia) {
		this.codisia = codisia;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	
	
	private static final long serialVersionUID = -2393511650074099319L;
}
