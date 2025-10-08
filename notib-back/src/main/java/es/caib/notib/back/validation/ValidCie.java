package es.caib.notib.back.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidCieValidator.class)
public @interface ValidCie {
	
	String message() default "Error en la validació de la notificació.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
