package es.caib.notib.back.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidEntregaPostalValidator.class)
public @interface ValidEntregaPostal {
	
	String message() default "Error en la validació de la entrega postal.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
