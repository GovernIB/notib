package es.caib.notib.core.api.dto.servei;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.GrupDto;

import java.io.Serializable;

public class ServeiGrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private ServeiDto procediment;
	private GrupDto grup;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ServeiDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ServeiDto procediment) {
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
