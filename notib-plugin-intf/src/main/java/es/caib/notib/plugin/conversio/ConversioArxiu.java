/**
 * 
 */
package es.caib.notib.plugin.conversio;

import lombok.Getter;
import lombok.Setter;

/**
 * Arxiu per al plugin de conversió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ConversioArxiu {

	private String arxiuNom;
	private byte[] arxiuContingut;

	public ConversioArxiu() {}

	public ConversioArxiu(String arxiuNom, byte[] arxiuContingut) {

		super();
		this.arxiuNom = arxiuNom;
		this.arxiuContingut = arxiuContingut;
	}

	public String getArxiuExtensio() {

		var index = arxiuNom.lastIndexOf(".");
		return index != -1 ? arxiuNom.substring(index + 1) : "";
	}

}
