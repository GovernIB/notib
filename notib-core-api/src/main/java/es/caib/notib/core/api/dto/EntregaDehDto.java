package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntregaDehDto implements Serializable{

	
	private boolean obligat;
	private String procedimentCodi;
	private String emisorNif;
	private boolean activa;

	private static final long serialVersionUID = 5160556424872017273L;
}
