/**
 * 
 */
package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificacioTableItemDto {

	private Long id;
	private TipusUsuariEnumDto tipusUsuari;
	private boolean errorLastCallback;
	private boolean hasEnviamentsPendentsRegistre;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;

	private EnviamentTipus enviamentTipus;
	private String numExpedient;
	private String concepte;
	private Date estatDate;
	private NotificacioEstatEnumDto estat;
	private String estatString;
	private String estatColor = "green";

	private String createdByNom;
	private String createdByCodi;
	private Date createdDate;

	private boolean permisProcessar;

	private boolean comunicacioSir;

	private String entitatNom;
	private String procedimentCodi;
	private String procedimentNom;
	private ProcSerTipusEnum procedimentTipus;
	private String organCodi;
	private String organNom;
	private OrganGestorEstatEnum organEstat;
	private Date estatProcessatDate;
	private Date enviadaDate;

	protected int registreEnviamentIntent;

	private Long documentId;
	private Date envCerData;
	private String referencia;

	private String titular;
	private String registreNums;

	private boolean deleted;

	private boolean entregaPostal;


	private Map<EnviamentEstat, Integer> contadorEstat = new HashMap<>();

	public String getEstatColor()  {

		return estat.getColor();
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = NotificacioEstatEnumDto.ENVIADA.equals(estat) && isComunicacioSir() ? NotificacioEstatEnumDto.ENVIAT_SIR : estat;
	}

	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	public boolean isEnviant() {
		return estat != null && estat.equals(NotificacioEstatEnumDto.PENDENT) && registreEnviamentIntent == 0 && !isNotificaError();
	}

	public boolean isJustificant() {
		return estat != null && !estat.equals(NotificacioEstatEnumDto.PENDENT) && !estat.equals(NotificacioEstatEnumDto.ENVIANT)
				&& !estat.equals(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS) && !estat.equals(NotificacioEstatEnumDto.REGISTRADA);
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

	public void updateEstatTipusCount(EnviamentEstat estat) {

		if (!contadorEstat.containsKey(estat)) {
			contadorEstat.put(estat, 1);
			return;
		}
		contadorEstat.put(estat, contadorEstat.get(estat) + 1);
	}

	public boolean isPlazoAmpliable() {
		return !entregaPostal && NotificacioEstatEnumDto.ENVIADA.equals(estat);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
