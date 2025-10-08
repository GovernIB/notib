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
@Constraint(validatedBy=OrganNoRepetitValidator.class)
public @interface OrganNoRepetit {

	String message() default "Ja existeix una altre òrgan gestor amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
