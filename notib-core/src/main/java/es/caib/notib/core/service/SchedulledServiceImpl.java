/**
 * 
 */
package es.caib.notib.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.SchedulledService;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SchedulledServiceImpl implements SchedulledService {
	
	@Autowired
	private NotificacioService notificacioService;
	

	// 1. Enviament de notificacions pendents al registre y notific@
	////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.registre.enviaments.periode}", 
			initialDelayString = "${config:es.caib.notib.tasca.registre.enviaments.retard.inicial}")
	public void registrarEnviamentsPendents() {
		notificacioService.registrarEnviamentsPendents();
	}
	
	// 2. Enviament de notificacions registrades a Notific@
	///////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
	public void notificaEnviamentsRegistrats() {
		notificacioService.notificaEnviamentsRegistrats();
	}

	// 3. Actualització de l'estat dels enviaments amb l'estat de Notific@
	// PENDENT ELIMINAR DESPRÉS DE PROVAR ADVISER
	//////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
	public void enviamentRefrescarEstatPendents() {
		notificacioService.enviamentRefrescarEstatPendents();
	}
	// 4. Actualització de l'estat dels enviaments amb l'estat de enviat_sir
	// PENDENT ELIMINAR DESPRÉS DE PROVAR ADVISER
	//////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial}")
	public void enviamentRefrescarEstatEnviatSir() {
		notificacioService.enviamentRefrescarEstatEnviatSir();
	}
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SchedulledServiceImpl.class);

}
