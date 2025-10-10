package es.caib.notib.back.validation;

import es.caib.notib.back.command.UsuariCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.EmailValidHelper;
import es.caib.notib.back.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

@Slf4j
public class ValidUsuariValidator implements ConstraintValidator<ValidUsuari, UsuariCommand> {

    @Autowired
    private SessionScopedContext sessionScopedContext;

    @Override
    public void initialize(final ValidUsuari constraintAnnotation) {
        // init
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(final UsuariCommand usuari, final ConstraintValidatorContext context) {

        var valid = true;
        var locale = new Locale(sessionScopedContext.getIdiomaUsuari());
        context.disableDefaultConstraintViolation();
        try {
            if (!StringUtils.isEmpty(usuari.getEmailAlt()) && !EmailValidHelper.isEmailValid(usuari.getEmailAlt())) {
                valid = false;
                var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale);
                context.buildConstraintViolationWithTemplate(msg).addNode("emailAlt").addConstraintViolation();
            }
        } catch (final Exception ex) {
            valid = false;
            log.error("S'ha produït un error inesperat al validar l'usuari. "
                    + "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
        }
        if (!valid) {
            String msg = "usuari.form.errors.validacio";
            msg = MessageHelper.getInstance().getMessage(msg, null, locale);
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }
        return valid;
    }
}
