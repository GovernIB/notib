package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CodiValorOrganGestorComuDto implements Serializable {

	private String codi;
	private String valor;
	private String organGestor;
	private boolean comu;

	private static final long serialVersionUID = 1;
}