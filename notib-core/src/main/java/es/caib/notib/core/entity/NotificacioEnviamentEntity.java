package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;

/**
 * Classe del model de dades que representa els enviaments d'una
 * notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="not_notificacio_env")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEnviamentEntity extends NotibAuditable<Long> {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_id")
	@ForeignKey(name = "NOT_NOTIFICACIO_NOTENV_FK")
	@NotFound(action = NotFoundAction.IGNORE)
	protected NotificacioEntity notificacio;
	
//	@Column(name="notificacio_id", insertable=false, updatable=false)
//	protected Long notificacioId;
	
	/* Titular */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "titular_id")
	@ForeignKey(name = "not_persona_notificacio_env_fk")
	protected PersonaEntity titular;
	
	/* Destinataris */
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
	@ForeignKey(name = "not_persona_not_fk")
    @JoinColumn(name = "notificacio_env_id") // we need to duplicate the physical information
	@NotFound(action = NotFoundAction.IGNORE)
	protected List<PersonaEntity> destinataris = new ArrayList<PersonaEntity>();
	
	/* Domicili */
	@Column(name = "dom_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliTipusEnumDto domiciliTipus;
	
	@Column(name = "dom_con_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus;
	
	@Column(name = "dom_via_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliViaTipusEnumDto domiciliViaTipus;
	
	@Column(name = "dom_via_nom", length = 50)
	protected String domiciliViaNom;
	@Column(name = "dom_num_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus;
	
	@Column(name = "dom_num_num", length = 5)
	protected String domiciliNumeracioNumero;
	
	@Column(name = "dom_num_qualif", length = 3)
	protected String domiciliNumeracioQualificador;
	
	@Column(name = "dom_num_puntkm", length = 10)
	protected String domiciliNumeracioPuntKm;
	
	@Column(name = "dom_apartat", length = 10)
	protected String domiciliApartatCorreus;
	
	@Column(name = "dom_bloc", length = 50)
	protected String domiciliBloc;
	
	@Column(name = "dom_portal", length = 50)
	protected String domiciliPortal;
	
	@Column(name = "dom_escala", length = 50)
	protected String domiciliEscala;
	
	@Column(name = "dom_planta", length = 50)
	protected String domiciliPlanta;
	
	@Column(name = "dom_porta", length = 50)
	protected String domiciliPorta;
	
	@Column(name = "dom_complem", length = 250)
	protected String domiciliComplement;
	
	@Column(name = "dom_poblacio", length = 255)
	protected String domiciliPoblacio;
	
	@Column(name = "dom_mun_codine", length = 6)
	protected String domiciliMunicipiCodiIne;
	
	@Column(name = "dom_mun_nom", length = 64)
	protected String domiciliMunicipiNom;
	
	@Column(name = "dom_codi_postal", length = 10)
	protected String domiciliCodiPostal;
	
	@Column(name = "dom_prv_codi", length = 2)
	protected String domiciliProvinciaCodi;
	
	@Column(name = "dom_prv_nom", length = 64)
	protected String domiciliProvinciaNom;
	
	@Column(name = "dom_pai_codiso", length = 3)
	protected String domiciliPaisCodiIso; // ISO-3166
	
	@Column(name = "dom_pai_nom", length = 64)
	protected String domiciliPaisNom;
	
	@Column(name = "dom_linea1", length = 50)
	protected String domiciliLinea1;
	
	@Column(name = "dom_linea2", length = 50)
	protected String domiciliLinea2;
	
	@Column(name = "dom_cie")
	protected Integer domiciliCie;
	
	/* DEH */
	@Column(name = "deh_obligat")
	protected Boolean dehObligat;
	
	@Column(name = "deh_nif", length = 9)
	protected String dehNif;
	
	@Column(name = "deh_proc_codi", length = 64)
	protected String dehProcedimentCodi;
	
	/* Altres */
	@Column(name = "servei_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected ServeiTipusEnumDto serveiTipus;
	
	@Column(name = "format_sobre", length = 10)
	protected String formatSobre;
	
	@Column(name = "format_fulla", length = 10)
	protected String formatFulla;
	
	/* Notifica informació */
	@Column(name = "notifica_ref", length = 20)
	protected String notificaReferencia;
	
	@Column(name = "notifica_id", length = 20)
	protected String notificaIdentificador;
	
	@Column(name = "notifica_datcre")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataCreacio;
	
	@Column(name = "notifica_datdisp")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataDisposicio;
	
	@Column(name = "notifica_datcad")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaDataCaducitat;
	
	@Column(name = "notifica_emi_dir3codi", length = 9)
	protected String notificaEmisorDir3;
	
	@Column(name = "notifica_emi_dir3desc", length = 100)
	protected String notificaEmisorDescripcio;
	
	@Column(name = "notifica_emi_dir3nif", length = 9)
	protected String notificaEmisorNif;
	
	@Column(name = "notifica_arr_dir3codi", length = 9)
	protected String notificaArrelDir3;
	
	@Column(name = "notifica_arr_dir3desc", length = 100)
	protected String notificaArrelDescripcio;
	
	@Column(name = "notifica_arr_dir3nif", length = 9)
	protected String notificaArrelNif;
	
	/* Notifica estat i datat */
	@Column(name = "notifica_estat", nullable = false)
	protected NotificacioEnviamentEstatEnumDto notificaEstat;
	
	@Column(name = "notifica_estat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEstatData;
	
	@Column(name = "notifica_estat_dataact")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEstatDataActualitzacio;
	
	@Column(name = "notifica_estat_final")
	protected boolean notificaEstatFinal;
	
	@Column(name = "notifica_estat_desc", length = 255)
	protected String notificaEstatDescripcio;
	
	@Column(name = "notifica_datat_origen", length = 20)
	protected String notificaDatatOrigen;
	
	@Column(name = "notifica_datat_recnif", length = 9)
	protected String notificaDatatReceptorNif;
	
	@Column(name = "notifica_datat_recnom", length = 400)
	protected String notificaDatatReceptorNom;
	
	@Column(name = "notifica_datat_numseg", length = 50)
	protected String notificaDatatNumSeguiment;
	
	@Column(name = "notifica_datat_errdes", length = 255)
	protected String notificaDatatErrorDescripcio;
	
	/* Notifica certificació */
	@Column(name = "notifica_cer_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaCertificacioData;
	
	@Column(name = "notifica_cer_arxiuid", length = 50)
	protected String notificaCertificacioArxiuId;
	
	@Column(name = "notifica_cer_hash", length = 50)
	protected String notificaCertificacioHash;
	
	@Column(name = "notifica_cer_origen", length = 20)
	protected String notificaCertificacioOrigen;
	
	@Column(name = "notifica_cer_metas", length = 255)
	protected String notificaCertificacioMetadades;
	
	@Column(name = "notifica_cer_csv", length = 50)
	protected String notificaCertificacioCsv;
	
	@Column(name = "notifica_cer_mime", length = 20)
	protected String notificaCertificacioMime;
	
	@Column(name = "notifica_cer_tamany", length = 20)
	protected Integer notificaCertificacioTamany;
	
	@Column(name = "notifica_cer_tipus")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	
	@Column(name = "notifica_cer_arxtip")
	@Enumerated(EnumType.ORDINAL)
	protected NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	
	@Column(name = "notifica_cer_numseg", length = 50)
	protected String notificaCertificacioNumSeguiment;
	
	/* Notifica error */
	@Column(name = "notifica_error", nullable = false)
	protected boolean notificaError;

	@Setter
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
	@JoinColumn(name = "notifica_error_event_id")
	@ForeignKey(name = "not_noteve_noterr_notdest_fk")
	@OnDelete(action = OnDeleteAction.CASCADE)
	protected NotificacioEventEntity notificacioErrorEvent;
	
	@Column(name = "notifica_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaIntentData;
	
	@Column(name = "notifica_intent_num")
	protected int notificaIntentNum;

	@Column(name="registre_numero_formatat", length = 50)
	private String registreNumeroFormatat;

	@Column(name="registre_data")
	private Date registreData;
	
	@Column(name="estat_registre")
	private NotificacioRegistreEstatEnumDto registreEstat;
	
	@Column(name="registre_estat_final")
	private boolean registreEstatFinal;
	
	@Column(name = "sir_con_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sirConsultaData;
	
	@Column(name = "sir_con_intent")
	protected int sirConsultaIntent;
	
	@Column(name = "sir_rec_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sirRecepcioData;
	
	@Column(name = "sir_reg_desti_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sirRegDestiData;
	
	@Transient
	private String csvUuid;

	public void setRegistreEstat(NotificacioRegistreEstatEnumDto registreEstat) {
		this.registreEstat = registreEstat;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public void setCsvUuid(String csvUuid) {
		this.csvUuid = csvUuid;
	}
	public void setTitular(PersonaEntity titular) {
		this.titular = titular;
	}
	public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public void setNotificacio(NotificacioEntity notificacio) {
		this.notificacio = notificacio;
	}
	public void updateNotificaReferencia(
			String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}
	public void updateNotificaIdentificador(
			String notificaidentificador) {
		this.notificaIdentificador = notificaidentificador;
	}
	public void setDehObligat(Boolean dehObligat) {
		this.dehObligat = dehObligat;
	}
	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}

	public void updateRegistreEstat(
			NotificacioRegistreEstatEnumDto registreEstat,
			Date registreEstatData,
			Date sirConsultaData,
			Date sirRegDestiData,
			String registreNumeroFormatat) {
		this.updateRegistreEstat(registreEstat);
		this.registreData = registreEstatData;
		this.sirRecepcioData = sirConsultaData;
		this.sirRegDestiData = sirRegDestiData;
		this.registreNumeroFormatat = registreNumeroFormatat;
		
	}

	public void updateRegistreEstat(NotificacioRegistreEstatEnumDto registreEstat) {
		boolean estatFinal =
				NotificacioRegistreEstatEnumDto.REBUTJAT.equals(registreEstat) ||
				NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(registreEstat);
		this.registreEstat = registreEstat;
		this.registreEstatFinal = estatFinal;
	}

	public void updateNotificaEnviada(
			String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
		this.notificaEstatData = new Date();
		this.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_ENVIADA;
		this.notificaError = false;
		this.notificacioErrorEvent = null;
		this.notificaIntentData = new Date();
		this.notificaEstatDataActualitzacio = new Date();
	}
	
	public void updateNotificaInformacio(
			Date notificaDataCreacio,
			Date notificaDataDisposicio,
			Date notificaDataCaducitat,
			String notificaEmisorDir3,
			String notificaEmisorDescripcio,
			String notificaEmisorNif,
			String notificaArrelDir3,
			String notificaArrelDescripcio,
			String notificaArrelNif) {
		this.notificaDataCreacio = notificaDataCreacio;
		this.notificaDataDisposicio = notificaDataDisposicio;
		this.notificaDataCaducitat = notificaDataCaducitat;
		this.notificaEmisorDir3 = notificaEmisorDir3;
		this.notificaEmisorDescripcio = notificaEmisorDescripcio;
		this.notificaEmisorNif = notificaEmisorNif;
		this.notificaArrelDir3 = notificaArrelDir3;
		this.notificaArrelDescripcio = notificaArrelDescripcio;
		this.notificaArrelNif = notificaArrelNif;
	}
	
	public void updateNotificaDatat(
			NotificacioEnviamentEstatEnumDto notificaEstat,
			Date notificaEstatData,
			boolean notificaEstatFinal,
			String notificaEstatDescripcio,
			String notificaDatatOrigen,
			String notificaDatatReceptorNif,
			String notificaDatatReceptorNom,
			String notificaDatatNumSeguiment,
			String notificaDatatErrorDescripcio) {
		this.notificaEstat = notificaEstat;
		this.notificaEstatData = notificaEstatData;
		this.notificaEstatFinal = notificaEstatFinal;
		this.notificaEstatDescripcio = notificaEstatDescripcio;
		this.notificaDatatOrigen = notificaDatatOrigen;
		this.notificaDatatReceptorNif = notificaDatatReceptorNif;
		this.notificaDatatReceptorNom = notificaDatatReceptorNom;
		this.notificaDatatNumSeguiment = notificaDatatNumSeguiment;
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
		this.notificaEstatDataActualitzacio = new Date();
	}
	
	public void updateNotificaCertificacio(
			Date notificaCertificacioData,
			String notificaCertificacioArxiuId,
			String notificaCertificacioHash,
			String notificaCertificacioOrigen,
			String notificaCertificacioMetadades,
			String notificaCertificacioCsv,
			String notificaCertificacioMime,
			Integer notificaCertificacioTamany,
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus, // acuse o sobre
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioData = notificaCertificacioData;
		this.notificaCertificacioArxiuId = notificaCertificacioArxiuId;
		this.notificaCertificacioHash = notificaCertificacioHash;
		this.notificaCertificacioOrigen = notificaCertificacioOrigen;
		this.notificaCertificacioMetadades = notificaCertificacioMetadades;
		this.notificaCertificacioCsv = notificaCertificacioCsv;
		this.notificaCertificacioMime = notificaCertificacioMime;
		this.notificaCertificacioTamany = notificaCertificacioTamany;
		this.notificaCertificacioTipus = notificaCertificacioTipus;
		this.notificaCertificacioArxiuTipus = notificaCertificacioArxiuTipus;
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	
	public void updateNotificaError(
			boolean notificaError,
			NotificacioEventEntity notificaErrorEvent) {
		this.notificaError = notificaError;
		this.notificacioErrorEvent = notificaErrorEvent;
	}
	
//	public void updateNotificaFiOperacio() {
//		this.notificaIntentData = new Date();
//	}
//	public void updateNotificaFiOperacio(boolean isError, Integer reintentPeriode) {
//		if (isError) {
//			this.intentNum++;
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.add(Calendar.MILLISECOND, reintentPeriode*(2^intentNum));
//			this.notificaIntentData = cal.getTime();
//		} else {
//			this.intentNum = 0;
//			this.notificaIntentData = new Date();
//		}
//	}
	public void updateNotificaDataRefrescEstat() {
		this.notificaEstatDataActualitzacio = new Date();
	}
	
	public void updateNotificaNovaConsulta(Integer reintentPeriode) {
		this.notificaIntentNum++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentPeriode*(2^notificaIntentNum));
		this.notificaIntentData = cal.getTime();
	}
	
	public void refreshNotificaConsulta() {
		this.notificaIntentNum = 0;
		this.notificaIntentData = new Date();
	}
	
	public void updateSirNovaConsulta(Integer reintentPeriode) {
		this.sirConsultaIntent++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentPeriode*(2^sirConsultaIntent));
		this.sirConsultaData = cal.getTime();
	}
	
	public void refreshSirConsulta() {
		this.sirConsultaIntent = 0;
		this.sirConsultaData = new Date();
	}
	
	public void update(
			Enviament enviament, 
			boolean isAmbEntregaDeh,
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular,
			NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
		this.serveiTipus = tipusServei;
		this.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
		this.notificaIntentNum = 0;
		this.notificacio = notificacioGuardada;
		this.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
		this.domiciliNumeracioTipus = numeracioTipus;
		this.domiciliConcretTipus = tipusConcret;
		this.domiciliViaTipus = domiciliViaTipus;
		if (enviament.getEntregaPostal() != null) {
			if(! enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
				this.domiciliViaNom = enviament.getEntregaPostal().getViaNom();
				this.domiciliNumeracioNumero = enviament.getEntregaPostal().getNumeroCasa();
				this.domiciliNumeracioQualificador = enviament.getEntregaPostal().getNumeroQualificador();
				this.domiciliNumeracioPuntKm = enviament.getEntregaPostal().getPuntKm();
				this.domiciliApartatCorreus = enviament.getEntregaPostal().getApartatCorreus();
				this.domiciliPortal = enviament.getEntregaPostal().getPortal();
				this.domiciliEscala = enviament.getEntregaPostal().getEscala();
				this.domiciliPlanta = enviament.getEntregaPostal().getPlanta();
				this.domiciliPorta = enviament.getEntregaPostal().getPorta();
				this.domiciliBloc = enviament.getEntregaPostal().getBloc();
				this.domiciliComplement = enviament.getEntregaPostal().getComplement();
				this.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
				this.domiciliPoblacio = enviament.getEntregaPostal().getPoblacio();
				this.domiciliMunicipiCodiIne = enviament.getEntregaPostal().getMunicipiCodi();
				this.domiciliProvinciaCodi = enviament.getEntregaPostal().getProvincia();
				this.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
				this.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
				this.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
				this.domiciliCie = enviament.getEntregaPostal().getCie();
				this.formatSobre = enviament.getEntregaPostal().getFormatSobre();
				this.formatFulla = enviament.getEntregaPostal().getFormatFulla();
			} else if (enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
				this.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
				this.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
				this.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
				this.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
			}
		}
		if (isAmbEntregaDeh && enviament.isEntregaDehActiva() && enviament.getEntregaDeh() != null) {
			this.dehNif = enviament.getTitular().getNif();
			this.dehObligat = enviament.getEntregaDeh().isObligat();
			this.dehProcedimentCodi = notificacioGuardada.getProcedimentCodiNotib();
		}
		
		this.titular = titular;
		
		// Inicialitzam les dates per consulta d'estats
		Date data = new Date();
		this.notificaIntentData = data;
		this.sirConsultaData = data;
	}


	public static BuilderV2 getBuilderV2(
			Enviament enviament, 
			boolean isAmbEntregaDeh,
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
			NotificaDomiciliConcretTipusEnumDto tipusConcret,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular,
			List<PersonaEntity> destinataris) {
		return new BuilderV2(
				enviament,
				isAmbEntregaDeh,
				numeracioTipus,
				tipusConcret,
				tipusServei,
				notificacioGuardada,
				titular,
				destinataris);
	}

	public static class BuilderV2 {
		NotificacioEnviamentEntity built;
		BuilderV2(
				Enviament enviament, 
				boolean isAmbEntregaDeh,
				NotificaDomiciliNumeracioTipusEnumDto numeracioTipus,
				NotificaDomiciliConcretTipusEnumDto tipusConcret,
				ServeiTipusEnumDto tipusServei,
				NotificacioEntity notificacioGuardada,
				PersonaEntity titular,
				List<PersonaEntity> destinataris) {	
			built = new NotificacioEnviamentEntity();
			built.serveiTipus = tipusServei;
			built.notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT;
			built.notificaIntentNum = 0;
			built.notificacio = notificacioGuardada;
			built.domiciliTipus = NotificaDomiciliTipusEnumDto.CONCRETO;
			built.domiciliNumeracioTipus = numeracioTipus;
			built.domiciliConcretTipus = tipusConcret;
			if (enviament.getEntregaPostal() != null) {
				if(! enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					built.domiciliViaNom = enviament.getEntregaPostal().getViaNom();
					built.domiciliNumeracioNumero = enviament.getEntregaPostal().getNumeroCasa();
					built.domiciliNumeracioQualificador = enviament.getEntregaPostal().getNumeroQualificador();
					built.domiciliNumeracioPuntKm = enviament.getEntregaPostal().getPuntKm();
					built.domiciliApartatCorreus = enviament.getEntregaPostal().getApartatCorreus();
					built.domiciliPortal = enviament.getEntregaPostal().getPortal();
					built.domiciliEscala = enviament.getEntregaPostal().getEscala();
					built.domiciliPlanta = enviament.getEntregaPostal().getPlanta();
					built.domiciliPorta = enviament.getEntregaPostal().getPorta();
					built.domiciliBloc = enviament.getEntregaPostal().getBloc();
					built.domiciliComplement = enviament.getEntregaPostal().getComplement();
					built.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
					built.domiciliPoblacio = enviament.getEntregaPostal().getPoblacio();
					built.domiciliMunicipiCodiIne = enviament.getEntregaPostal().getMunicipiCodi();
					built.domiciliProvinciaCodi = enviament.getEntregaPostal().getProvincia();
					built.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
					built.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
					built.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
					built.domiciliCie = enviament.getEntregaPostal().getCie();
					built.formatSobre = enviament.getEntregaPostal().getFormatSobre();
					built.formatFulla = enviament.getEntregaPostal().getFormatFulla();
				} else if (enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					built.domiciliLinea1 = enviament.getEntregaPostal().getLinea1();
					built.domiciliLinea2 = enviament.getEntregaPostal().getLinea2();
					built.domiciliCodiPostal = enviament.getEntregaPostal().getCodiPostal();
					built.domiciliPaisCodiIso = enviament.getEntregaPostal().getPaisCodi();
				}
			}
			if (isAmbEntregaDeh && enviament.isEntregaDehActiva() && enviament.getEntregaDeh() != null) {
				built.dehNif = enviament.getTitular().getNif();
				built.dehObligat = enviament.getEntregaDeh().isObligat();
				built.dehProcedimentCodi = notificacioGuardada.getProcedimentCodiNotib();
			}
			
			built.titular = titular;
			built.destinataris = destinataris;
			
			// Inicialitzam les dates per consulta d'estats
			Date data = new Date();
			built.notificaIntentData = data;
			built.sirConsultaData = data;
		}
		
		public BuilderV2 domiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
			built.domiciliViaTipus = domiciliViaTipus;
			return this;
		}
		
		public BuilderV2 destinataris(List<PersonaEntity> destinataris) {
			built.destinataris = destinataris;
			return this;
		}
		
		public NotificacioEnviamentEntity build() {
			return built;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((notificacio == null) ? 0 : notificacio.hashCode());
		result = prime * result + ((titular == null || titular.getNif() == null) ? 0 : titular.getNif().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificacioEnviamentEntity other = (NotificacioEnviamentEntity) obj;
		if (notificacio == null) {
			if (other.notificacio != null)
				return false;
		} else if (!notificacio.equals(other.notificacio))
			return false;
		if (titular.getNif() == null) {
			if (other.titular.getNif() != null)
				return false;
		} else if (!titular.getNif().equals(other.titular.getNif()))
			return false;
		return true;
	}

	public boolean isPendentRefrescarEstatNotifica(){
		if (!notificaEstatFinal)
			return !Arrays.asList(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT,
					NotificacioEnviamentEstatEnumDto.REGISTRADA,
					NotificacioEnviamentEstatEnumDto.FINALITZADA,
					NotificacioEnviamentEstatEnumDto.PROCESSADA).contains(notificaEstat);
		else {
			return notificaEstat.equals(NotificacioEnviamentEstatEnumDto.EXPIRADA) && notificaCertificacioData == null;
		}
	}
	public boolean isPendentRefrescarEstatRegistre(){
		return !notificaEstatFinal && notificaEstat.equals(NotificacioEnviamentEstatEnumDto.ENVIAT_SIR)
					&& !Arrays.asList(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT,
							NotificacioRegistreEstatEnumDto.REBUTJAT).contains(registreEstat);
	}

	@PreRemove
	private void preRemove() {
		this.notificacioErrorEvent = null;
	}

	private static final long serialVersionUID = 6993171107561077019L;
}
