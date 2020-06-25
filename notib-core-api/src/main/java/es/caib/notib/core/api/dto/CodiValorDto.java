package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CodiValorDto implements Serializable {

	private String codi;
	private String valor;

	private static final long serialVersionUID = 1;
}
