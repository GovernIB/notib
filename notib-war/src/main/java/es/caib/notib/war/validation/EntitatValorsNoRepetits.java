/**
 * 
 */
package es.caib.notib.war.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=EntitatValorsNoRepetitsValidator.class)
public @interface EntitatValorsNoRepetits {

	String message() default "Ja existeix una altra entitat amb aquest codi, dir3 o nif";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
