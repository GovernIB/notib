package es.caib.notib.core.aspect;

import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.EnviamentTableHelper;
import es.caib.notib.core.repository.EnviamentTableRepository;
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
@Order(200)
@Component
public class EnviamentTableAdvice {
	@Autowired
	private EnviamentTableRepository enviamentTableRepository;

	@Autowired
	private EnviamentTableHelper enviamentTableHelper;

	@AfterReturning(
			pointcut = "execution(* es.caib.notib.core.helper.AuditEnviamentHelper.desa*(..))",
			returning = "enviament")
	public void addNotificacioTableView(NotificacioEnviamentEntity enviament) {
		enviamentTableHelper.crearRegistre(enviament);
	}

	@AfterReturning(
			pointcut =  "    execution(* es.caib.notib.core.helper.AuditEnviamentHelper.update*(..)) " +
						" || execution(* es.caib.notib.core.helper.AuditEnviamentHelper.reset*(..)) " +
						" || @annotation(UpdateEnviamentTable)",
			returning = "enviament")
	public void updateNotificacioTableView(NotificacioEnviamentEntity enviament) {
		enviamentTableHelper.actualitzarRegistre(enviament);
	}

	@Before(
			"    execution(* es.caib.notib.core.helper.AuditEnviamentHelper.deleteEnviament(es.caib.notib.core.entity.NotificacioEnviamentEntity)) " +
			" && args(enviament)")
	public void deleteNotificacioTableView(NotificacioEnviamentEntity enviament) {
		enviamentTableRepository.delete(enviament.getId());
	}

}
