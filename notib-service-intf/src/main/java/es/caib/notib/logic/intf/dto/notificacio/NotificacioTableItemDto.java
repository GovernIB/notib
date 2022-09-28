/**
 * 
 */
package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	private String estatString;

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

	private int nTramitacio;
	private int nCompareixenca;
	private int nLlegida;
	private int nRebutjada;
	private int nExpirada;
	private int nAnulada;
	private int nError;
	private int nFinalitzada;
	private int nProcessada;

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
		return estat != null && !estat.equals(NotificacioEstatEnumDto.PENDENT) && !estat.equals(NotificacioEstatEnumDto.ENVIANT) && !estat.equals(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS);
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

		switch (estat) {

			case NOTIB_PENDENT:
			case REGISTRADA:
			case NOTIB_ENVIADA:
			case ENVIAMENT_PROGRAMAT:
				nTramitacio++;
				break;
			case ENVIADA:
			case ENVIADA_CI:
			case ENVIADA_DEH:
			case PENDENT:
			case ENTREGADA_OP:
			case PENDENT_ENVIAMENT:
			case PENDENT_SEU:
			case PENDENT_CIE:
			case PENDENT_DEH:
				nCompareixenca++;
				break;
			case LLEGIDA:
				nLlegida++;
				break;
			case REBUTJADA:
				nRebutjada++;
				break;
			case EXPIRADA:
				nExpirada++;
				break;
			case ANULADA:
				nAnulada++;
				break;
			case FINALITZADA:
				nFinalitzada++;
				break;
			case PROCESSADA:
				nProcessada++;
				break;
			default:
				nError++;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
