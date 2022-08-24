package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
@Getter @Setter
@Builder
public class CodiValorDto implements Serializable {

	private String codi;
	private String valor;

	private static final long serialVersionUID = 1;
}
