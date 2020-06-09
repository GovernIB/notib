package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter @AllArgsConstructor @ToString(includeFieldNames = true)
public class AccioParam {

	String codi;
	String valor;
	
}
