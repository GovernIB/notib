package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GrupFiltreDto implements Serializable{

	private String codi;
	private String nom;
	private Long organGestorId;
	

	private static final long serialVersionUID = 2436397125477145283L;
}
