/**
 * 
 */
package es.caib.notib.logic.aspect;

import es.caib.notib.logic.intf.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * services.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Aspect
@Order(100)
@Component
public class AuditAdvice {

	@Autowired
	private AuditService auditService;
	
	@AfterReturning(pointcut = "@annotation(Audita)", returning = "entitat")
	public void audita(JoinPoint joinPoint, Object entitat) throws NoSuchMethodException, SecurityException {

		final String methodName = joinPoint.getSignature().getName();
		final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
	    Method method = methodSignature.getMethod();
	    if (method.getDeclaringClass().isInterface()) {
	        method = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());    
	    }
	    Audita auditAnnotation = method.getAnnotation(Audita.class);
		log.debug(">>> AUDIT - JoinPoint: " + joinPoint.getSignature().toShortString());
		log.debug(">>> AUDIT - Entitat a auditar: " + auditAnnotation.entityType());
		log.debug(">>> AUDIT - Tipus d'operació: " + auditAnnotation.operationType());
		log.debug(">>> AUDIT - Objecte disponible per auditar: " + auditAnnotation.returnType());
		log.debug(">>> AUDIT ----------------------------------------------------------------- ");
		auditService.audita(entitat, auditAnnotation.operationType(), auditAnnotation.entityType(),
							auditAnnotation.returnType(), joinPoint.getSignature().toShortString());
	}

//	// Auditoria d'entitat
//	// =================================================================================
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.EntitatServiceImpl.create(..))", returning="entitat")
//	public void doAfterEntitatCreateThrowing(JoinPoint joinPoint, EntitatDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.EntitatServiceImpl.update*(..))", returning="entitat")
//	public void doAfterEntitatUpdate(JoinPoint joinPoint, EntitatDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.EntitatServiceImpl.delete(..))", returning="entitat")
//	public void doAfterEntitatDelete(JoinPoint joinPoint, EntitatDto entitat) {
//
//	}

	// Auditoria de procediment
	// =================================================================================
	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.create(..))", returning="procediment")
//	public void doAfterProcedimentCreateThrowing(JoinPoint joinPoint, ProcedimentDto procediment) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.update(..))", returning="procediment")
//	public void doAfterProcedimentUpdate(JoinPoint joinPoint, ProcedimentDto procediment) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.delete(..))", returning="procediment")
//	public void doAfterProcedimentDelete(JoinPoint joinPoint, ProcedimentDto procediment) {
//
//	}
//
//	// ProcedimentHelper
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.helper.ProcedimentHelper.nouProcediment(..))", returning="procediment")
//	public void doAfterNouProcediment(JoinPoint joinPoint, ProcedimentEntity procediment) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.helper.ProcedimentHelper.updateProcediment(..))", returning="procediment")
//	public void doAfterUpdateProcediment(JoinPoint joinPoint, ProcedimentEntity procediment) {
//
//	}
	
//	// Auditoria de grup
//	// =================================================================================
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.GrupServiceImpl.create(..))", returning="grup")
//	public void doAfterGrupCreateThrowing(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.GrupServiceImpl.update(..))", returning="grup")
//	public void doAfterGrupUpdate(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.GrupServiceImpl.delete(..))", returning="grup")
//	public void doAfterGrupDelete(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
	
//	// Auditoria de procedimentGrup
//	// =================================================================================
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.create(..))", returning="grup")
//	public void doAfterGrupCreateThrowing(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.update(..))", returning="grup")
//	public void doAfterGrupUpdate(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
//	
//	@AfterReturning(pointcut="execution(* es.caib.notib.core.service.ProcedimentServiceImpl.delete(..))", returning="grup")
//	public void doAfterGrupDelete(JoinPoint joinPoint, GrupDto entitat) {
//
//	}
	
	// Auditoria de notificació
	// =================================================================================
	
	// Auditoria d'enviament
	// =================================================================================
	
	// Auditoria d'aplicació
	// =================================================================================
	
}
