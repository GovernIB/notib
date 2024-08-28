package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.AuditoriaDto;
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

	protected Long id;
	protected String emisorDir3Codi;
	protected EnviamentTipus enviamentTipus;
	protected String concepte;
	protected String descripcio;
	protected String organGestor;
	protected String organGestorNom;
	protected Date enviamentDataProgramada;
	protected Integer retard;
	protected int notificaEnviamentIntent;
	protected Date caducitat;
	protected String csv_uuid;
	protected ProcSerDto procediment;
	protected String procedimentCodiNotib;
	protected GrupDto grup;
	protected String grupCodi;
	protected NotificacioEstatEnumDto estat;
	protected Date notificaErrorData;
	protected String notificaErrorDescripcio;
//	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	protected String serveiTipus;
	protected List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<>();
	protected String usuariCodi;
	protected String motiu;
	protected String numExpedient;
	protected boolean permisProcessar;
	protected EntitatDto entitat;
	protected boolean errorLastCallback;
	protected TipusUsuariEnumDto tipusUsuari;
	protected Date notificaEnviamentData;
	protected Date notificaEnviamentNotificaData;
	protected Idioma idioma;

	protected Document document;
	protected Document document2;
	protected Document document3;
	protected Document document4;
	protected Document document5;

	protected boolean hasEnviamentsPendents;
	protected boolean justificantCreat;
	protected boolean deleted;

	protected String registreLlibreNom;
	protected String registreOficinaNom;

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
