package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDataDto;
import es.caib.notib.core.api.dto.notenviament.EnviamentInfoDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;

	private String organGestorCodi;
	private String organGestorNom;

	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
	private String csv_uuid;
	private String procedimentCodiNotib;
	private GrupDto grup;
	private String grupCodi;
	private NotificacioEstatEnumDto estat;
	protected Date estatDate;
	protected Date estatProcessatDate;
	private String serveiTipus;
	private List<EnviamentInfoDto> enviaments = new ArrayList<>();
	private String usuariCodi;
	private String motiu;
	private String numExpedient;

	private boolean eventsCallbackPendent;
	private boolean errorLastCallback;
	private TipusUsuariEnumDto tipusUsuari;
	private IdiomaEnumDto idioma;

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
	private	NotificacioEventTipusEnumDto noticaErrorEventTipus;

	private boolean justificantCreat;

	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	public boolean isEnviant() {
		return estat != null && estat.equals(NotificacioEstatEnumDto.PENDENT) && registreEnviamentIntent == 0 && !isNotificaError();
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = NotificacioEstatEnumDto.ENVIADA.equals(estat) && isComunicacioSir() ? NotificacioEstatEnumDto.ENVIAT_SIR : estat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;
}
