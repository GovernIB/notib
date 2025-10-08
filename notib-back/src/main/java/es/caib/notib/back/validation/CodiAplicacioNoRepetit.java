/**
 * 
 */
package es.caib.notib.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiAplicacioNoRepetitValidator.class)
public @interface CodiAplicacioNoRepetit {

	String message() default "Ja existeix una altra aplicació amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
