package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Informació de l'auditoria d'una notificació
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
			roles = { BaseConfig.ROLE_ADMIN , BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_ORGAN},
			grantedPermissions = { PermissionEnum.READ }
		)
	}
)
public class NotificacioAuditResource extends BaseResource<Long> {

	protected AuditService.TipusOperacio tipusOperacio;
	protected String joinPoint;

	private String createdBy;
	private LocalDateTime createdDate;

	private Long notificacioId;

	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	private TipusUsuariEnumDto tipusUsuari;
	private String usuari;
	private String emisor;
	private EnviamentTipus tipus;
	private Long entitatId;
	private String organ;
	private String procediment;
	private String grup;
	private String concepte;
	private String descripcio;
	private String numExpedient;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private Long documentId;
	private NotificacioEstatEnumDto estat;
	private Date estatDate;
	private Date estatProcessatDate;
	private String motiu;

	// Registre
	private int registreEnviamentIntent;
	private Integer registreNumero;
	private String registreNumeroFormatat;
	private Date registreData;

	// Notifica
	private Date notificaEnviamentData;
	private int notificaEnviamentIntent;

	// Errors
	private boolean errorLastCallback;
	private Long errorEventId;
	protected String referencia;
}
