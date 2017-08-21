package es.caib.notib.plugin.registre.sortida;

import java.util.Date;

public class DocumentRegistre {
	
	private String titol;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private int arxiuMida;
	private String tipusMIMEFitxerAnexat;
	private String tipusDocumental;
	private int origenCiutadaAdmin;
	private Date dataCaptura;	
	
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	
	public int getArxiuMida() {
		return arxiuMida;
	}
	public void setArxiuMida(int arxiuMida) {
		this.arxiuMida = arxiuMida;
	}
	
	public String getTipusMIMEFitxerAnexat() {
		return tipusMIMEFitxerAnexat;
	}
	public void setTipusMIMEFitxerAnexat(String tipusMIMEFitxerAnexat) {
		this.tipusMIMEFitxerAnexat = tipusMIMEFitxerAnexat;
	}
	
	public String getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	
	public int getOrigenCiutadaAdmin() {
		return origenCiutadaAdmin;
	}
	public void setOrigenCiutadaAdmin(int origenCiutadaAdmin) {
		this.origenCiutadaAdmin = origenCiutadaAdmin;
	}
	
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	
	
}
