/**
 * 
 */
package es.caib.notib.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.back.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusDocumentCommand {

	private Long id;
	private Long entitatId;
	private TipusDocumentEnumDto tipusDoc;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public TipusDocumentEnumDto getTipusDoc() {
		return tipusDoc;
	}
	public void setTipusDoc(TipusDocumentEnumDto tipusDoc) {
		this.tipusDoc = tipusDoc;
	}
	
	public static TipusDocumentCommand asCommand(TipusDocumentDto dto) {	
		return ConversioTipusHelper.convertir(
				dto,
				TipusDocumentCommand.class);
	}
	public static TipusDocumentDto asDto(TipusDocumentCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				TipusDocumentDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}