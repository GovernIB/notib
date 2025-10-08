/**
 * 
 */
package es.caib.notib.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Command per a validar les dades d'una regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = UsuariExistsValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsuariExists {

	String message() default "usuari.exists.error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
