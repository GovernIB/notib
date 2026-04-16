package es.caib.notib.persist.resourceentity;


import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioEnviamentAuditResource;
import es.caib.notib.logic.intf.service.AuditService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base d'auditoria d'enviaments d'una notificació
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "notificacio_env_audit")
@Getter
@Setter
@NoArgsConstructor
public class NotificacioEnviamentAuditResourceEntity extends BaseAuditableResourceEntity<NotificacioEnviamentAuditResource> {

	// TODO AQUESTS DOS CAMPS A LA ENTITY CLASSICA VENENE DE EXTESOS DE NotibAuditoria
	@Enumerated(EnumType.STRING)
	protected AuditService.TipusOperacio tipusOperacio;
	protected String joinPoint;
	// TODO ---------------------------

	@Column(name = "enviament_id")
	private Long enviamentId;
	@Column(name = "notificacio_id")
	private Long notificacioId;

	// Dades enviament
	@Column(name = "titular_id")
	private Long titularId;
	@Column(name = "destinataris_id", length = 200)
	private String destinataris;
	@Column(name = "domicili_tipus")
	@Enumerated(EnumType.STRING)
	private NotificaDomiciliConcretTipus domiciliTipus;
	@Column(name = "domicili", length = 500)
	private String domicili;
	@Column(name = "servei_tipus")
	@Enumerated(EnumType.STRING)
	private ServeiTipus serveiTipus;
	@Column(name = "cie")
	private Integer cie;
	@Column(name = "format_sobre", length = 10)
	private String formatSobre;
	@Column(name = "format_fulla", length = 10)
	private String formatFulla;
	@Column(name = "deh_obligat")
	private Boolean dehObligat;
	@Column(name = "deh_nif", length = 9)
	private String dehNif;

	// Notifica
	@Column(name = "notifica_ref", length = 36)
	private String notificaReferencia;
	@Column(name = "notifica_id", length = 20)
	private String notificaIdentificador;
	@Column(name = "notifica_datcre")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaDataCreacio;
	@Column(name = "notifica_datdisp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaDataDisposicio;
	@Column(name = "notifica_datcad")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaDataCaducitat;
	@Column(name = "notifica_emi_dir3codi", length = 9)
	private String notificaEmisorDir3;
	@Column(name = "notifica_arr_dir3codi", length = 9)
	private String notificaArrelDir3;
	// estat i datat
	@Column(name = "notifica_estat", nullable = false)
	private EnviamentEstat notificaEstat;
	@Column(name = "notifica_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEstatData;
	@Column(name = "notifica_estat_final")
	private boolean notificaEstatFinal;
	@Column(name = "notifica_datat_origen", length = 20)
	private String notificaDatatOrigen;
	@Column(name = "notifica_datat_recnif", length = 9)
	private String notificaDatatReceptorNif;
	@Column(name = "notifica_datat_numseg", length = 50)
	private String notificaDatatNumSeguiment;
	// certificació
	@Column(name = "notifica_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaCertificacioData;
	@Column(name = "notifica_cer_arxiuid", length = 50)
	private String notificaCertificacioArxiuId;
	@Column(name = "notifica_cer_origen", length = 20)
	private String notificaCertificacioOrigen;
	@Column(name = "notifica_cer_tipus")
	@Enumerated(EnumType.STRING)
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	@Column(name = "notifica_cer_arxtip")
	@Enumerated(EnumType.STRING)
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	@Column(name = "notifica_cer_numseg", length = 50)
	private String notificaCertificacioNumSeguiment;

	// Registre + SIR
	@Column(name="registre_numero_formatat", length = 50)
	private String registreNumeroFormatat;
	@Column(name="registre_data")
	private Date registreData;
	@Column(name="registre_estat")
	@Enumerated(EnumType.STRING)
	private NotificacioRegistreEstatEnumDto registreEstat;
	@Column(name="registre_estat_final")
	private boolean registreEstatFinal;
	@Column(name = "sir_con_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirConsultaData;
	@Column(name = "sir_rec_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirRecepcioData;
	@Column(name = "sir_reg_desti_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirRegDestiData;

	// Errors
	@Column(name = "notifica_error_event_id")
	private Long notificacioErrorEvent;
	@Column(name = "notifica_error", nullable = false)
	private boolean notificaError;
	@Column(name = "notifica_datat_errdes", length = 255)
	private String notificaDatatErrorDescripcio;
}
