/**
 * 
 */
package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArxiuDto implements Serializable {

	private String nom;
	private String contentType;
	private byte[] contingut;
	private long tamany;

	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
		if (contingut != null)
			this.tamany = contingut.length;
		else
			this.tamany = 0;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
