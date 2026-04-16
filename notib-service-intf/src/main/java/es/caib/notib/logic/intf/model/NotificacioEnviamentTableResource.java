package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Informació provinent de la taula optimitzada d'enviaments.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_SUPER },
			grantedPermissions = { PermissionEnum.READ }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_USER },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE }
		),
	}
)
public class NotificacioEnviamentTableResource extends BaseResource<Long> {
//
//	private NotificacioEnviamentEntity enviament;
//	private NotificacioEntity notificacio;
//	private EntitatResourceEntity entitat;

	private Long notificacioId;
	private Date enviadaDate;
	protected EnviamentTipus tipusEnviament;
	protected String destinataris;
	private String procedimentCodiNotib;
	private String usuariCodi;
	private String grupCodi;
	private String titularNif;
	private String titularNom;
	private String titularEmail;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularRaoSocial;
	protected Date enviamentDataProgramada;
	protected String emisorDir3Codi;
	protected String concepte;
	protected String descripcio;
	private String registreLlibreNom;
	private String organCodi;
	private String organId;
	private String organNom;
	private OrganGestorEstatEnum organEstat;
	private String procedimentNom;
	private String referenciaNotificacio;
	protected NotificacioEstatEnumDto estat;
	protected String csv_uuid;
	protected Boolean hasErrors;
	private Boolean procedimentIsComu;
	protected Long procedimentOrganId;
	private boolean procedimentRequirePermission;
	private ProcSerTipusEnum procedimentTipus;
	protected String registreNumero;
	public void registreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	protected Date registreData;
	protected Integer registreEnviamentIntent;
	protected Date notificaDataCaducitat;
	protected String notificaIdentificador;
	protected String notificaCertificacioNumSeguiment;
	protected EnviamentEstat notificaEstat;
	protected String notificaReferencia;
	protected boolean errorLastCallback;
	protected boolean entregaPostal;
	protected Boolean anulable;
	protected boolean anulat;
	protected String motiuAnulacio;

	private LocalDateTime createdDate;
	private String createdBy;
}
