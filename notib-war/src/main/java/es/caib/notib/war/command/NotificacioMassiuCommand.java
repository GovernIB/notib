package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
import es.caib.notib.war.validation.ValidNotificacioMassiu;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

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

	@NotNull
	private Date caducitat;

	@Size(max=160)
	private String email;
	private Long pagadorPostalId;

	public static NotificacioMassiuCommand asCommand(NotificacioMassivaDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				NotificacioMassiuCommand.class);
	}
	public NotificacioMassivaDto asDto() throws IOException {
		
		NotificacioMassivaDto notificacioMassiu = ConversioTipusHelper.convertir(
				this,
				NotificacioMassivaDto.class);
		
		notificacioMassiu.setFicheroCsvBytes(this.getFicheroCsv().getBytes());
		notificacioMassiu.setFicheroCsvNom(this.getFicheroCsv().getOriginalFilename());
		notificacioMassiu.setFicheroZipBytes(this.getFicheroZip().getBytes());
		notificacioMassiu.setFicheroZipNom(this.getFicheroZip().getOriginalFilename());
		
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
