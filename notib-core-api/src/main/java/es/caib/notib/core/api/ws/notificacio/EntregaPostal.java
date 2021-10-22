/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.util.TrimStringDeserializer;
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
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String viaNom;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String numeroCasa;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String numeroQualificador;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String puntKm;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String apartatCorreus;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String portal;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String escala;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String planta;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String porta;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String bloc;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String complement;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String codiPostal;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String poblacio;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String municipiCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String provincia;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String paisCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String linea1;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String linea2;
	private Integer cie;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String formatSobre;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String formatFulla;
}
