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
@Constraint(validatedBy=ValidIfVisibleAndNormalitzatValidator.class)
@Documented
public @interface ValidIfVisibleAndNormalitzat {

	String fieldNameVisible();
	String fieldValueVisble();
	
	String fieldName();
	NotificaDomiciliConcretTipus fieldValue();
	String dependFieldName();
	String dependFieldNameSecond();

	String message() default "Aquest camp és obligatori";
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		ValidIfVisibleAndNormalitzat[] value();
    }
}
