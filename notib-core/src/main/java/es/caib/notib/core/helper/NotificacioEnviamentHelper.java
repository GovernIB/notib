package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;


/**
 * Helper per a convertir notificació entity i enviament entity a notificacioEnviamentDto.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioEnviamentHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	public NotificacioEnviamentDtoV2 toNotificacioEnviamentDto(
			NotificacioEntity notificacio,
			Page<NotificacioEnviamentEntity> enviamentsPage) {
		
		NotificacioEnviamentDtoV2 enviaments = null;
		
		if (!enviamentsPage.getContent().isEmpty()) {
			enviaments = new NotificacioEnviamentDtoV2();
			
			for (NotificacioEnviamentEntity enviament : enviamentsPage.getContent()) {
				enviaments.setId(enviament.getId());
				enviaments.setCreatedDate(enviament.getCreatedDate().toDate());
				enviaments.setNotificaIdentificador(enviament.getNotificaIdentificador());
				enviaments.setUsuari(enviament.getCreatedBy().getCodi());
				enviaments.setNotificacio(
						conversioTipusHelper.convertir(
								notificacio, 
								NotificacioDtoV2.class));
				enviaments.setTitularNom(enviament.getTitularNom());
				enviaments.setTitularLlinatge1(enviament.getTitularLlinatge1());
				enviaments.setTitularLlinatge2(enviament.getTitularLlinatge2());
				enviaments.setTitularNif(enviament.getTitularNif());
				enviaments.setTitularRaoSocial(enviament.getTitularRaoSocial());
				enviaments.setTitularEmail(enviament.getTitularEmail());
				enviaments.setDestinatariNom(enviament.getDestinatariNom());
				enviaments.setDestinatariLlinatge1(enviament.getDestinatariLlinatge1());
				enviaments.setDestinatariLlinatge2(enviament.getDestinatariLlinatge2());
				enviaments.setDestinatariNif(enviament.getDestinatariNif());
				enviaments.setDestinatariRaoSocial(enviament.getDestinatariRaoSocial());
				enviaments.setDestinatariEmail(enviament.getDestinatariEmail());
			}
		}
		return enviaments;
		
	}
}
