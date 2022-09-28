package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class GrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	@EqualsAndHashCode.Include
	private String codi;
	private String nom;
	private Long entitatId;
	private Long organGestorId;
	private String organGestorCodi;
	
	public String getNomIRol() {
		return nom + " (" + codi + ")";
	}
	
	private static final long serialVersionUID = 7999677809220395478L;

}
