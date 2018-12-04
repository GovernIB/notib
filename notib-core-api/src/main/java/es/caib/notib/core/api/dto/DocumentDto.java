package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class DocumentDto implements Serializable{

	
	private String arxiuNom;
	private byte[] contingutBase64;
	private String hash;
	private String url;
	private String metadades;
	private boolean normalitzat;
	private boolean generarCsv;
	private String uUID;
	private String cSV;
	
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public byte[] getContingutBase64() {
		return contingutBase64;
	}
	public void setContingutBase64(byte[] contingutBase64) {
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
	public String getUUID() {
		return uUID;
	}
	public void setUUID(String uUID) {
		this.uUID = uUID;
	}
	public String getCSV() {
		return cSV;
	}
	public void setCSV(String cSV) {
		this.cSV = cSV;
	}
	
	private static final long serialVersionUID = 299966599434094856L;

}
