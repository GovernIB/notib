/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	private String uuid;
	private String csv;
	private boolean normalitzat;
	//	private Map<String, String> metadades;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;

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
	public OrigenEnum getOrigen() {
		return origen;
	}
	public void setOrigen(OrigenEnum origen) {
		this.origen = origen;
	}
	public ValidesaEnum getValidesa() {
		return validesa;
	}
	public void setValidesa(ValidesaEnum validesa) {
		this.validesa = validesa;
	}
	public TipusDocumentalEnum getTipoDocumental() {
		return tipoDocumental;
	}
	public void setTipoDocumental(TipusDocumentalEnum tipoDocumental) {
		this.tipoDocumental = tipoDocumental;
	}
	public Boolean getModoFirma() {
		return modoFirma;
	}
	public void setModoFirma(Boolean modoFirma) {
		this.modoFirma = modoFirma;
	}

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
