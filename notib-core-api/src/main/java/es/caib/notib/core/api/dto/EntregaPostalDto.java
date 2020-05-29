package es.caib.notib.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntregaPostalDto implements Serializable{

	private NotificaDomiciliConcretTipusEnumDto tipus;
	private NotificaDomiciliViaTipusEnumDto viaTipus;
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
	private String codiPostalNorm;
	private String poblacio;
	private String municipiCodi;
	private String provincia;
	private String paisCodi;
	private String linea1;
	private String linea2;
	private String formatSobre;
	private String formatFulla;
	private Integer cie;
	private boolean activa;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -8074332473505468212L;

}
