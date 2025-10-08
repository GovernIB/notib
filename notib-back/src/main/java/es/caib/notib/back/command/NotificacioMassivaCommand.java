package es.caib.notib.back.command;

import es.caib.notib.back.helper.ConversioTipusHelper;
import es.caib.notib.back.validation.ValidNotificacioMassiu;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.service.GestioDocumentalService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Date;

/**
 * Command per al manteniment d'enviaments massius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
		
		var notificacioMassivaDto = ConversioTipusHelper.convertir(this, NotificacioMassivaDto.class);
		if (fitxerCSVGestdocId == null || fitxerCSVGestdocId.isEmpty() ) {
			notificacioMassivaDto.setFicheroCsvBytes(this.getFicheroCsv().getBytes());
			notificacioMassivaDto.setFicheroCsvNom(this.getFicheroCsv().getOriginalFilename());
		} else {
			notificacioMassivaDto.setFicheroCsvBytes(gestioDocumentalService.obtenirArxiuTemporal(fitxerCSVGestdocId, false));
			notificacioMassivaDto.setFicheroCsvNom(fitxerCSVNom);
		}
		if (fitxerZIPGestdocId == null || fitxerZIPGestdocId.isEmpty() ) {
			notificacioMassivaDto.setFicheroZipBytes(this.getFicheroZip().getBytes());
			notificacioMassivaDto.setFicheroZipNom(this.getFicheroZip().getOriginalFilename());
		} else {
			notificacioMassivaDto.setFicheroZipBytes(gestioDocumentalService.obtenirArxiuTemporal(fitxerZIPGestdocId, true));
			notificacioMassivaDto.setFicheroZipNom(fitxerZIPNom);
		}
		return notificacioMassivaDto;
	}
	
	public int getEmailDefaultSize() {

		var emailSize = 0;
		try {
			var email = this.getClass().getDeclaredField("email");
			emailSize = email.getAnnotation(Size.class).max();
		} catch (Exception ex) {
			log.error("No s'ha pogut recuperar la longitud del email: " + ex.getMessage());
		}
		return emailSize;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
