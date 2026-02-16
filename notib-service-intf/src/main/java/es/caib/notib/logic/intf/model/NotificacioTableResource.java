package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * Informació provinent de la taula optimitzada de notificacions.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	//descriptionField = NotificacioTableResource.Fields.codi,
	//quickFilterFields = { NotificacioTableResource.Fields.codi, NotificacioTableResource.Fields.nom },
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
public class NotificacioTableResource extends BaseResource<Long> {

	private TipusUsuariEnumDto tipusUsuari;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private EnviamentTipus enviamentTipus;
	private String numExpedient;
	protected int registreEnviamentIntent;
	private String concepte;
	private NotificacioEstatEnumDto estat;
	private Date estatDate;
	private String entitatNom;
	private String procedimentCodi;
	private String procedimentNom;
	private boolean procedimentIsComu;
	private boolean procedimentRequirePermission;
	private ProcSerTipusEnum procedimentTipus;
	private String organId;
	private String organCodi;
	private String organNom;
	private OrganGestorEstatEnum organEstat;
	private boolean isLastEventFiReintents;
	private boolean isErrorLastEvent;
	private Date estatProcessatDate;
	private Date enviadaDate;
	private String referencia;
	private String titular;
	private String notificaIds;
	private String registreNums;
	private Integer estatMask;
	private String estatString;
	private Long documentId;
	private Date envCerData;
	private boolean hasEnviamentsPendentsRegistre;
	private boolean perActualitzar;
	private boolean deleted;
	private boolean entregaPostal;
	private boolean entregaPostalError;
	private boolean anulable;

	// Camps no presents a l'entitat JPA
	private String estatColor = "green";
	private boolean errorLastCallback;
	private boolean permisProcessar;
	private boolean comunicacioSir;

}
