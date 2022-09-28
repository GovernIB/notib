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
public class TipusAssumpteDto implements Serializable{

	private String codi;
	private String nom;

	private static final long serialVersionUID = -3831959843313056718L;
}
