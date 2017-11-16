/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació sobre la certificació d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class Certificacio {

	private Date data;
	private String origen;
	private String contingutBase64;
	private int tamany;
	private String hash;
	private String metadades;
	private String csv;
	private String tipusMime;

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getContingutBase64() {
		return contingutBase64;
	}
	public void setContingutBase64(String contingutBase64) {
		this.contingutBase64 = contingutBase64;
	}
	public int getTamany() {
		return tamany;
	}
	public void setTamany(int tamany) {
		this.tamany = tamany;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getMetadades() {
		return metadades;
	}
	public void setMetadades(String metadades) {
		this.metadades = metadades;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}

}
