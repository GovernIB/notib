package es.caib.notib.war.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidUuidDocumentValidator.class)
public @interface ValidUuidDocument {
	
	String fieldName();
	String dependFieldName();
	
	String message() default "El camp valor del document és obligatori";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
