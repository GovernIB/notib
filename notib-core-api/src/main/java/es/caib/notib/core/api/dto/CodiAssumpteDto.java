package es.caib.notib.core.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CodiAssumpteDto implements Serializable {

	private String codi;
	private String nom;
	private String tipusAssumpte;

	private static final long serialVersionUID = -8835856793759657155L;
}
