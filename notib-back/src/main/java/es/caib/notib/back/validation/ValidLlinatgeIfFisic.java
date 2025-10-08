/**
 * 
 */
package es.caib.notib.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidLlinatgeIfFisicValidator.class)
@Documented
public @interface ValidLlinatgeIfFisic {

	String fieldName();
	String fieldName2();
	String fieldValue();
	String fieldValue2();
	String dependFieldName();

	String message() default "Aquest camp és obligatori";
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		ValidLlinatgeIfFisic[] value();
    }
}
