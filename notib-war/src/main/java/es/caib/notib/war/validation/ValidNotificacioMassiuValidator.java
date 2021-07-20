package es.caib.notib.war.validation;

import es.caib.notib.war.command.NotificacioMassivaCommand;
import es.caib.notib.war.helper.MessageHelper;
import org.apache.commons.io.FilenameUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidNotificacioMassiuValidator  implements ConstraintValidator<ValidNotificacioMassiu, NotificacioMassivaCommand> {
	// Validació de documents
	Long csvFileMaxSize = 2097152L; //2MB
	Long zipFileMaxSize = 15728640L; // 15MB
	List<String> formatsZipDisponibles = Arrays.asList(new String[] {"application/zip", "application/x-zip-compressed"});
	List<String> formatsCsvDisponibles = Arrays.asList(new String[] {"application/octet-stream", "text/csv"});
	List<String> extensionsZipDisponibles = Arrays.asList(new String[] {"zip"});;
	List<String> extensionsCsvDisponibles = Arrays.asList(new String[] {"csv"});;

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

		if (((notificacioMassivaCommand.getFicheroCsv() == null || notificacioMassivaCommand.getFicheroCsv().getSize() == 0))) {
			return "NotEmpty";
		}
		if ((notificacioMassivaCommand.getFicheroCsv() != null && notificacioMassivaCommand.getFicheroCsv().getSize() != 0)) {
			String extensio = FilenameUtils.getExtension(notificacioMassivaCommand.getFicheroCsv().getOriginalFilename());
			if (!extensionsCsvDisponibles.contains(extensio)) {
				return "notificacio.form.valid.document.format";
			}
			if (!formatsCsvDisponibles.contains(notificacioMassivaCommand.getFicheroCsv().getContentType())) {
				return "notificacio.form.valid.document.format";
			}
			Long fileSize = notificacioMassivaCommand.getFicheroCsv().getSize();
			if (fileSize > csvFileMaxSize) {
				return "notificacio.form.valid.document.size";
			}
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

		if ((notificacioMassivaCommand.getFicheroZip() != null && notificacioMassivaCommand.getFicheroZip().getSize() != 0)) {
			String extensio = FilenameUtils.getExtension(notificacioMassivaCommand.getFicheroZip().getOriginalFilename());
			if (!extensionsZipDisponibles.contains(extensio)) {
				return "notificacio.form.valid.document.format";
			}
			if (!formatsZipDisponibles.contains(notificacioMassivaCommand.getFicheroZip().getContentType())) {
				return "notificacio.form.valid.document.format";
			}
			Long fileSize = notificacioMassivaCommand.getFicheroZip().getSize();
			if (fileSize > zipFileMaxSize) {
				return "notificacio.form.valid.document.size";
			}
		}
		return null;
	}
}
