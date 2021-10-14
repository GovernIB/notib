package es.caib.notib.war.command;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
/**
 * Command per definir la visibilitat de columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ColumnesCommand {

	private Long id;
	private boolean dataEnviament;
	private boolean dataProgramada;
	private boolean notIdentificador;
	private boolean proCodi; 
	private boolean grupCodi; 
	private boolean dir3Codi; 
	private boolean usuari; 
	private boolean enviamentTipus; 
	private boolean concepte; 
	private boolean descripcio; 
	private boolean titularNif; 
	private boolean titularNomLlinatge; 
	private boolean titularEmail;
	private boolean destinataris; 
	private boolean llibreRegistre; 
	private boolean numeroRegistre; 
	private boolean dataRegistre; 
	private boolean dataCaducitat; 
	private boolean codiNotibEnviament;
	private boolean numCertificacio; 
	private boolean csvUuid; 
	private boolean estat;

	public static ColumnesCommand asCommand(ColumnesDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ColumnesCommand.class);
	}
	public static ColumnesDto asDto(ColumnesCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ColumnesDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
