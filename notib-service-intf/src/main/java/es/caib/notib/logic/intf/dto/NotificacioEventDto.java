/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Informació d'un event associat a una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioEventDto extends AuditoriaDto {

	private Long id;
	private Long enviamentId;
	private NotificacioEventTipusEnumDto tipus;
	private Date data;
//	private String descripcio;
	private boolean error;
	private String errorDescripcio;
	private boolean fiReintents;
	private int intents;

//	private CallbackEstatEnumDto callbackEstat;
//	private String callbackError;
//	private Integer callbackIntents;

//	private NotificacioEventEnviamentDto enviament;

//	public boolean isEnviamentAssociat() {
//		return enviament != null;
//	}
//
//	public String getCallbackError(){
//		return callbackError !=null ? callbackError : "";
//	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean isEventCie() {

		return NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(tipus)
				|| NotificacioEventTipusEnumDto.CIE_CANCELAR.equals(tipus)
				|| NotificacioEventTipusEnumDto.CIE_CONSULTA_ESTAT.equals(tipus)
				|| NotificacioEventTipusEnumDto.CIE_ADVISER.equals(tipus)
				|| NotificacioEventTipusEnumDto.CIE_ADVISER_CERTIFICACIO.equals(tipus)
				|| NotificacioEventTipusEnumDto.CIE_ADVISER_DATAT.equals(tipus);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
