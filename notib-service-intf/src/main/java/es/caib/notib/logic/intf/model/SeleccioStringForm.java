package es.caib.notib.logic.intf.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SeleccioStringForm implements Serializable {

		@NotNull
		@NotEmpty
		private List<String> ids;
		private boolean massivo = false;
}
