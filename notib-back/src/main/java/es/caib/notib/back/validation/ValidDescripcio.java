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
 * Constraint de validació que controla que el nombre de
 * document d'identitat sigui vàlid. Els tipus de document
 * suportats son: NIF, DNI, NIE i CIF. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidDescripcioValidator.class)
public @interface ValidDescripcio {

	String fieldName();
	
	String message() default "El format del concepte és incorrecte";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
