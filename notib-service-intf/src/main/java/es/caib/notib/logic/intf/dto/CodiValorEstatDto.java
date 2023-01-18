package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CodiValorEstatDto implements Serializable {

	private Long id;
	private String codi;
	private String valor;
	private OrganGestorEstatEnum estat;

	private static final long serialVersionUID = 1;
}
