package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida si el camp grupCodi ha de ser NotNull. És obligatori únicament quan el procediment
 * seleccionat està configurat amb grups.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class NotificacioGrupCodiRequired implements CustomValidator<NotificacioResource> {

	private final ProcedimentResourceService procedimentResourceService;

	@Override
	public boolean validate(NotificacioResource value, ConstraintValidatorContext context) {
		if (value.getProcediment() == null) {
			return true;
		}
		var procediment = procedimentResourceService.getOne(value.getProcediment().getId(), null);
		if (!procediment.isAgrupar()) {
			return true;
		}
		return value.getGrupCodi() != null;
	}

	@Override
	public String getFieldMessage() {
		return "{javax.validation.constraints.NotNull.message}";
	}

}
