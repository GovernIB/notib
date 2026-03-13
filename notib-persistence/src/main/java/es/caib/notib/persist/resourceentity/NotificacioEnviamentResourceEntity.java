package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades d'enviament d'una notificació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "notificacio_env")
@Getter
@Setter
@NoArgsConstructor
public class NotificacioEnviamentResourceEntity extends BaseAuditableResourceEntity<NotificacioEnviamentResource> {

	@Column(name = "servei_tipus")
	@Enumerated(EnumType.ORDINAL)
	private ServeiTipus serveiTipus;
	@Column(name = "notifica_ref", length = 36, unique = true)
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
	@Column(name = "plazo_ampliado")
	private boolean plazoAmpliado;
	@Column(name = "deh_obligat")
	private Boolean dehObligat;
	@Column(name = "deh_nif", length = 9)
	private String dehNif;
	@Column(name = "deh_proc_codi", length = 64)
	private String dehProcedimentCodi;
	@Column(name = "notifica_emi_dir3codi", length = 9)
	private String notificaEmisorDir3;
	@Column(name = "notifica_emi_dir3desc", length = 100)
	private String notificaEmisorDescripcio;
	@Column(name = "notifica_emi_dir3nif", length = 9)
	private String notificaEmisorNif;
	@Column(name = "notifica_arr_dir3codi", length = 9)
	private String notificaArrelDir3;
	@Column(name = "notifica_arr_dir3desc", length = 100)
	private String notificaArrelDescripcio;
	@Column(name = "notifica_arr_dir3nif", length = 9)
	private String notificaArrelNif;
	@Column(name = "notifica_estat", nullable = false)
	private EnviamentEstat notificaEstat;
	@Column(name = "notifica_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEstatData;
	@Column(name = "notifica_estat_dataact")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEstatDataActualitzacio;
	@Column(name = "notifica_estat_final")
	private boolean notificaEstatFinal;
	@Column(name = "notifica_estat_desc", length = 255)
	private String notificaEstatDescripcio;
	@Column(name = "notifica_datat_origen", length = 20)
	private String notificaDatatOrigen;
	@Column(name = "notifica_datat_recnif", length = 9)
	private String notificaDatatReceptorNif;
	@Column(name = "notifica_datat_recnom", length = 400)
	private String notificaDatatReceptorNom;
	@Column(name = "notifica_datat_numseg", length = 50)
	private String notificaDatatNumSeguiment;
	@Column(name = "notifica_datat_errdes", length = 255)
	private String notificaDatatErrorDescripcio;
	@Column(name = "notifica_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaCertificacioData;
	@Column(name = "notifica_cer_arxiuid", length = 50)
	private String notificaCertificacioArxiuId;
	@Column(name = "notifica_cer_hash", length = 50)
	private String notificaCertificacioHash;
	@Column(name = "notifica_cer_origen", length = 20)
	private String notificaCertificacioOrigen;
	@Column(name = "notifica_cer_metas", length = 255)
	private String notificaCertificacioMetadades;
	@Column(name = "notifica_cer_csv", length = 50)
	private String notificaCertificacioCsv;
	@Column(name = "notifica_cer_mime", length = 20)
	private String notificaCertificacioMime;
	@Column(name = "notifica_cer_tamany", length = 20)
	private Integer notificaCertificacioTamany;
	@Column(name = "notifica_cer_tipus")
	@Enumerated(EnumType.ORDINAL)
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	@Column(name = "notifica_cer_arxtip")
	@Enumerated(EnumType.ORDINAL)
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	@Column(name = "notifica_cer_numseg", length = 50)
	private String notificaCertificacioNumSeguiment;
	@Column(name = "notifica_error", nullable = false)
	private boolean notificaError;
	@Column(name = "notifica_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaIntentData;
	@Column(name = "notifica_intent_num")
	private int notificaIntentNum;
	@Column(name="registre_numero_formatat", length = 50)
	private String registreNumeroFormatat;
	@Column(name="registre_motiu", length = 255)
	private String registreMotiu;
	@Column(name="registre_data")
	private Date registreData;
	@Column(name="estat_registre")
	private NotificacioRegistreEstatEnumDto registreEstat;
	@Column(name="registre_estat_final")
	private boolean registreEstatFinal;
	@Column(name = "sir_con_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirConsultaData;
	@Column(name = "sir_con_intent")
	private int sirConsultaIntent;
	@Column(name="sir_fi_pooling")
	private boolean sirFiPooling;
	@Column(name = "sir_rec_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirRecepcioData;
	@Column(name = "sir_reg_desti_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sirRegDestiData;
	@Column(name = "deh_cert_intent_num")
	private int dehCertIntentNum;
	@Column(name = "deh_cert_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dehCertIntentData;
	@Column(name = "cie_cert_intent_num")
	private int cieCertIntentNum;
	@Column(name = "cie_cert_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date cieCertIntentData;
	@Column(name = "per_email")
	private boolean perEmail;
	@Column(name = "callback_error")
	private boolean errorLastCallback;
	@Column(name = "anulat")
	private boolean anulat;
	@Column(name = "motiu_anulacio", length = 250)
	private String motiuAnulacio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "notificacio_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "notificacio_notenv_fk"),
		nullable = false)
	private NotificacioResourceEntity notificacio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "titular_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "persona_notificacio_env_fk"),
		nullable = false)
	private PersonaResourceEntity titular;

	@Formula("(select ntf.concepte from " + BaseConfig.DB_PREFIX + "notificacio ntf where ntf.id = notificacio_id)")
	private String notificacioConcepte;

	@Builder
	public NotificacioEnviamentResourceEntity(
		NotificacioEnviamentResource resource,
		NotificacioResourceEntity notificacio,
		PersonaResourceEntity titular) {
		this.serveiTipus = resource.getServeiTipus();
		this.plazoAmpliado = resource.isPlazoAmpliado();
		this.perEmail = resource.isPerEmail();
		this.notificaReferencia = resource.getNotificaReferencia();
		this.notificaIdentificador = resource.getNotificaIdentificador();
		this.notificaDataCreacio = resource.getNotificaDataCreacio();
		this.notificaDataDisposicio = resource.getNotificaDataDisposicio();
		this.notificaDataCaducitat = resource.getNotificaDataCaducitat();
		this.notificaEmisorDir3 = resource.getNotificaEmisorDir3();
		this.notificaEmisorDescripcio = resource.getNotificaEmisorDescripcio();
		this.notificaEmisorNif = resource.getNotificaEmisorNif();
		this.notificaArrelDir3 = resource.getNotificaArrelDir3();
		this.notificaArrelDescripcio = resource.getNotificaArrelDescripcio();
		this.notificaArrelNif = resource.getNotificaArrelNif();
		this.notificaEstat = resource.getNotificaEstat();
		this.notificaEstatData = resource.getNotificaEstatData();
		this.notificaEstatDataActualitzacio = resource.getNotificaEstatDataActualitzacio();
		this.notificaEstatFinal = resource.isNotificaEstatFinal();
		this.notificaEstatDescripcio = resource.getNotificaEstatDescripcio();
		this.notificaDatatOrigen = resource.getNotificaDatatOrigen();
		this.notificaDatatReceptorNif = resource.getNotificaDatatReceptorNif();
		this.notificaDatatReceptorNom = resource.getNotificaDatatReceptorNom();
		this.notificaDatatNumSeguiment = resource.getNotificaDatatNumSeguiment();
		this.notificaDatatErrorDescripcio = resource.getNotificaDatatErrorDescripcio();
		this.notificaCertificacioData = resource.getNotificaCertificacioData();
		this.notificaCertificacioArxiuId = resource.getNotificaCertificacioArxiuId();
		this.notificaCertificacioHash = resource.getNotificaCertificacioHash();
		this.notificaCertificacioOrigen = resource.getNotificaCertificacioOrigen();
		this.notificaCertificacioMetadades = resource.getNotificaCertificacioMetadades();
		this.notificaCertificacioCsv = resource.getNotificaCertificacioCsv();
		this.notificaCertificacioMime = resource.getNotificaCertificacioMime();
		this.notificaCertificacioTamany = resource.getNotificaCertificacioTamany();
		this.notificaCertificacioTipus = resource.getNotificaCertificacioTipus();
		this.notificaCertificacioArxiuTipus = resource.getNotificaCertificacioArxiuTipus();
		this.notificaCertificacioNumSeguiment = resource.getNotificaCertificacioNumSeguiment();
		this.notificaError = resource.isNotificaError();
		this.notificaIntentData = resource.getNotificaIntentData();
		this.notificaIntentNum = resource.getNotificaIntentNum();
		this.registreNumeroFormatat = resource.getRegistreNumeroFormatat();
		this.registreMotiu = resource.getRegistreMotiu();
		this.registreData = resource.getRegistreData();
		this.registreEstat = resource.getRegistreEstat();
		this.registreEstatFinal = resource.isRegistreEstatFinal();
		this.sirConsultaData = resource.getSirRecepcioData();
		this.sirConsultaIntent = resource.getSirConsultaIntent();
		this.sirFiPooling = resource.isSirFiPooling();
		this.sirRecepcioData = resource.getSirRecepcioData();
		this.sirRegDestiData = resource.getSirRegDestiData();
		this.dehObligat = resource.getDehObligat();
		this.dehNif = resource.getDehNif();
		this.dehProcedimentCodi = resource.getDehProcedimentCodi();
		this.dehCertIntentNum = resource.getDehCertIntentNum();
		this.dehCertIntentData = resource.getDehCertIntentData();
		this.cieCertIntentNum = resource.getCieCertIntentNum();
		this.cieCertIntentData = resource.getCieCertIntentData();
		this.errorLastCallback = resource.isErrorLastCallback();
		this.anulat = resource.isAnulat();
		this.motiuAnulacio = resource.getMotiuAnulacio();
		this.notificacio = notificacio;
		this.titular = titular;
	}

}
