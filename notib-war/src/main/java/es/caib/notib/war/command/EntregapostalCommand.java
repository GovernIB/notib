package es.caib.notib.war.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.war.validation.ValidIfVisibleAndNormalitzat;

/**
 * Command per al manteniment de entregues postals
 * 	
 * @author Limit Tecnologies <limit@limit.es>
 *
 */

//@ValidIfVisibleAndNotEqual.List({
//	@ValidIfVisibleAndNotEqual(
//        fieldName = "visible",
//        fieldValue = "true",
//        noDependFieldName = "tipus",
//        noExpectedFieldValue =  NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR,
//        dependFieldName = "codiPostal")
//})
@ValidIfVisibleAndNormalitzat.List({
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "codiPostal",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "tipusVia",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "viaNom",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "numeroCasa",
			dependFieldNameSecond = "puntKm"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "puntKm",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "municipiCodi",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "provincia",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.NACIONAL,
			dependFieldName = "poblacio",
			dependFieldNameSecond = "numeroCasa"),
	
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.ESTRANGER,
			dependFieldName = "codiPostal",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.ESTRANGER,
			dependFieldName = "viaNom",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.ESTRANGER,
			dependFieldName = "paisCodi",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.ESTRANGER,
			dependFieldName = "poblacio",
			dependFieldNameSecond = "numeroCasa"),
	
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS,
			dependFieldName = "codiPostal",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS,
			dependFieldName = "apartatCorreus",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS,
			dependFieldName = "municipiCodi",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS,
			dependFieldName = "provincia",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS,
			dependFieldName = "poblacio",
			dependFieldNameSecond = "numeroCasa"),
	
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR,
			dependFieldName = "codiPostal",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR,
			dependFieldName = "linea1",
			dependFieldNameSecond = "numeroCasa"),
	@ValidIfVisibleAndNormalitzat(
			fieldNameVisible =  "visible",
			fieldValueVisble =  "true",
			fieldName = "tipus",
			fieldValue = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR,
			dependFieldName = "linea2",
			dependFieldNameSecond = "numeroCasa")
})
public class EntregapostalCommand {

	private NotificaDomiciliConcretTipusEnumDto tipus;
	private NotificaDomiciliViaTipusEnumDto tipusVia;
	@Size(max=50)
	private String viaNom;
	@Size(max=5)
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
	@NotEmpty @Size(max=10)
	private String codiPostal;
	@Size(max=255)
	private String poblacio;
	private String municipiCodi; 
	private String provincia;
	private String paisCodi;
	@Size(max=50)
	private String linea1;
	@Size(max=50)
	private String linea2;
	private String formatSobre;
	private String formatFulla;
	private boolean visible = true;
	
	public NotificaDomiciliConcretTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(NotificaDomiciliConcretTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public NotificaDomiciliViaTipusEnumDto getTipusVia() {
		return tipusVia;
	}
	public void setTipusVia(NotificaDomiciliViaTipusEnumDto tipusVia) {
		this.tipusVia = tipusVia;
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
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
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
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	
}
