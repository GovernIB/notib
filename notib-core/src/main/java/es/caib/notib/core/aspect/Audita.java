package es.caib.notib.core.aspect;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;


@Retention(RUNTIME)
@Target(METHOD)
public @interface Audita {

	TipusOperacio operationType();
	TipusEntitat entityType();
	TipusObjecte returnType() default TipusObjecte.ENTITAT;
//	Class<? extends NotibAuditable<?>> entityClass();
//	Class<?> returnClass() default Object.class;
	
}
