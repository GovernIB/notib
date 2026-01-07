package es.caib.notib.logic.intf.dto;

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
@Getter @Setter
public class CodiValorOrganGestorComuDto implements Serializable {

	@EqualsAndHashCode.Exclude
	private Long id;
	private String codi;
	@EqualsAndHashCode.Exclude
	private String valor;
	private String organGestor;
	private String organNom;
	@EqualsAndHashCode.Exclude
	private boolean comu;

	private static final long serialVersionUID = 1;
}