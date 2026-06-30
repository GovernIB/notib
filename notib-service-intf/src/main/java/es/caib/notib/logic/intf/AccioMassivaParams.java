package es.caib.notib.logic.intf;

import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AccioMassivaParams implements Serializable {

	private List<Long> ids;
	private SeleccioTipus seleccioTipus;

	public boolean idsEmpty() {
		return ids == null || ids.isEmpty();
	}
}
