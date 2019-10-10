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
 * Constraint de validació que controla que el nombre de
 * document d'identitat sigui vàlid. Els tipus de document
 * suportats son: NIF, DNI, NIE i CIF. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidConcepteValidator.class)
public @interface ValidConcepte {

	String fieldName();
	
	String message() default "El format del concepte és incorrecte";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
