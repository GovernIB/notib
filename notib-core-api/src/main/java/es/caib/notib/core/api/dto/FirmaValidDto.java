/**
 * 
 */
package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FirmaValidDto {

	String arxiuGestdocId;
	String nom;
	String mediaType;
	Long mida;
	private boolean signed;
	private boolean error = false;
	private String errorMsg = null;
	
}
