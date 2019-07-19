/**
 * 
 */
package es.caib.notib.war.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;

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
	NotificaDomiciliConcretTipusEnumDto noExpectedFieldValue();
	
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
