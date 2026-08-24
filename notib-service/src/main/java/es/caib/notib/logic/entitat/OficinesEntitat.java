package es.caib.notib.logic.entitat;

import es.caib.notib.logic.intf.dto.OficinaDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
public class OficinesEntitat implements Serializable {

	private List<OficinaDto> oficines;
}
