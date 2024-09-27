package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.notenviament.EnviamentInfo;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioInfoDto extends AuditoriaDto {

	private Long id;
	private EnviamentTipus enviamentTipus;
	private String concepte;
	private String descripcio;

	private String organGestorCodi;
	private String organGestorNom;

	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
	private Date caducitatOriginal;
	private String csv_uuid;
	private String procedimentCodiNotib;
	private GrupDto grup;
	private String grupCodi;
	private NotificacioEstatEnumDto estat;
	protected Date estatDate;
	protected Date estatProcessatDate;
	private String serveiTipus;
	private List<EnviamentInfo> enviaments = new ArrayList<>();
	private String usuariCodi;
	private String usuariNom;
	private String motiu;
	private String numExpedient;

	private boolean eventsCallbackPendent;
	private String dataCallbackPendent;
	private TipusUsuariEnumDto tipusUsuari;
	private Idioma idioma;

	// Documents de la notificació
	private DocumentDto document;
	private DocumentDto document2;
	private DocumentDto document3;
	private DocumentDto document4;
	private DocumentDto document5;

	// Dades del procediment
	private ProcSerDataDto procediment;
	private OperadorPostalDataDto operadorPostal;
	private CieDataDto cie;

	private boolean hasEnviamentsPendents;

	private boolean comunicacioSir;

	private String registreLlibreNom;
	private String registreOficinaNom;
	private int registreEnviamentIntent;

	private Date notificaEnviamentData;
	private Date notificaEnviamentNotificaData;

	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private NotificacioEventTipusEnumDto noticaErrorEventTipus;
	private boolean fiReintents;
	private String fiReintentsDesc;
	private boolean errorLastCallback;
	private boolean callbackFiReintents;
	private String callbackFiReintentsDesc;
	private List<String> notificacionsMovilErrorDesc = new ArrayList<>();

	private Date enviadaDate;

	private boolean justificantCreat;

	public boolean isNotificaError() {

		if (comunicacioSir) {
			for (EnviamentInfo e : enviaments) {
				if (e.isNotificacioError()) {
					notificaErrorDescripcio = e.getNotificacioErrorDescripcio();
					return true;
				}
			}
		}
		return notificaErrorData != null;
	}

	public boolean isEnviant() {
		return estat != null && estat.equals(NotificacioEstatEnumDto.PENDENT) && registreEnviamentIntent == 0 && !isNotificaError();
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = NotificacioEstatEnumDto.ENVIADA.equals(estat) && isComunicacioSir() ? NotificacioEstatEnumDto.ENVIAT_SIR : estat;
	}

	public boolean isUsuariWeb() {
		return TipusUsuariEnumDto.INTERFICIE_WEB.equals(tipusUsuari);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;
}
