package es.caib.notib.persist.entity.auditoria;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.persist.audit.NotibAuditoria;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa la informació de auditoria d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="not_notificacio_env_audit")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEnviamentAudit extends NotibAuditoria<Long> {

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


	public static Builder getBuilder(NotificacioEnviamentEntity notificacioEnviamentEntity, TipusOperacio tipusOperacio, String joinPoint) {
		return new Builder(notificacioEnviamentEntity, tipusOperacio, joinPoint);
	}

	public static class Builder {
		NotificacioEnviamentAudit built;
		Builder(NotificacioEnviamentEntity notificacioEnviamentEntity, TipusOperacio tipusOperacio, String joinPoint) {
			built = new NotificacioEnviamentAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.enviamentId = notificacioEnviamentEntity.getId();
			built.notificacioId = notificacioEnviamentEntity.getNotificacio() != null ? notificacioEnviamentEntity.getNotificacio().getId() : null;
			built.titularId = notificacioEnviamentEntity.getTitular() != null ? notificacioEnviamentEntity.getTitular().getId() : null;
			built.destinataris = getDestinataris(notificacioEnviamentEntity.getDestinataris());

			built.serveiTipus = notificacioEnviamentEntity.getServeiTipus();
			if (notificacioEnviamentEntity.getEntregaPostal() != null) {
				EntregaPostalEntity entregaPostal = notificacioEnviamentEntity.getEntregaPostal();
				built.domiciliTipus = entregaPostal.getDomiciliConcretTipus();
				built.domicili = ellipsis(entregaPostal.toString(), 500);
				built.cie = entregaPostal.getDomiciliCie();
				built.formatSobre = entregaPostal.getFormatSobre();
				built.formatFulla = entregaPostal.getFormatFulla();
			}

			built.dehObligat = notificacioEnviamentEntity.getDehObligat();
			built.dehNif = notificacioEnviamentEntity.getDehNif();
			built.notificaReferencia = notificacioEnviamentEntity.getNotificaReferencia();
			built.notificaIdentificador = notificacioEnviamentEntity.getNotificaIdentificador();
			built.notificaDataCreacio = notificacioEnviamentEntity.getNotificaDataCreacio();
			built.notificaDataDisposicio = notificacioEnviamentEntity.getNotificaDataDisposicio();
			built.notificaDataCaducitat = notificacioEnviamentEntity.getNotificaDataCaducitat();
			built.notificaEmisorDir3 = notificacioEnviamentEntity.getNotificaEmisorDir3();
			built.notificaArrelDir3 = notificacioEnviamentEntity.getNotificaArrelDir3();
			built.notificaEstat = notificacioEnviamentEntity.getNotificaEstat();
			built.notificaEstatData = notificacioEnviamentEntity.getNotificaEstatData();
			built.notificaEstatFinal = notificacioEnviamentEntity.isNotificaEstatFinal();
			built.notificaDatatOrigen = notificacioEnviamentEntity.getNotificaDatatOrigen();
			built.notificaDatatReceptorNif = notificacioEnviamentEntity.getNotificaDatatReceptorNif();
			built.notificaDatatNumSeguiment = notificacioEnviamentEntity.getNotificaDatatNumSeguiment();
			built.notificaCertificacioData = notificacioEnviamentEntity.getNotificaCertificacioData();
			built.notificaCertificacioArxiuId = notificacioEnviamentEntity.getNotificaCertificacioArxiuId();
			built.notificaCertificacioOrigen = notificacioEnviamentEntity.getNotificaCertificacioOrigen();
			built.notificaCertificacioTipus = notificacioEnviamentEntity.getNotificaCertificacioTipus();
			built.notificaCertificacioArxiuTipus = notificacioEnviamentEntity.getNotificaCertificacioArxiuTipus();
			built.notificaCertificacioNumSeguiment = notificacioEnviamentEntity.getNotificaCertificacioNumSeguiment();
			built.registreNumeroFormatat = notificacioEnviamentEntity.getRegistreNumeroFormatat();
			built.registreData = notificacioEnviamentEntity.getRegistreData();
			built.registreEstatFinal = notificacioEnviamentEntity.isRegistreEstatFinal();
			built.sirConsultaData = notificacioEnviamentEntity.getSirConsultaData();
			built.sirRecepcioData = notificacioEnviamentEntity.getSirRecepcioData();
			built.sirRegDestiData = notificacioEnviamentEntity.getSirRegDestiData();
			built.notificacioErrorEvent = notificacioEnviamentEntity.getNotificacioErrorEvent() != null ? notificacioEnviamentEntity.getNotificacioErrorEvent().getId() : null;
			built.notificaError = notificacioEnviamentEntity.isNotificaError();
			built.notificaDatatErrorDescripcio = notificacioEnviamentEntity.getNotificaDatatErrorDescripcio();
		}
		public NotificacioEnviamentAudit build() {
			return built;
		}

		private String getDestinataris(List<PersonaEntity> destinataris) {

			StringBuilder destinatariIds = new StringBuilder();
			if (destinataris != null) {
				for (PersonaEntity destinatari: destinataris) {
					destinatariIds.append(destinatari.getId()).append(" - ");
				}
			}
			if (destinatariIds.length() == 0) {
				return null;
			}
			return destinatariIds.length() > 100 ? ellipsis(destinatariIds.toString(), 100) : destinatariIds.substring(0, destinatariIds.length() - 1);

		}
		private String ellipsis(final String text, int length) {
			return text.length() > length ? text.substring(0, length - 3) + "..." : text;
		}
	}

	private static final long serialVersionUID = 6993171107561077019L;
}
