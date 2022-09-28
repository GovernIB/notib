package es.caib.notib.logic.intf.dto.cie;

import java.io.Serializable;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CieFormatSobreDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private Long pagadorCieId;	

	private static final long serialVersionUID = -2057306471713763412L;
}
