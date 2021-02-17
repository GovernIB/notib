/**
 * 
 */
package es.caib.notib.core.api.dto;

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
public class NotificacioDtoV2 extends AuditoriaDto {

	private Long id;
	private String emisorDir3Codi;
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private String organGestor;
	private String organGestorNom;
	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
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
	private Date notificaEnviamentData;
	private Date notificaEnviamentNotificaData;
	private IdiomaEnumDto idioma;

	private DocumentDto document;
	private DocumentDto document2;
	private DocumentDto document3;
	private DocumentDto document4;
	private DocumentDto document5;
	
	private boolean errorLastEvent;
	private boolean hasEnviamentsPendents;
	
	public boolean isNotificaError() {
		return notificaErrorData != null;
	}
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
