/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;


/**
 * Informació d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@XmlRootElement
public class Notificacio {

	private String cifEntitat;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private Date enviamentDataProgramada;
	private String concepte;
	private String pagadorCorreusCodiDir3;
	private String pagadorCorreusContracteNum;
	private String pagadorCorreusCodiClientFacturacio;
	private Date pagadorCorreusDataVigencia;
	private String pagadorCieCodiDir3;
	private Date pagadorCieDataVigencia;
	private String procedimentCodiSia;
	private String procedimentDescripcioSia;
	private String documentArxiuNom;
	private String documentContingutBase64;
	private String documentSha1;
	private boolean documentNormalitzat;
	private boolean documentGenerarCsv;
	private String seuExpedientSerieDocumental;
	private String seuExpedientUnitatOrganitzativa;
	private String seuExpedientIdentificadorEni;
	private String seuExpedientTitol;
	private String seuRegistreOficina;
	private String seuRegistreLlibre;
	private String seuIdioma;
	private String seuAvisTitol;
	private String seuAvisText;
	private String seuAvisTextMobil;
	private String seuOficiTitol;
	private String seuOficiText;
	private boolean registreEnviar;
	private String registreOficina;
	private String registreLlibre;
	private String registreTipusAssumpte;
	private String registreCodiAssumpte;
	private String registreIdioma;
	private String registreTipusDocumental;
	private int registreOrigenCiutadaAdmin;
	private Date registreDataCaptura;
	private String documentacioFisicaCodi;

	private NotificacioEstatEnumDto estat;
	private List<NotificacioDestinatari> destinataris;

	private boolean error;
	private Date errorEventData;
	private String errorEventDescripcio;
	private String errorEventError;

	public String getCifEntitat() {
		return cifEntitat;
	}
	public void setCifEntitat(String cifEntitat) {
		this.cifEntitat = cifEntitat;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getPagadorCorreusCodiDir3() {
		return pagadorCorreusCodiDir3;
	}
	public void setPagadorCorreusCodiDir3(String pagadorCorreusCodiDir3) {
		this.pagadorCorreusCodiDir3 = pagadorCorreusCodiDir3;
	}
	public String getPagadorCorreusContracteNum() {
		return pagadorCorreusContracteNum;
	}
	public void setPagadorCorreusContracteNum(String pagadorCorreusContracteNum) {
		this.pagadorCorreusContracteNum = pagadorCorreusContracteNum;
	}
	public String getPagadorCorreusCodiClientFacturacio() {
		return pagadorCorreusCodiClientFacturacio;
	}
	public void setPagadorCorreusCodiClientFacturacio(String pagadorCorreusCodiClientFacturacio) {
		this.pagadorCorreusCodiClientFacturacio = pagadorCorreusCodiClientFacturacio;
	}
	public Date getPagadorCorreusDataVigencia() {
		return pagadorCorreusDataVigencia;
	}
	public void setPagadorCorreusDataVigencia(Date pagadorCorreusDataVigencia) {
		this.pagadorCorreusDataVigencia = pagadorCorreusDataVigencia;
	}
	public String getPagadorCieCodiDir3() {
		return pagadorCieCodiDir3;
	}
	public void setPagadorCieCodiDir3(String pagadorCieCodiDir3) {
		this.pagadorCieCodiDir3 = pagadorCieCodiDir3;
	}
	public Date getPagadorCieDataVigencia() {
		return pagadorCieDataVigencia;
	}
	public void setPagadorCieDataVigencia(Date pagadorCieDataVigencia) {
		this.pagadorCieDataVigencia = pagadorCieDataVigencia;
	}
	public String getProcedimentCodiSia() {
		return procedimentCodiSia;
	}
	public void setProcedimentCodiSia(String procedimentCodiSia) {
		this.procedimentCodiSia = procedimentCodiSia;
	}
	public String getProcedimentDescripcioSia() {
		return procedimentDescripcioSia;
	}
	public void setProcedimentDescripcioSia(String procedimentDescripcioSia) {
		this.procedimentDescripcioSia = procedimentDescripcioSia;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public void setDocumentArxiuNom(String documentArxiuNom) {
		this.documentArxiuNom = documentArxiuNom;
	}
	public String getDocumentContingutBase64() {
		return documentContingutBase64;
	}
	public void setDocumentContingutBase64(String documentContingutBase64) {
		this.documentContingutBase64 = documentContingutBase64;
	}
	public String getDocumentSha1() {
		return documentSha1;
	}
	public void setDocumentSha1(String documentSha1) {
		this.documentSha1 = documentSha1;
	}
	public boolean isDocumentNormalitzat() {
		return documentNormalitzat;
	}
	public void setDocumentNormalitzat(boolean documentNormalitzat) {
		this.documentNormalitzat = documentNormalitzat;
	}
	public boolean isDocumentGenerarCsv() {
		return documentGenerarCsv;
	}
	public void setDocumentGenerarCsv(boolean documentGenerarCsv) {
		this.documentGenerarCsv = documentGenerarCsv;
	}
	public String getSeuExpedientSerieDocumental() {
		return seuExpedientSerieDocumental;
	}
	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
		this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
	}
	public String getSeuExpedientUnitatOrganitzativa() {
		return seuExpedientUnitatOrganitzativa;
	}
	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
	}
	public String getSeuExpedientIdentificadorEni() {
		return seuExpedientIdentificadorEni;
	}
	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
		this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
	}
	public String getSeuExpedientTitol() {
		return seuExpedientTitol;
	}
	public void setSeuExpedientTitol(String seuExpedientTitol) {
		this.seuExpedientTitol = seuExpedientTitol;
	}
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public void setSeuRegistreOficina(String seuRegistreOficina) {
		this.seuRegistreOficina = seuRegistreOficina;
	}
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
		this.seuRegistreLlibre = seuRegistreLlibre;
	}
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public List<NotificacioDestinatari> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<NotificacioDestinatari> destinataris) {
		this.destinataris = destinataris;
	}
	
	public boolean isRegistreEnviar() {
		return registreEnviar;
	}
	public void setRegistreEnviar(boolean registreEnviar) {
		this.registreEnviar = registreEnviar;
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
	
	public String getRegistreTipusAssumpte() {
		return registreTipusAssumpte;
	}
	public void setRegistreTipusAssumpte(String registreTipusAssumpte) {
		this.registreTipusAssumpte = registreTipusAssumpte;
	}
	
	public String getRegistreCodiAssumpte() {
		return registreCodiAssumpte;
	}
	public void setRegistreCodiAssumpte(String registreCodiAssumpte) {
		this.registreCodiAssumpte = registreCodiAssumpte;
	}
	
	public String getRegistreIdioma() {
		return registreIdioma;
	}
	public void setRegistreIdioma(String registreIdioma) {
		this.registreIdioma = registreIdioma;
	}
	
	public String getRegistreTipusDocumental() {
		return registreTipusDocumental;
	}
	public void setRegistreTipusDocumental(String registreTipusDocumental) {
		this.registreTipusDocumental = registreTipusDocumental;
	}
	
	public int getRegistreOrigenCiutadaAdmin() {
		return registreOrigenCiutadaAdmin;
	}
	public void setRegistreOrigenCiutadaAdmin(int registreOrigenCiutadaAdmin) {
		this.registreOrigenCiutadaAdmin = registreOrigenCiutadaAdmin;
	}
	
	public Date getRegistreDataCaptura() {
		return registreDataCaptura;
	}
	public void setRegistreDataCaptura(Date registreDataCaptura) {
		this.registreDataCaptura = registreDataCaptura;
	}
	
	public String getDocumentacioFisicaCodi() {
		return documentacioFisicaCodi;
	}
	public void setDocumentacioFisicaCodi(String documentacioFisicaCodi) {
		this.documentacioFisicaCodi = documentacioFisicaCodi;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public Date getErrorEventData() {
		return errorEventData;
	}
	public void setErrorEventData(Date errorEventData) {
		this.errorEventData = errorEventData;
	}
	public String getErrorEventDescripcio() {
		return errorEventDescripcio;
	}
	public void setErrorEventDescripcio(String errorEventDescripcio) {
		this.errorEventDescripcio = errorEventDescripcio;
	}
	public String getErrorEventError() {
		return errorEventError;
	}
	public void setErrorEventError(String errorEventError) {
		this.errorEventError = errorEventError;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
