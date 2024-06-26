package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
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
public class NotificacioDtoV2 extends AuditoriaDto {

	private Long id;
	private String emisorDir3Codi;
	private EnviamentTipus enviamentTipus;
	private String concepte;
	private String descripcio;
	private String organGestor;
	private String organGestorNom;
	private Date enviamentDataProgramada;
	private Integer retard;
	protected int notificaEnviamentIntent;
	private Date caducitat;
	private String csv_uuid;
	private ProcSerDto procediment;
	private String procedimentCodiNotib;
	private GrupDto grup;
	private String grupCodi;
	private NotificacioEstatEnumDto estat;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
//	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	private String serveiTipus;
	private List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<>();
	private String usuariCodi;
	private String motiu;
	private String numExpedient;
	private boolean permisProcessar;
	private EntitatDto entitat;
	private boolean errorLastCallback;
	private TipusUsuariEnumDto tipusUsuari;
	private Date notificaEnviamentData;
	private Date notificaEnviamentNotificaData;
	private Idioma idioma;

	private Document document;
	private Document document2;
	private Document document3;
	private Document document4;
	private Document document5;

	private boolean hasEnviamentsPendents;
	private boolean justificantCreat;
	private boolean deleted;

	private String registreLlibreNom;
	private String registreOficinaNom;

	public boolean isNotificaError() {
		return notificaErrorData != null;
	}
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}

	public List<NotificacioEnviamentDtoV2> getEnviamentsFinalitzats() {
		List<NotificacioEnviamentDtoV2> enviamentsFinalitzats = new ArrayList<>();
		if (enviaments != null && !enviaments.isEmpty()) {
			for(NotificacioEnviamentDtoV2 enviament: enviaments) {
				if (enviament.isNotificaEstatFinal()) {
					enviamentsFinalitzats.add(enviament);
				}
			}
		}
		return enviamentsFinalitzats;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
