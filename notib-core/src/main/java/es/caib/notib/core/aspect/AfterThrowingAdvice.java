/**
 * 
 */
package es.caib.notib.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.service.AplicacioService;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * services.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Aspect
@Component
public class AfterThrowingAdvice {

	@Autowired
	private AplicacioService aplicacioService;

	@AfterThrowing(pointcut="execution(* es.caib.notib.*.*(..))", throwing="exception")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		aplicacioService.excepcioSave(exception);
	}
	
}
