/**
 * 
 */
package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	private Boolean fiReintents;
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

	private static final long serialVersionUID = -139254994389509932L;

}
