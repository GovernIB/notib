/**
 * 
 */
package es.caib.notib.logic.intf.dto.notenviament;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEnviamentDatatableDto {

	private Long id;

	private PersonaDto titular;
	private List<PersonaDto> destinataris;

	private EnviamentEstat notificaEstat;
	private String estatColor;
	private String estatIcona;
	private Date notificaEstatData;

	private NotificacioEstatEnumDto notificacioEstat;
	private Date notificacioErrorData;
	private String notificacioErrorDescripcio;
	private Date notificaCertificacioData;

	private NotificacioRegistreEstatEnumDto registreEstat;
	private Date registreData;
	private String registreNumeroFormatat;
	private boolean perEmail;

	private boolean fiReintents;
	private String fiReintentsDesc;

	private NotificacioEventDto ultimEvent;
	private boolean ultimEventError;
	private boolean errorLastCallback;
	private boolean callbackFiReintents;
	private String callbackFiReintentsDesc;
	private String  notificacioMovilErrorDesc;

	public boolean isUltimEventError() {
		return ultimEvent != null && ultimEvent.isError();
	}


	public boolean isNotificacioError() {
		return notificacioErrorData != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
