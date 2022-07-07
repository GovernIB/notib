package es.caib.notib.war.command;

import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.core.api.service.GestioDocumentalService;
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
public class NotificacioMassivaCommand {

	private MultipartFile ficheroCsv;
	private String fitxerCSVGestdocId;
	private String fitxerCSVNom;
	private MultipartFile ficheroZip;
	private String fitxerZIPGestdocId;
	private String fitxerZIPNom;

	@NotNull
	private Date caducitat;

	@Size(max=160)
	private String email;
	private Long pagadorPostalId;

	public static NotificacioMassivaCommand asCommand(NotificacioMassivaDto dto) {
		return ConversioTipusHelper.convertir(dto, NotificacioMassivaCommand.class);
	}
	public NotificacioMassivaDto asDto(GestioDocumentalService gestioDocumentalService) throws IOException {
		
		NotificacioMassivaDto notificacioMassivaDto = ConversioTipusHelper.convertir(this, NotificacioMassivaDto.class);

		if (fitxerCSVGestdocId == null || fitxerCSVGestdocId.isEmpty() ) {
			notificacioMassivaDto.setFicheroCsvBytes(this.getFicheroCsv().getBytes());
			notificacioMassivaDto.setFicheroCsvNom(this.getFicheroCsv().getOriginalFilename());
		} else {
			notificacioMassivaDto.setFicheroCsvBytes(gestioDocumentalService.obtenirArxiuTemporal(fitxerCSVGestdocId));
			notificacioMassivaDto.setFicheroCsvNom(fitxerCSVNom);
		}
		if (fitxerZIPGestdocId == null || fitxerZIPGestdocId.isEmpty() ) {
			notificacioMassivaDto.setFicheroZipBytes(this.getFicheroZip().getBytes());
			notificacioMassivaDto.setFicheroZipNom(this.getFicheroZip().getOriginalFilename());
		} else {
			notificacioMassivaDto.setFicheroZipBytes(gestioDocumentalService.obtenirArxiuTemporal(fitxerZIPGestdocId));
			notificacioMassivaDto.setFicheroZipNom(fitxerZIPNom);
		}
		return notificacioMassivaDto;
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
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioMassivaCommand.class);
}
