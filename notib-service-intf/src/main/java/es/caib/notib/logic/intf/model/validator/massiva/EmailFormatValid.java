package es.caib.notib.logic.intf.model.validator.massiva;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmailFormatValid implements CustomValidator<NotificacioMassivaResource>  {

	public static final Pattern EMAIL_REGEX = Pattern.compile(
		"^(?![\\.-])[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +  // local part
			"@" +  // @ symbol
			"(?![\\.-])[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)*" +  // domain part
			"(?:\\.[A-Za-z]{2,})$" +  // top-level domain
			"(?![\\.-])", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean validate(NotificacioMassivaResource resource, ConstraintValidatorContext context) {

		try {
			if (StringUtils.isBlank(resource.getEmail())) {
				return true;
			}
			Matcher matcher = EMAIL_REGEX.matcher(resource.getEmail());
			return matcher.find();
		} catch (Exception e) {
			return false;
		}
	}
}
