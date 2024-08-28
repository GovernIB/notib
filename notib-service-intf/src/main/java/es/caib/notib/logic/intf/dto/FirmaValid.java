/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FirmaValid {

	private String arxiuGestdocId;
	private String nom;
	private String mediaType;
	private Long mida;
	private boolean signed;
	private boolean error = false;
	private String errorMsg = null;
	
}
