package es.caib.notib.persist.entity;

import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.persist.audit.NotibAuditable;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
@Setter
@Table(name="not_notificacio_env")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEnviamentEntity extends NotibAuditable<Long> {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_id")
	@ForeignKey(name = "NOT_NOTIFICACIO_NOTENV_FK")
	@NotFound(action = NotFoundAction.IGNORE)
	protected NotificacioEntity notificacio;

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

	/* Notifica informació */
	@Column(name = "notifica_ref", length = 36)
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
	protected EnviamentEstat notificaEstat;
	
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
	@ForeignKey(name = "NOT_NOTENV_NOTEVENT_ERROR_FK")
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
	
	@Column(name = "deh_cert_intent_num")
	protected int dehCertIntentNum;
	
	@Column(name = "deh_cert_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date dehCertIntentData;
	
	@Column(name = "cie_cert_intent_num")
	protected int cieCertIntentNum;
	
	@Column(name = "cie_cert_intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cieCertIntentData;

	@OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "ENTREGA_POSTAL_ID")
	@ForeignKey(name = "NOT_NOTIFICACIO_ENV_DOM_FK")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private EntregaPostalEntity entregaPostal;

	@Column(name = "per_email")
	private boolean perEmail;

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
	public void setNotificaEstat(EnviamentEstat notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public void setNotificacio(NotificacioEntity notificacio) {
		this.notificacio = notificacio;
	}
	public void updateNotificaReferencia(String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}
	public void updateNotificaIdentificador(String notificaidentificador) {
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
		boolean estatFinal = NotificacioRegistreEstatEnumDto.REBUTJAT.equals(registreEstat) || NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(registreEstat);
		this.registreEstat = registreEstat;
		this.registreEstatFinal = estatFinal;
	}

	public void updateNotificaEnviada(String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
		this.notificaEstatData = new Date();
		this.notificaEstat = EnviamentEstat.NOTIB_ENVIADA;
		this.notificaError = false;
		this.notificacioErrorEvent = null;
		this.notificaIntentData = new Date();
		this.notificaEstatDataActualitzacio = new Date();
	}

	public void updateNotificaEnviadaEmail() {
		this.notificaEstatData = new Date();
		this.notificaEstat = EnviamentEstat.FINALITZADA;
		this.notificaEstatFinal = true;
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
			EnviamentEstat notificaEstat,
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
		if (!StringUtils.isBlank(notificaDatatOrigen))
			this.notificaDatatOrigen = notificaDatatOrigen;
		if (!StringUtils.isBlank(notificaDatatReceptorNif))
			this.notificaDatatReceptorNif = notificaDatatReceptorNif;
		if (!StringUtils.isBlank(notificaDatatReceptorNom))
			this.notificaDatatReceptorNom = notificaDatatReceptorNom;
		if (!StringUtils.isBlank(notificaDatatNumSeguiment))
			this.notificaDatatNumSeguiment = notificaDatatNumSeguiment;
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
		this.notificaEstatDataActualitzacio = new Date();
	}

	public void updateReceptorDatat(
			String notificaDatatReceptorNif,
			String notificaDatatReceptorNom) {
		if (!StringUtils.isBlank(notificaDatatReceptorNif))
			this.notificaDatatReceptorNif = notificaDatatReceptorNif;
		if (!StringUtils.isBlank(notificaDatatReceptorNom))
			this.notificaDatatReceptorNom = notificaDatatReceptorNom;
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
	
	public void updateNotificaError(boolean notificaError, NotificacioEventEntity notificaErrorEvent) {

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
	
	public void updateDEHCertNovaConsulta(Integer reintentPeriode) {
		this.dehCertIntentNum++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentPeriode*(2^dehCertIntentNum));
		this.dehCertIntentData = cal.getTime();
	}
	
	public void updateCIECertNovaConsulta(Integer reintentPeriode) {
		this.cieCertIntentNum++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentPeriode*(2^cieCertIntentNum));
		this.cieCertIntentData = cal.getTime();
	}
	
	public void update(
			Enviament enviament,
			boolean isAmbEntregaDeh,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular) {
		this.serveiTipus = tipusServei;
		this.notificaEstat = EnviamentEstat.NOTIB_PENDENT;
		this.notificaIntentNum = 0;
		this.notificacio = notificacioGuardada;
		if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
			if (entregaPostal == null) {
				entregaPostal = new EntregaPostalEntity();
			}
			entregaPostal.update(enviament.getEntregaPostal());
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

		this.perEmail = InteressatTipus.FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) && // Interessar sense NIF
				(this.destinataris == null || this.destinataris.isEmpty()) &&							// No té destinataris (els destinataris tenen NIF obligatòriament)
				this.entregaPostal == null;																// No s'envia per entrega postal
	}


	public static BuilderV2 getBuilderV2(
			Enviament enviament, 
			boolean isAmbEntregaDeh,
			ServeiTipusEnumDto tipusServei,
			NotificacioEntity notificacioGuardada,
			PersonaEntity titular,
			List<PersonaEntity> destinataris,
			String referencia) {
		return new BuilderV2(
				enviament,
				isAmbEntregaDeh,
				tipusServei,
				notificacioGuardada,
				titular,
				destinataris,
				referencia);
	}

	public static class BuilderV2 {
		NotificacioEnviamentEntity built;
		BuilderV2(
				Enviament enviament, 
				boolean isAmbEntregaDeh,
				ServeiTipusEnumDto tipusServei,
				NotificacioEntity notificacioGuardada,
				PersonaEntity titular,
				List<PersonaEntity> destinataris,
				String referencia) {
			built = new NotificacioEnviamentEntity();
			built.serveiTipus = tipusServei;
			built.notificaEstat = EnviamentEstat.NOTIB_PENDENT;
			built.notificaIntentNum = 0;
			built.notificacio = notificacioGuardada;
			if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
				EntregaPostalEntity entregaPostal = new EntregaPostalEntity();
				entregaPostal.update(enviament.getEntregaPostal());
				built.entregaPostal = entregaPostal;
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
			built.notificaReferencia = referencia;

			built.perEmail = InteressatTipus.FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) && 	// Interessar sense NIF
					(built.destinataris == null || built.destinataris.isEmpty()) &&								// No té destinataris (els destinataris tenen NIF obligatòriament)
					built.entregaPostal == null;																// No s'envia per entrega postal
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
			return !Arrays.asList(EnviamentEstat.NOTIB_PENDENT,
					EnviamentEstat.REGISTRADA,
					EnviamentEstat.FINALITZADA,
					EnviamentEstat.PROCESSADA).contains(notificaEstat);
		else {
			return notificaEstat.equals(EnviamentEstat.EXPIRADA) && notificaCertificacioData == null;
		}
	}
	public boolean isPendentRefrescarEstatRegistre(){
		return !notificaEstatFinal && notificaEstat.equals(EnviamentEstat.ENVIAT_SIR)
					&& !Arrays.asList(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT,
							NotificacioRegistreEstatEnumDto.REBUTJAT).contains(registreEstat);
	}

	@PreRemove
	private void preRemove() {
		this.notificacioErrorEvent = null;
	}

	private static final long serialVersionUID = 6993171107561077019L;
}
