package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
/**
 * Command per definir la visibilitat de columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ColumnesCommand {

	private Long id;
	private boolean dataCreacio;
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
	private boolean titularNomLlinatge;
	private boolean destinataris;
	private boolean numeroRegistre;
	private boolean dataCaducitat;
	private boolean codiNotibEnviament;
	private boolean csvUuid;
	private boolean estat;
	private boolean referenciaNotificacio;
	private boolean entregaPostal;

	public static ColumnesCommand asCommand(ColumnesDto dto) {
		return ConversioTipusHelper.convertir(dto, ColumnesCommand.class);
	}
	public static ColumnesDto asDto(ColumnesCommand command) {
		return ConversioTipusHelper.convertir(command, ColumnesDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
