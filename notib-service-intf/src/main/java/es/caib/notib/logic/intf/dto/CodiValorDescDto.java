package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class CodiValorDescDto implements Serializable {

	private String codi;
	private String valor;
	private String desc;

	private static final long serialVersionUID = 1;
}