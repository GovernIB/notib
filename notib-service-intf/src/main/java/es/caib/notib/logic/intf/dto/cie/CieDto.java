package es.caib.notib.logic.intf.dto.cie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class CieDto extends CieDataDto{
	private Long entitatId;
}
