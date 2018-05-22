/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació sobre els paràmetres necessaris per a enviar
 * la notificació a la seu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class ParametresSeu {

	private String expedientSerieDocumental;
	private String expedientUnitatOrganitzativa;
	private String expedientIdentificadorEni;
	private String expedientTitol;
	private String procedimentCodi;
	private String registreOrgan;
	private String registreOficina;
	private String registreLlibre;
	private String idioma;
	private String avisTitol;
	private String avisText;
	private String avisTextMobil;
	private String oficiTitol;
	private String oficiText;

	public String getExpedientSerieDocumental() {
		return expedientSerieDocumental;
	}
	public void setExpedientSerieDocumental(String expedientSerieDocumental) {
		this.expedientSerieDocumental = expedientSerieDocumental;
	}
	public String getExpedientUnitatOrganitzativa() {
		return expedientUnitatOrganitzativa;
	}
	public void setExpedientUnitatOrganitzativa(String expedientUnitatOrganitzativa) {
		this.expedientUnitatOrganitzativa = expedientUnitatOrganitzativa;
	}
	public String getExpedientIdentificadorEni() {
		return expedientIdentificadorEni;
	}
	public void setExpedientIdentificadorEni(String expedientIdentificadorEni) {
		this.expedientIdentificadorEni = expedientIdentificadorEni;
	}
	public String getExpedientTitol() {
		return expedientTitol;
	}
	public void setExpedientTitol(String expedientTitol) {
		this.expedientTitol = expedientTitol;
	}
	public String getRegistreOficina() {
		return registreOficina;
	}
	public void setRegistreOficina(String registreOficina) {
		this.registreOficina = registreOficina;
	}
	public String getRegistreLlibre() {
		return registreLlibre;
	}
	public void setRegistreLlibre(String registreLlibre) {
		this.registreLlibre = registreLlibre;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getAvisTitol() {
		return avisTitol;
	}
	public void setAvisTitol(String avisTitol) {
		this.avisTitol = avisTitol;
	}
	public String getAvisText() {
		return avisText;
	}
	public void setAvisText(String avisText) {
		this.avisText = avisText;
	}
	public String getAvisTextMobil() {
		return avisTextMobil;
	}
	public void setAvisTextMobil(String avisTextMobil) {
		this.avisTextMobil = avisTextMobil;
	}
	public String getOficiTitol() {
		return oficiTitol;
	}
	public void setOficiTitol(String oficiTitol) {
		this.oficiTitol = oficiTitol;
	}
	public String getOficiText() {
		return oficiText;
	}
	public void setOficiText(String oficiText) {
		this.oficiText = oficiText;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getRegistreOrgan() {
		return registreOrgan;
	}
	public void setRegistreOrgan(String registreOrgan) {
		this.registreOrgan = registreOrgan;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
