/**
 * 
 */
package es.caib.notib.core.api.dto.notenviament;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

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

	private NotificacioEnviamentEstatEnumDto notificaEstat;
	private Date notificaEstatData;

	private NotificacioEstatEnumDto notificacioEstat;
	private Date notificacioErrorData;
	private String notificacioErrorDescripcio;
	private Date notificaCertificacioData;

	private NotificacioRegistreEstatEnumDto registreEstat;
	private Date registreData;
	private String registreNumeroFormatat;

	public boolean isNotificacioError() {
		return notificacioErrorData != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
