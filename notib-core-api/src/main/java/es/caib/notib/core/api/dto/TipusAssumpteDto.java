package es.caib.notib.core.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TipusAssumpteDto implements Serializable{

	private String codi;
	private String nom;

	private static final long serialVersionUID = -3831959843313056718L;
}
