package es.caib.notib.war.validation;

import es.caib.notib.war.command.NotificacioMassiuCommand;
import es.caib.notib.war.helper.MessageHelper;
import org.apache.commons.io.FilenameUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValidNotificacioMassiuValidator  implements ConstraintValidator<ValidNotificacioMassiu, NotificacioMassiuCommand> {

	@Override
	public void initialize(final ValidNotificacioMassiu constraintAnnotation) {
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(NotificacioMassiuCommand notificacioMassiu, ConstraintValidatorContext context) {
		boolean valid = true;
		
		// Validació de documents
		Long csvFileMaxSize = 2097152L; //2MB
		Long zipFileMaxSize = 15728640L; // 15MB
		List<String> formatsZipDisponibles = Arrays.asList(new String[] {"application/zip", "application/x-zip-compressed"});
		List<String> formatsCsvDisponibles = Arrays.asList(new String[] {"application/octet-stream", "text/csv"});
		List<String> extensionsZipDisponibles = Arrays.asList(new String[] {"zip"});;
		List<String> extensionsCsvDisponibles = Arrays.asList(new String[] {"csv"});;

		if (((notificacioMassiu.getFicheroCsv() == null || notificacioMassiu.getFicheroCsv().getSize() == 0))) {
			valid = false;
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("ficheroCsv")
					.addConstraintViolation();
		}
		
		if ((notificacioMassiu.getFicheroCsv() != null && notificacioMassiu.getFicheroCsv().getSize() != 0)) {
				String extensio = FilenameUtils.getExtension(notificacioMassiu.getFicheroCsv().getOriginalFilename());
				if (!extensionsCsvDisponibles.contains(extensio)) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
							.addNode("ficheroCsv")
							.addConstraintViolation();
				}
				if (!formatsCsvDisponibles.contains(notificacioMassiu.getFicheroCsv().getContentType())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
							.addNode("ficheroCsv")
							.addConstraintViolation();
				}
				Long fileSize = notificacioMassiu.getFicheroCsv().getSize();
				if (fileSize > csvFileMaxSize) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size"))
							.addNode("ficheroCsv")
							.addConstraintViolation();
				}
		}
		
//		if (((notificacioMassiu.getFicheroZip() == null || notificacioMassiu.getFicheroZip().getSize() == 0))) {
//			valid = false;
//			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
//					.addNode("ficheroZip")
//					.addConstraintViolation();
//		}
		
		if ((notificacioMassiu.getFicheroZip() != null && notificacioMassiu.getFicheroZip().getSize() != 0)) {
				String extensio = FilenameUtils.getExtension(notificacioMassiu.getFicheroZip().getOriginalFilename());
				if (!extensionsZipDisponibles.contains(extensio)) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
							.addNode("ficheroZip")
							.addConstraintViolation();
				}
				if (!formatsZipDisponibles.contains(notificacioMassiu.getFicheroZip().getContentType())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
							.addNode("ficheroZip")
							.addConstraintViolation();
				}
				Long fileSize = notificacioMassiu.getFicheroZip().getSize();
				if (fileSize > zipFileMaxSize) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size"))
							.addNode("ficheroZip")
							.addConstraintViolation();
				}
		}
		

		
		return valid;
	}

}
