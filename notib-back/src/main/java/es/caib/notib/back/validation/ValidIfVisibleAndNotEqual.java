/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;

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
@Constraint(validatedBy=ValidIfVisibleAndNotEqualValidator.class)
@Documented
public @interface ValidIfVisibleAndNotEqual {

	String fieldName();
	String fieldValue();
	String dependFieldName();
	String noDependFieldName();
	NotificaDomiciliConcretTipus noExpectedFieldValue();
	
	String message() default "Aquest camp és obligatori";
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		ValidIfVisibleAndNotEqual[] value();
    }
}
