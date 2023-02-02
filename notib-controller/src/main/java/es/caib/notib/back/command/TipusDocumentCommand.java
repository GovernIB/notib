/**
 * 
 */
package es.caib.notib.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class TipusDocumentCommand {

	private Long id;
	private Long entitatId;
	private TipusDocumentEnumDto tipusDoc;

	
	public static TipusDocumentCommand asCommand(TipusDocumentDto dto) {	
		return ConversioTipusHelper.convertir(dto, TipusDocumentCommand.class);
	}
	public static TipusDocumentDto asDto(TipusDocumentCommand command) {
		return ConversioTipusHelper.convertir(command, TipusDocumentDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
