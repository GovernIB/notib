package es.caib.notib.core.api.dto.cie;

import es.caib.notib.core.api.dto.AuditoriaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CieFormatFullaDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private Long pagadorCieId;	

	private static final long serialVersionUID = 4814433005549236274L;
}
