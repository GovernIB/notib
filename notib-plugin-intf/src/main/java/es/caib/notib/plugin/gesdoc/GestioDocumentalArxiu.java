/**
 * 
 */
package es.caib.notib.plugin.gesdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Arxiu emmagatzemat a dins la gestió documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class GestioDocumentalArxiu {

	private String fileName;
	private byte[] content;

	public GestioDocumentalArxiu(String fileName, byte[] content) {

		super();
		this.fileName = fileName;
		this.content = content;
	}

}
