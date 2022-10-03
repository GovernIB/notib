package es.caib.notib.back.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidNotificacioMassiuValidator.class)
public @interface ValidNotificacioMassiu {

	String message() default "Error en la validació del enviament massiu.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
