package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrganismeDto implements Serializable{

	private String codi;
	private String nom;
	private String pare;
	private List<String> fills = new ArrayList<String>();
	
	private static final long serialVersionUID = -3831959843313056718L;
	
}
