package es.caib.notib.back.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidCieValidator.class)
public @interface ValidCie {
	
	String message() default "Error en la validació de la notificació.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
