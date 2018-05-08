/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@XmlRootElement
public class RespostaConsultaEstatEnviament {

	private String concepte;
	private String descripcio;
	private String emisorDir3Codi;
	private String emisorDir3Descripcio;
	private String emisorArrelDir3Codi;
	private String emisorArrelDir3Descripcio;
	private String destiDir3Codi;
	private String destiDir3Descripcio;
	private EnviamentTipusEnum enviamentTipus;
	private Date dataCreacio;
	private Date dataPostaDisposicio;
	private Date dataCaducitat;
	private Integer retard;
	private String procedimentCodi;
	private String procedimentDescripcio;
	private String referencia;
	private Persona titular;
	private List<Persona> destinataris;
	private EntregaPostal entregaPostal;
	private EntregaDeh entregaDeh;
	private Certificacio certificacio;
	private EnviamentEstatEnum estat;
	private Date estatData;

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
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public String getEmisorDir3Descripcio() {
		return emisorDir3Descripcio;
	}
	public void setEmisorDir3Descripcio(String emisorDir3Descripcio) {
		this.emisorDir3Descripcio = emisorDir3Descripcio;
	}
	public String getEmisorArrelDir3Codi() {
		return emisorArrelDir3Codi;
	}
	public void setEmisorArrelDir3Codi(String emisorArrelDir3Codi) {
		this.emisorArrelDir3Codi = emisorArrelDir3Codi;
	}
	public String getEmisorArrelDir3Descripcio() {
		return emisorArrelDir3Descripcio;
	}
	public void setEmisorArrelDir3Descripcio(String emisorArrelDir3Descripcio) {
		this.emisorArrelDir3Descripcio = emisorArrelDir3Descripcio;
	}
	public String getDestiDir3Codi() {
		return destiDir3Codi;
	}
	public void setDestiDir3Codi(String destiDir3Codi) {
		this.destiDir3Codi = destiDir3Codi;
	}
	public String getDestiDir3Descripcio() {
		return destiDir3Descripcio;
	}
	public void setDestiDir3Descripcio(String destiDir3Descripcio) {
		this.destiDir3Descripcio = destiDir3Descripcio;
	}
	public EnviamentTipusEnum getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipusEnum enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataPostaDisposicio() {
		return dataPostaDisposicio;
	}
	public void setDataPostaDisposicio(Date dataPostaDisposicio) {
		this.dataPostaDisposicio = dataPostaDisposicio;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentDescripcio() {
		return procedimentDescripcio;
	}
	public void setProcedimentDescripcio(String procedimentDescripcio) {
		this.procedimentDescripcio = procedimentDescripcio;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public Persona getTitular() {
		return titular;
	}
	public void setTitular(Persona titular) {
		this.titular = titular;
	}
	public List<Persona> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<Persona> destinataris) {
		this.destinataris = destinataris;
	}
	public EntregaPostal getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregaPostal entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDeh getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDeh entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public Certificacio getCertificacio() {
		return certificacio;
	}
	public void setCertificacio(Certificacio certificacio) {
		this.certificacio = certificacio;
	}
	public EnviamentEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(EnviamentEstatEnum estat) {
		this.estat = estat;
	}
	public Date getEstatData() {
		return estatData;
	}
	public void setEstatData(Date estatData) {
		this.estatData = estatData;
	}

}
