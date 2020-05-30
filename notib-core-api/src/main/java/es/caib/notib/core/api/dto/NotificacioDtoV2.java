/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotificacioDtoV2 extends AuditoriaDto {

	private Long id;
	private String emisorDir3Codi;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
	private DocumentDto document;
	private String csv_uuid;
	private ProcedimentDto procediment;
	private String procedimentCodiNotib;
	private GrupDto grup;
	private String grupCodi;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private String serveiTipus;
	private List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
	private String usuariCodi;
	private String motiu;
	private String numExpedient;
	private boolean permisProcessar;
	private EntitatDto entitat;
	private boolean errorLastCallback;
	private TipusUsuariEnumDto tipusUsuari;
	
	
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
