package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter @Setter
public class CodiValorOrganGestorComuDto implements Serializable {

	@EqualsAndHashCode.Exclude
	private Long id;
	private String codi;
	@EqualsAndHashCode.Exclude
	private String valor;
	private String organGestor;
	@EqualsAndHashCode.Exclude
	private boolean comu;

	private static final long serialVersionUID = 1;
}