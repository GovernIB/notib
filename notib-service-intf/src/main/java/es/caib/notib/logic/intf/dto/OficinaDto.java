package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OficinaDto implements Serializable{

	private String codi;
	private String nom;
	private String organCodi;
	private boolean sir;

	private static final long serialVersionUID = -3831959843313056718L;
	
}
