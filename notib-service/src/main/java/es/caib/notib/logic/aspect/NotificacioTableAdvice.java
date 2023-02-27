package es.caib.notib.logic.aspect;

import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * services.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Aspect
@Order(201)
@Component
public class NotificacioTableAdvice {

	@Autowired
	private NotificacioTableHelper notificacioTableHelper;

	@AfterReturning(pointcut = "execution(* es.caib.notib.logic.helper.AuditNotificacioHelper.desa*(..))", returning = "notificacio")
	public void addNotificacioTableView(NotificacioEntity notificacio) {
		notificacioTableHelper.crearRegistre(notificacio);
	}

	@AfterReturning(
//			pointcut = "execution(* es.caib.notib.core.helper.AuditNotificacioHelper.update*(..)) || @annotation(UpdateNotificacioTable)",
			pointcut = "@annotation(UpdateNotificacioTable)", returning = "notificacio")
	public void updateNotificacioTableView(NotificacioEntity notificacio) {
		notificacioTableHelper.actualitzarRegistre(notificacio);
	}

	@Before("execution(* es.caib.notib.logic.helper.AuditNotificacioHelper.deleteNotificacio(es.caib.notib.persist.entity.NotificacioEntity)) && args(notificacio)")
	public void deleteNotificacioTableView(NotificacioEntity notificacio) {
		notificacioTableHelper.eliminarRegistre(notificacio);
	}

}