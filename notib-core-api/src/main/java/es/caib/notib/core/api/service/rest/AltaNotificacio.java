package es.caib.notib.core.api.service.rest;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class AltaNotificacio {
	
//	private Long id;
	
	private String nifEntitat;
	private NotificaEnviamentTipus enviamentTipus;
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
	private String documentArxiuId;
	private String documentContingutBase64;
	private String documentSha1;
	
	private boolean documentNormalitzat;
	private boolean documentGenerarCsv;
	private boolean certificada;
	
	private List<NotificacioDestinatari> destinataris;
	
	public AltaNotificacio(){}
	
	public AltaNotificacio(
			String nifEntitat,
			NotificaEnviamentTipus enviamentTipus,
			Date enviamentDataProgramada,
			String concepte,
			String pagadorCorreusCodiDir3,
			String pagadorCorreusContracteNum,
			String pagadorCorreusCodiClientFacturacio,
			Date pagadorCorreusDataVigencia,
			String pagadorCieCodiDir3,
			Date pagadorCieDataVigencia,
			String procedimentCodiSia,
			String procedimentDescripcioSia,
			String documentArxiuId,
			String documentContingutBase64,
			String documentSha1,
			boolean documentNormalitzat,
			boolean documentGenerarCsv,
			boolean certificada,
			List<NotificacioDestinatari> destinataris ) {
	
		super();
		this.nifEntitat = nifEntitat;
		this.enviamentTipus = enviamentTipus;
		this.enviamentDataProgramada = enviamentDataProgramada;
		this.concepte = concepte;
		this.pagadorCorreusCodiDir3 = pagadorCorreusCodiDir3;
		this.pagadorCorreusContracteNum = pagadorCorreusContracteNum;
		this.pagadorCorreusCodiClientFacturacio = pagadorCorreusCodiClientFacturacio;
		this.pagadorCorreusDataVigencia = pagadorCorreusDataVigencia;
		this.pagadorCieCodiDir3 = pagadorCieCodiDir3;
		this.pagadorCieDataVigencia = pagadorCieDataVigencia;
		this.procedimentCodiSia = procedimentCodiSia;
		this.procedimentDescripcioSia = procedimentDescripcioSia;
		this.documentArxiuId = documentArxiuId;
		this.documentContingutBase64 = documentContingutBase64;
		this.documentSha1 = documentSha1;
		this.documentNormalitzat = documentNormalitzat;
		this.documentGenerarCsv = documentGenerarCsv;
		this.certificada = certificada;
		this.destinataris = destinataris;
	}
	

	public String getNifEntitat() {
		return nifEntitat;
	}

	public void setNifEntitat(String nifEntitat) {
		this.nifEntitat = nifEntitat;
	}

	public NotificaEnviamentTipus getEnviamentTipus() {
		return enviamentTipus;
	}

	public void setEnviamentTipus(NotificaEnviamentTipus enviamentTipus) {
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

	public String getDocumentArxiuId() {
		return documentArxiuId;
	}

	public void setDocumentArxiuId(String documentArxiuId) {
		this.documentArxiuId = documentArxiuId;
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

	public boolean isCertificada() {
		return certificada;
	}

	public void setCertificada(boolean certificada) {
		this.certificada = certificada;
	}

	public List<NotificacioDestinatari> getDestinataris() {
		return destinataris;
	}

	public void setDestinataris(List<NotificacioDestinatari> destinataris) {
		this.destinataris = destinataris;
	}


}
