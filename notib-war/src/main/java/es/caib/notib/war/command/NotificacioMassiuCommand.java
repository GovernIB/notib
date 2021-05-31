package es.caib.notib.war.command;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import es.caib.notib.core.api.dto.NotificacioMassiuDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidNotificacioMassiu;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment d'enviaments massius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ValidNotificacioMassiu
public class NotificacioMassiuCommand {
	
	private MultipartFile ficheroCsv;
	private MultipartFile ficheroZip;
	private Date caducitat;
	@NotEmpty @Size(max=160)
	private String email;

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
		notificacioMassiu.setFicheroZipBytes(command.getFicheroZip().getBytes());
		
		return notificacioMassiu;
	}
	
	public int getEmailDefaultSize() {
		int emailSize = 0;
		try {
			Field email = this.getClass().getDeclaredField("email");
			emailSize = email.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del email: " + ex.getMessage());
		}
		return emailSize;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioMassiuCommand.class);
}
