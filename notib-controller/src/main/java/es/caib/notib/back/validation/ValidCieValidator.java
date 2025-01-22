package es.caib.notib.back.validation;

import es.caib.notib.back.command.CieCommand;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class ValidCieValidator implements ConstraintValidator<ValidCie, CieCommand> {

    @Autowired
    private PagadorCieService cieService;
    @Autowired
    private SessionScopedContext sessionScopedContext;

    @Override
    public void initialize(final ValidCie constraintAnnotation) {
        //init
    }

    @Override
    public boolean isValid(CieCommand cieCommand, ConstraintValidatorContext context) {

        Locale locale = new Locale(sessionScopedContext.getIdiomaUsuari());
        var valid = true;
        if (cieService.existeixCieByEntitatAndOrganGestor(cieCommand.getOrganismePagadorCodi())) {
            var msg = MessageHelper.getInstance().getMessage("cie.form.organ.pagador.entitat.existent", null, locale);
            context.buildConstraintViolationWithTemplate(msg).addNode("organismePagadorCodi").addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
