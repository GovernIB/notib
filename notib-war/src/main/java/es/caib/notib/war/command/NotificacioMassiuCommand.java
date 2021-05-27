package es.caib.notib.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.NotificacioMassiuDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'enviaments massius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioMassiuCommand {
	
	@NotEmpty
	private MultipartFile ficheroCsv;
	@NotEmpty
	private MultipartFile ficheroZip;

	public static NotificacioMassiuCommand asCommand(NotificacioMassiuDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				NotificacioMassiuCommand.class);
	}
	public static NotificacioMassiuDto asDto(NotificacioMassiuCommand command) throws IOException {
		
		NotificacioMassiuDto notificacioMassiu = ConversioTipusHelper.convertir(
				command,
				NotificacioMassiuDto.class);
		
		notificacioMassiu.setFicheroCsvBytes(command.getFicheroCsv().getBytes());
		
		return notificacioMassiu;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
