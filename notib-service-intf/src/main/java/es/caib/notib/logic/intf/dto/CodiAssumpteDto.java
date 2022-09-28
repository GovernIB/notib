package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CodiAssumpteDto implements Serializable {

	private String codi;
	private String nom;
	private String tipusAssumpte;

	private static final long serialVersionUID = -8835856793759657155L;
}
