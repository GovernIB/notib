package es.caib.notib.logic.intf.dto.cie;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliViaTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaPostalDto implements Serializable{

	private NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
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
}
