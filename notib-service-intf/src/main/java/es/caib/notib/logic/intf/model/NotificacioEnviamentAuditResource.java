package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentEstat;
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
import es.caib.notib.logic.intf.service.AuditService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * Informació de l'auditoria d'enviaments d'una notificació
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
public class NotificacioEnviamentAuditResource extends BaseResource<Long> {

	protected AuditService.TipusOperacio tipusOperacio;
	protected String joinPoint;

	private String createdBy;
	private LocalDateTime createdDate;

	private Long enviamentId;
	private Long notificacioId;

	// Dades enviament
	private Long titularId;
	private String destinataris;
	private NotificaDomiciliConcretTipus domiciliTipus;
	private String domicili;
	private ServeiTipus serveiTipus;
	private Integer cie;
	private String formatSobre;
	private String formatFulla;
	private Boolean dehObligat;
	private String dehNif;

	// Notifica
	private String notificaReferencia;
	private String notificaIdentificador;
	private LocalDateTime notificaDataCreacio;
	private LocalDateTime notificaDataDisposicio;
	private LocalDateTime notificaDataCaducitat;
	private String notificaEmisorDir3;
	private String notificaArrelDir3;
	// estat i datat
	private EnviamentEstat notificaEstat;
	private LocalDateTime notificaEstatData;
	private LocalDateTime notificaEstatFinal;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatNumSeguiment;
	// certificació
	private LocalDateTime notificaCertificacioData;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioOrigen;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;

	// Registre + SIR
	private String registreNumeroFormatat;
	private LocalDateTime registreData;
	private NotificacioRegistreEstatEnumDto registreEstat;
	private boolean registreEstatFinal;
	private LocalDateTime sirConsultaData;
	private LocalDateTime sirRecepcioData;
	private LocalDateTime sirRegDestiData;

	// Errors
	private Long notificacioErrorEvent;
	private boolean notificaError;
	private String notificaDatatErrorDescripcio;
}
