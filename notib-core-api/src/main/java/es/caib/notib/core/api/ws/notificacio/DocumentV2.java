/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del document que s'envia amb la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@JsonAutoDetect
public class DocumentV2 {
	
	private String arxiuId;
	private String arxiuNom;
	private String contingutBase64;
	private String url;
	private String uuid;
	private String csv;
	private boolean normalitzat;
	//	private Map<String, String> metadades;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;

	public boolean isEmpty() {
		return ((arxiuNom == null || arxiuNom.isEmpty()) &&
				(contingutBase64 == null || contingutBase64.isEmpty()) &&
				(url == null || url.isEmpty()) &&
				(uuid == null || uuid.isEmpty()) &&
				(csv == null || csv.isEmpty()));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
