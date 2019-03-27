package es.caib.notib.core.api.dto;

import javax.xml.datatype.XMLGregorianCalendar;

public class AnexoWsDto {
	protected String titulo;
    protected String nombreFicheroAnexado;
    protected byte[] ficheroAnexado;
    protected String tipoMIMEFicheroAnexado;
    protected String tipoDocumental;
    protected String validezDocumento;
    protected String tipoDocumento;
    protected String observaciones;
    protected Integer origenCiudadanoAdmin;
    protected XMLGregorianCalendar fechaCaptura;
    protected Integer modoFirma;
    protected String nombreFirmaAnexada;
    protected byte[] firmaAnexada;
    protected String tipoMIMEFirmaAnexada;
    protected String csv;
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getNombreFicheroAnexado() {
		return nombreFicheroAnexado;
	}
	public void setNombreFicheroAnexado(String nombreFicheroAnexado) {
		this.nombreFicheroAnexado = nombreFicheroAnexado;
	}
	public byte[] getFicheroAnexado() {
		return ficheroAnexado;
	}
	public void setFicheroAnexado(byte[] ficheroAnexado) {
		this.ficheroAnexado = ficheroAnexado;
	}
	public String getTipoMIMEFicheroAnexado() {
		return tipoMIMEFicheroAnexado;
	}
	public void setTipoMIMEFicheroAnexado(String tipoMIMEFicheroAnexado) {
		this.tipoMIMEFicheroAnexado = tipoMIMEFicheroAnexado;
	}
	public String getTipoDocumental() {
		return tipoDocumental;
	}
	public void setTipoDocumental(String tipoDocumental) {
		this.tipoDocumental = tipoDocumental;
	}
	public String getValidezDocumento() {
		return validezDocumento;
	}
	public void setValidezDocumento(String validezDocumento) {
		this.validezDocumento = validezDocumento;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Integer getOrigenCiudadanoAdmin() {
		return origenCiudadanoAdmin;
	}
	public void setOrigenCiudadanoAdmin(Integer origenCiudadanoAdmin) {
		this.origenCiudadanoAdmin = origenCiudadanoAdmin;
	}
	public XMLGregorianCalendar getFechaCaptura() {
		return fechaCaptura;
	}
	public void setFechaCaptura(XMLGregorianCalendar fechaCaptura) {
		this.fechaCaptura = fechaCaptura;
	}
	public Integer getModoFirma() {
		return modoFirma;
	}
	public void setModoFirma(Integer modoFirma) {
		this.modoFirma = modoFirma;
	}
	public String getNombreFirmaAnexada() {
		return nombreFirmaAnexada;
	}
	public void setNombreFirmaAnexada(String nombreFirmaAnexada) {
		this.nombreFirmaAnexada = nombreFirmaAnexada;
	}
	public byte[] getFirmaAnexada() {
		return firmaAnexada;
	}
	public void setFirmaAnexada(byte[] firmaAnexada) {
		this.firmaAnexada = firmaAnexada;
	}
	public String getTipoMIMEFirmaAnexada() {
		return tipoMIMEFirmaAnexada;
	}
	public void setTipoMIMEFirmaAnexada(String tipoMIMEFirmaAnexada) {
		this.tipoMIMEFirmaAnexada = tipoMIMEFirmaAnexada;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
    
    
}
