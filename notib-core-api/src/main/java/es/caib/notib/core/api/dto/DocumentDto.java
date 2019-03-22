package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class DocumentDto implements Serializable {

	private String id;
	private String arxiuGestdocId;
	private String arxiuNom;
	private String contingutBase64;
	private String hash;
	private String url;
	private String metadades;
	private boolean normalitzat;
	private boolean generarCsv;
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
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMetadades() {
		return metadades;
	}
	public void setMetadades(String metadades) {
		this.metadades = metadades;
	}
	public boolean isNormalitzat() {
		return normalitzat;
	}
	public void setNormalitzat(boolean normalitzat) {
		this.normalitzat = normalitzat;
	}
	public boolean isGenerarCsv() {
		return generarCsv;
	}
	public void setGenerarCsv(boolean generarCsv) {
		this.generarCsv = generarCsv;
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
	public String getArxiuGestdocId() {
		return arxiuGestdocId;
	}
	public void setArxiuGestdocId(String arxiuGestdocId) {
		this.arxiuGestdocId = arxiuGestdocId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private static final long serialVersionUID = 299966599434094856L;

}
