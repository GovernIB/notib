package es.caib.notib.war.validation;

import es.caib.notib.war.command.NotificacioMassivaCommand;
import es.caib.notib.war.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ValidNotificacioMassiuValidator  implements ConstraintValidator<ValidNotificacioMassiu, NotificacioMassivaCommand> {
	// Validació de documents
	Long csvFileMaxSize = 2097152L; //2MB
	Long zipFileMaxSize = 15728640L; // 15MB
//	List<String> formatsZipDisponibles = Arrays.asList("application/zip", "application/x-zip-compressed");
//	List<String> formatsCsvDisponibles = Arrays.asList("application/octet-stream", "text/csv");
	List<String> extensionsZipDisponibles = Collections.singletonList("zip");
	List<String> extensionsCsvDisponibles = Collections.singletonList("csv");

	@Override
	public void initialize(final ValidNotificacioMassiu constraintAnnotation) {
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(NotificacioMassivaCommand notificacioMassivaCommand, ConstraintValidatorContext context) {
		boolean valid = true;

		String messageError = checkCSVFile(notificacioMassivaCommand);
		if (messageError != null && !messageError.isEmpty()) {
			valid = false;
			notificacioMassivaCommand.setFicheroCsv(null);
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(messageError))
					.addNode("ficheroCsv")
					.addConstraintViolation();
		}

		messageError = checkZIPFile(notificacioMassivaCommand);
		if (messageError != null && !messageError.isEmpty()) {
			valid = false;
			notificacioMassivaCommand.setFicheroZip(null);
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(messageError))
					.addNode("ficheroZip")
					.addConstraintViolation();
		}
		
		return valid;
	}

	/**
	 * Return the key of the error message on invalid csv file, return null for valid csv files.
	 *
	 * @param notificacioMassivaCommand Command to validate
	 * @return String with the error message key
	 */
	private String checkCSVFile(NotificacioMassivaCommand notificacioMassivaCommand) {
		if (notificacioMassivaCommand.getFitxerCSVGestdocId() != null && !notificacioMassivaCommand.getFitxerCSVGestdocId().isEmpty()) {
			return null;
		}

		MultipartFile fitxerCSV = notificacioMassivaCommand.getFicheroCsv();
		if (fitxerCSV == null || fitxerCSV.getSize() == 0) {
			return "NotEmpty";
		}

		String extensio = FilenameUtils.getExtension(fitxerCSV.getOriginalFilename());
		if (!extensionsCsvDisponibles.contains(extensio)) {
			log.info("Error validacio CSV enviament massiu. Extensió fitxer incorrecte: " + extensio);
			return "notificacio.form.valid.document.format";
		}
//		if (!formatsCsvDisponibles.contains(fitxerCSV.getContentType())) {
//			log.info("Error validacio CSV enviament massiu. Format fitxer incorrecte: " + fitxerCSV.getContentType());
//			return "notificacio.form.valid.document.format";
//		}
		Long fileSize = fitxerCSV.getSize();
		if (fileSize > csvFileMaxSize) {
			return "notificacio.form.valid.document.size";
		}
		return null;
	}

	/**
	 * Return the key of the error message on invalid csv file, return null for valid csv files.
	 *
	 * @param notificacioMassivaCommand Command to validate
	 * @return String with the error message key
	 */
	private String checkZIPFile(NotificacioMassivaCommand notificacioMassivaCommand) {
		if (notificacioMassivaCommand.getFitxerZIPGestdocId() != null && !notificacioMassivaCommand.getFitxerZIPGestdocId().isEmpty()) {
			return null;
		}

		MultipartFile fitxerZIP = notificacioMassivaCommand.getFicheroZip();
		if (fitxerZIP != null && fitxerZIP.getSize() != 0) {
			String extensio = FilenameUtils.getExtension(fitxerZIP.getOriginalFilename());
			if (!extensionsZipDisponibles.contains(extensio)) {
				log.info("Error validacio ZIP enviament massiu. Extensió fitxer incorrecte: " + extensio);
				return "notificacio.form.valid.document.format";
			}
//			if (!formatsZipDisponibles.contains(fitxerZIP.getContentType())) {
//				log.info("Error validacio CSV enviament massiu. Format fitxer incorrecte: " + fitxerZIP.getContentType());
//				return "notificacio.form.valid.document.format";
//			}
			Long fileSize = fitxerZIP.getSize();
			if (fileSize > zipFileMaxSize) {
				return "notificacio.form.valid.document.size";
			}
		}
		return null;
	}
}
