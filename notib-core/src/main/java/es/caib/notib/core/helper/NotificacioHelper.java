package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


/**
 * Helper per registrar/notificar Notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
}
