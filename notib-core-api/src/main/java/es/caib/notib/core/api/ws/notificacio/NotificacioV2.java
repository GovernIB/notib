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
	private String numExpedient;
	private String refExterna;
	private String idioma;
	private String observacions;
	private TipusAssumpteEnumDto tipusAssumpte;
	private List<Enviament> enviaments;
	private String extracte;
	
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
	public TipusAssumpteEnumDto getTipusAssumpte() {
		return tipusAssumpte;
	}
	public void setTipusAssumpte(TipusAssumpteEnumDto tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
