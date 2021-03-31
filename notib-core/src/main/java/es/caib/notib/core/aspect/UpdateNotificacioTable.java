package es.caib.notib.core.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target(METHOD)
public @interface UpdateNotificacioTable {

//	TipusOperacio operationType();
//	TipusEntitat entityType();
//	TipusObjecte returnType() default TipusObjecte.ENTITAT;
//	Class<? extends NotibAuditable<?>> entityClass();
//	Class<?> returnClass() default Object.class;
	
}
