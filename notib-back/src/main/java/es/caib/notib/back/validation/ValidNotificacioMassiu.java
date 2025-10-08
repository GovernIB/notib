package es.caib.notib.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidNotificacioMassiuValidator.class)
public @interface ValidNotificacioMassiu {

	String message() default "Error en la validació del enviament massiu.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
