package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OficinaDto implements Serializable{

	private String codi;
	private String nom;

	private static final long serialVersionUID = -3831959843313056718L;
	
}
