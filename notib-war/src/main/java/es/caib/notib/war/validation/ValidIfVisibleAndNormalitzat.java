/**
 * 
 */
package es.caib.notib.war.validation;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
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
@Constraint(validatedBy=ValidIfVisibleAndNormalitzatValidator.class)
@Documented
public @interface ValidIfVisibleAndNormalitzat {

	String fieldNameVisible();
	String fieldValueVisble();
	
	String fieldName();
	NotificaDomiciliConcretTipusEnumDto fieldValue();
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
