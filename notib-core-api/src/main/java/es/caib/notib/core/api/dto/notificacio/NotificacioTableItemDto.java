/**
 * 
 */
package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
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
public class NotificacioTableItemDto {

	private Long id;
	private TipusUsuariEnumDto tipusUsuari;
	private boolean errorLastCallback;
	private boolean hasEnviamentsPendentsRegistre;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;

	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String numExpedient;
	private String concepte;
	private Date estatDate;
	private NotificacioEstatEnumDto estat;

	private String createdByNom;
	private String createdByCodi;
	private Date createdDate;

	private boolean permisProcessar;

	private String entitatNom;
	private String procedimentCodi;
	private String procedimentNom;
	private String organCodi;
	private String organNom;

	protected int registreEnviamentIntent;


	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	public boolean isEnviant() {
		return estat != null && estat.equals(NotificacioEstatEnumDto.PENDENT) && registreEnviamentIntent == 0 && !isNotificaError();
	}

	public String getOrganGestorDesc() {
		if (organNom != null && !organNom.isEmpty())
			return organCodi + " - " + organNom;
		return organCodi;
	}

	public String getProcedimentDesc() {
		String procedimentDesc = "";
		if (procedimentCodi != null && !procedimentCodi.isEmpty())
			procedimentDesc = procedimentCodi;
		if (procedimentNom != null && !procedimentNom.isEmpty())
			procedimentDesc += " - " + procedimentNom;
		return procedimentDesc;
	}

	public String getCreatedByComplet() {
		String nomComplet = "";
		if (createdByNom != null && !createdByNom.isEmpty())
			nomComplet += createdByNom + " ";
		if (createdByCodi != null && !createdByCodi.isEmpty())
			nomComplet += "(" + createdByCodi + ")";
		return nomComplet;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
