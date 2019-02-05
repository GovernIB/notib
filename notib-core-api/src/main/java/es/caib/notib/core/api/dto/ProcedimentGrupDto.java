package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class ProcedimentGrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private ProcedimentDto procediment;
	private GrupDto grup;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public GrupDto getGrup() {
		return grup;
	}
	public void setGrup(GrupDto grup) {
		this.grup = grup;
	}


	private static final long serialVersionUID = 7999677809220395478L;

}
