package es.caib.notib.war.validation;

import com.google.common.base.Strings;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.UsuariCommand;
import es.caib.notib.war.helper.EmailValidHelper;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.SessioHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

@Slf4j
public class ValidUsuariValidator implements ConstraintValidator<ValidUsuari, UsuariCommand> {

    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private ProcedimentService procedimentService;

    @Override
    public void initialize(final ValidUsuari constraintAnnotation) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(final UsuariCommand usuari, final ConstraintValidatorContext context) {

        boolean valid = true;
        Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
        context.disableDefaultConstraintViolation();
        try {
            if (!Strings.isNullOrEmpty(usuari.getEmailAlt()) && !EmailValidHelper.isEmailValid(usuari.getEmailAlt())) {
                valid = false;
                context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale))
                        .addNode("emailAlt").addConstraintViolation();
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