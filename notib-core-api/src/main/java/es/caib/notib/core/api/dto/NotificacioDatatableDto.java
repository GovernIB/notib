/**
 * 
 */
package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioDatatableDto {

	private Long id;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private Date estatDate;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private Long entitatId;
	private String entitatNom;
	private String procedimentCodi;
	private String procedimentNom;
	private boolean permisProcessar;
	private String numExpedient;
	private String organGestor;
	private String organGestorNom;

	private String createdByNom;
	private String createdByCodi;
	private Date createdDate;

	private boolean errorLastCallback;
	private boolean errorLastEvent;
//	private boolean hasEnviamentsPendents;
	private boolean hasEnviamentsPendentsRegistre;
	protected int notificaEnviamentIntent;
	
	private TipusUsuariEnumDto tipusUsuari;
	
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	public boolean isEnviant() {
		return estat.equals(NotificacioEstatEnumDto.PENDENT) && notificaEnviamentIntent == 0;
	}

	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getCreatedByComplet() {
		String nomComplet = "";
		if (createdByNom != null && !createdByNom.isEmpty())
			nomComplet += createdByNom + " ";
		if (createdByCodi != null && !createdByCodi.isEmpty())
			nomComplet += "(" + createdByCodi + ")";
		return nomComplet;
	}

	public String getProcedimentDesc() {
		String procedimentDesc = "";
		if (procedimentCodi != null && !procedimentCodi.isEmpty())
			procedimentDesc = procedimentCodi;
		if (procedimentNom != null & !procedimentNom.isEmpty())
			procedimentDesc += " - " + procedimentNom;
		return procedimentDesc;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
