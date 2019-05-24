/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;

/**
 * Informació de l'entrega postal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class EntregaPostal {

	private NotificaDomiciliConcretTipusEnumDto tipus;
	private EntregaPostalViaTipusEnum viaTipus;
	private String viaNom;
	private String numeroCasa;
	private String numeroQualificador;
	private String puntKm;
	private String apartatCorreus;
	private String portal;
	private String escala;
	private String planta;
	private String porta;
	private String bloc;
	private String complement;
	private String codiPostal;
	private String poblacio;
	private String municipiCodi;
	private String provinciaCodi;
	private String paisCodi;
	private String linea1;
	private String linea2;
	private Integer cie;
	private String formatSobre;
	private String formatFulla;

	public NotificaDomiciliConcretTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(NotificaDomiciliConcretTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public EntregaPostalViaTipusEnum getViaTipus() {
		return viaTipus;
	}
	public void setViaTipus(EntregaPostalViaTipusEnum viaTipus) {
		this.viaTipus = viaTipus;
	}
	public String getViaNom() {
		return viaNom;
	}
	public void setViaNom(String viaNom) {
		this.viaNom = viaNom;
	}
	public String getNumeroCasa() {
		return numeroCasa;
	}
	public void setNumeroCasa(String numeroCasa) {
		this.numeroCasa = numeroCasa;
	}
	public String getNumeroQualificador() {
		return numeroQualificador;
	}
	public void setNumeroQualificador(String numeroQualificador) {
		this.numeroQualificador = numeroQualificador;
	}
	public String getPuntKm() {
		return puntKm;
	}
	public void setPuntKm(String puntKm) {
		this.puntKm = puntKm;
	}
	public String getApartatCorreus() {
		return apartatCorreus;
	}
	public void setApartatCorreus(String apartatCorreus) {
		this.apartatCorreus = apartatCorreus;
	}
	public String getPortal() {
		return portal;
	}
	public void setPortal(String portal) {
		this.portal = portal;
	}
	public String getEscala() {
		return escala;
	}
	public void setEscala(String escala) {
		this.escala = escala;
	}
	public String getPlanta() {
		return planta;
	}
	public void setPlanta(String planta) {
		this.planta = planta;
	}
	public String getPorta() {
		return porta;
	}
	public void setPorta(String porta) {
		this.porta = porta;
	}
	public String getBloc() {
		return bloc;
	}
	public void setBloc(String bloc) {
		this.bloc = bloc;
	}
	public String getComplement() {
		return complement;
	}
	public void setComplement(String complement) {
		this.complement = complement;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public String getPoblacio() {
		return poblacio;
	}
	public void setPoblacio(String poblacio) {
		this.poblacio = poblacio;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getLinea1() {
		return linea1;
	}
	public void setLinea1(String linea1) {
		this.linea1 = linea1;
	}
	public String getLinea2() {
		return linea2;
	}
	public void setLinea2(String linea2) {
		this.linea2 = linea2;
	}
	public Integer getCie() {
		return cie;
	}
	public void setCie(Integer cie) {
		this.cie = cie;
	}
	public String getFormatSobre() {
		return formatSobre;
	}
	public void setFormatSobre(String formatSobre) {
		this.formatSobre = formatSobre;
	}
	public String getFormatFulla() {
		return formatFulla;
	}
	public void setFormatFulla(String formatFulla) {
		this.formatFulla = formatFulla;
	}
}
