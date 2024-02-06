package es.caib.notib.logic.intf.dto.notenviament;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un destinatari d'una anotació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class NotEnviamentTableItemDto implements Serializable {

	private Long id;
	private Date createdDate;
	private Date enviadaDate;

	private String destinataris;
	private Date notificaDataCaducitat;
	private EnviamentTipus tipusEnviament;

	private String codiNotibEnviament;

	// Titular
	private String titularNif;
	private String titularNom;
	private String titularEmail;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularRaoSocial;

	//Info notificació
	private Date enviamentDataProgramada;
	private String procedimentNom;
	private String procedimentCodiNotib;
	private String procedimentCodiNom;
	private ProcSerTipusEnum procedimentTipus;
	private String grupCodi;
	private String organNom;
	private String organCodi;
	private String organCodiNom;
	private OrganGestorEstatEnum organEstat;
	private String usuariCodi;
	private String concepte;
	private String descripcio;
	private String llibre;
	private NotificacioEstatEnumDto estat;
	private Long notificacioId;
	private String csvUuid;
	private String referenciaNotificacio;

	//Registre
	private Integer registreNumero;
	private Date registreData;

	//Notific@
	private String notificaIdentificador;
	private String notificaCertificacioNumSeguiment;

	private boolean isEnviant;

	public NotEnviamentTableItemDto() {
	}

	public NotificacioEstatEnumDto getEstat() {
		if (isEnviant){
			return NotificacioEstatEnumDto.ENVIANT;
		}
		return this.estat;
	}

	public String getProcedimentCodiNom() {
		return procedimentCodiNotib + " - " + procedimentNom;
	}

	public String getOrganCodiNom() {
		return organCodi + " - " + organNom;
	}

	public String getTitularNomLlinatge() {
		return PersonaDto.builder()
				.nom(titularNom)
				.llinatge1(titularLlinatge1)
				.llinatge2(titularLlinatge2)
				.nif(titularNif)
				.raoSocial(titularRaoSocial)
				.email(titularEmail)
				.build().getNomFormatted();
	}

	private static final long serialVersionUID = -139254994389509932L;

}