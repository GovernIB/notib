package es.caib.notib.core.api.dto.procediment;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.GrupDto;

import java.io.Serializable;

public class ProcSerGrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private ProcSerDto procSer;
	private GrupDto grup;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProcSerDto getProcSer() {
		return procSer;
	}
	public void setProcSer(ProcSerDto procSer) {
		this.procSer = procSer;
	}
	public GrupDto getGrup() {
		return grup;
	}
	public void setGrup(GrupDto grup) {
		this.grup = grup;
	}


	private static final long serialVersionUID = 7999677809220395478L;

}
