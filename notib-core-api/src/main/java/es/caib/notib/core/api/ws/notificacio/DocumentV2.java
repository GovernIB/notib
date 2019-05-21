/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació del document que s'envia amb la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class DocumentV2 {
	
	private String arxiuId;
	private String arxiuNom;
	private String contingutBase64;
	private String url;
//	private Map<String, String> metadades;
	private boolean normalitzat;
//	private boolean generarCsv;
	private String uuid;
	private String csv;
	
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getContingutBase64() {
		return contingutBase64;
	}
	public void setContingutBase64(String contingutBase64) {
		this.contingutBase64 = contingutBase64;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isNormalitzat() {
		return normalitzat;
	}
	public void setNormalitzat(boolean normalitzat) {
		this.normalitzat = normalitzat;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getArxiuId() {
		return arxiuId;
	}
	public void setArxiuId(String arxiuId) {
		this.arxiuId = arxiuId;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
