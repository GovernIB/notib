/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació de l'entrega postal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	private String provincia;
	private String paisCodi;
	private String linea1;
	private String linea2;
	private Integer cie;
	private String formatSobre;
	private String formatFulla;
}
