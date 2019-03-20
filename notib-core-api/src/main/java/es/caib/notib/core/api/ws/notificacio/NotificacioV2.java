/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.TipusAssumpteEnumDto;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@XmlRootElement
public class NotificacioV2 {

	private String emisorDir3Codi;
	private ComunicacioTipusEnum comunicacioTipus;
	private EnviamentTipusEnum enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private DocumentV2 document;
	private String codiUsuari;
	private String codiProcediment;
	private String codiGrup;
	private String registreOficina;
	private String registreLlibre;
	private String registreNumero;
	private Date registreData;
	private String extracte;
	private String docFisica;
	private String idioma;
	private String tipusAssumpte;
	private String numExpedient;
	private String codiAssumpte;
	private String refExterna;
	private String observacions;
	private List<Enviament> enviaments;
	
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public ComunicacioTipusEnum getComunicacioTipus() {
		return comunicacioTipus;
	}
	public void setComunicacioTipus(ComunicacioTipusEnum comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}
	public EnviamentTipusEnum getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipusEnum enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public DocumentV2 getDocument() {
		return document;
	}
	public void setDocument(DocumentV2 document) {
		this.document = document;
	}
	public List<Enviament> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<Enviament> enviaments) {
		this.enviaments = enviaments;
	}
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public String getCodiGrup() {
		return codiGrup;
	}
	public void setCodiGrup(String codiGrup) {
		this.codiGrup = codiGrup;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public void setNumExpedient(String numExpedient) {
		this.numExpedient = numExpedient;
	}
	public String getRefExterna() {
		return refExterna;
	}
	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
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
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public String getDocFisica() {
		return docFisica;
	}
	public void setDocFisica(String docFisica) {
		this.docFisica = docFisica;
	}
	public String getTipusAssumpte() {
		return tipusAssumpte;
	}
	public void setTipusAssumpte(String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
