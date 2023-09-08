package es.caib.notib.logic.intf.dto.cie;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class CieFormatFullaDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private Long pagadorCieId;	

	private static final long serialVersionUID = 4814433005549236274L;
}
