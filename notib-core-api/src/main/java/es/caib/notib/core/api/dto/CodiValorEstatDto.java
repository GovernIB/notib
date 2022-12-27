package es.caib.notib.core.api.dto;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CodiValorEstatDto implements Serializable {

	private Long id;
	private String codi;
	private String valor;
	private OrganGestorEstatEnum estat;

	private static final long serialVersionUID = 1;
}
