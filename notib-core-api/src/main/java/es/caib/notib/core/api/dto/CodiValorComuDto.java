package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CodiValorComuDto implements Serializable {

	private String codi;
	private String valor;
	private boolean comu;

	private static final long serialVersionUID = 1;
}