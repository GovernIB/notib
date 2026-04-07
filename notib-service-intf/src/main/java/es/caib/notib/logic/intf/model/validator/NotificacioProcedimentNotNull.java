package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Valida si el camp de procediment ha de ser NotNull. En les notificacions a on l'usuari te permís de "comunicacions
 * sense procediment" sobre l'òrgan gestor seleccionat el camp procediment és opcional.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class NotificacioProcedimentNotNull implements CustomValidator<NotificacioResource> {

	private final NotificacioResourceService notificacioResourceService;

	@Override
	public boolean validate(NotificacioResource value, ConstraintValidatorContext context) {
		if (isProcedimentRequired(value)) {
			return value.getProcediment() != null;
		} else {
			return true;
		}
	}

	@Override
	public String getFieldMessage() {
		return "{javax.validation.constraints.NotNull.message}";
	}

	private boolean isProcedimentRequired(NotificacioResource notificacio) {
		Map<String, Object> changes = notificacioResourceService.onChange(
			null,
			notificacio,
			NotificacioResource.Fields.organGestor,
			notificacio.getOrganGestor(),
			null);
		return (boolean)changes.get(NotificacioResource.Fields.procedimentRequired);
	}

}
