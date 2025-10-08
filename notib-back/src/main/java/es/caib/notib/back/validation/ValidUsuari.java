package es.caib.notib.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidUsuariValidator.class)
public @interface ValidUsuari {

    String message() default "Error en la validació de l'usuari.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
