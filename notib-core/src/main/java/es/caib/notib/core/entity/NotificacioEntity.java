/**
 * 
 */
package es.caib.notib.core.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.audit.NotibAuditable;


/**
 * Classe del model de dades que representa una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_notificacio")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEntity extends NotibAuditable<Long> {

	@Column(name = "emisor_dir3codi", length = 9, nullable = false)
	private String emisorDir3Codi;
	
	@Column(name = "com_tipus", nullable = false)
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	
	@Column(name = "env_tipus", nullable = false)
	private NotificaEnviamentTipusEnumDto enviamentTipus;
//	@Column(name = "env_data_prog")
//	@Temporal(TemporalType.DATE)
//	private Date enviamentDataProgramada;
	@Column(name = "concepte", length = 50, nullable = false)
	private String concepte;
	
	@Column(name = "descripcio", length = 100)
	private String descripcio;
	@Column(name = "pagcor_dir3", length = 9)
	private String pagadorCorreusCodiDir3;
	@Column(name = "pagcor_numcont", length = 20)
	private String pagadorCorreusContracteNum;
	@Column(name = "pagcor_codi_client", length = 20)
	private String pagadorCorreusCodiClientFacturacio;
	@Column(name = "pagcor_data_vig")
	@Temporal(TemporalType.DATE)
	private Date pagadorCorreusDataVigencia;
	@Column(name = "pagcie_dir3", length = 9)
	private String pagadorCieCodiDir3;
	@Column(name = "pagcie_data_vig")
	@Temporal(TemporalType.DATE)
	private Date pagadorCieDataVigencia;
	@Column(name = "proc_desc_sia", length = 256)
	private String procedimentDescripcioSia;
//	@Column(name = "retard_postal")
//	private Integer retardPostal;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	private Date caducitat;
	
	@Column(name = "proc_codi_notib", length = 6, nullable = false)
	private String procedimentCodiNotib;
	
	@Column(name = "grup_codi", length = 6, nullable = false)
	private String grupCodi;
	
	@Column(name = "doc_arxiu_nom", length = 256, nullable = false)
	private String documentArxiuNom;
	
	@Column(name = "csv_uuid", length = 64)
	private String csv_uuid;
	
	@Column(name = "doc_arxiu_id", length = 64)
	private String documentArxiuId;
	
	@Column(name = "doc_hash", length = 40, nullable = false)
	private String documentHash;
	
	@Column(name = "doc_normalitzat", nullable = false)
	private boolean documentNormalitzat;
	
	@Column(name = "doc_gen_csv", nullable = false)
	private boolean documentGenerarCsv;
	
	@Column(name = "registre_oficina", length = 52, nullable = false)
	private String registreOficina;
	@Column(name = "registre_llibre", length = 256, nullable = false)
	private String registreLlibre;
	@Column(name = "registre_extracte", length = 52, nullable = false)
	private String registreExtracte;
	@Column(name = "registre_doc_fisica", length = 256, nullable = false)
	private String registreDocFisica;
	@Column(name = "registre_idioma", length = 52, nullable = false)
	private String registreIdioma;
	@Column(name = "registre_tipus_assumpte", length = 256, nullable = false)
	private String registreTipusAssumpte;
	@Column(name = "registre_num_expedient", length = 256, nullable = false)
	private String registreNumExpedient;
	@Column(name = "registre_ref_externa", length = 52, nullable = false)
	private String registreRefExterna;
	@Column(name = "registre_codi_assumpte", length = 256, nullable = false)
	private String registreCodiAssumpte;
	@Column(name = "registre_observacions", length = 256, nullable = false)
	private String registreObservacions;
	
	@Column(name = "estat", nullable = false)
	private NotificacioEstatEnumDto estat;
	
	@Column(name = "not_error_tipus")
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	
	@Column(name = "not_env_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEnviamentData;
	
	@Column(name = "not_env_intent")
	private int notificaEnviamentIntent;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "not_error_event_id")
	@ForeignKey(name = "not_noterrevent_notificacio_fk")
	private NotificacioEventEntity notificaErrorEvent;
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy(value="id")
	private List<NotificacioEnviamentEntity> enviaments = new ArrayList<NotificacioEnviamentEntity>();
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<NotificacioEventEntity> events = new ArrayList<NotificacioEventEntity>();
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_entitat_notificacio_fk")
	private EntitatEntity entitat;

	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public NotificacioComunicacioTipusEnumDto getComunicacioTipus() {
		return comunicacioTipus;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
//	public Date getEnviamentDataProgramada() {
//		return enviamentDataProgramada;
//	}
	public String getConcepte() {
		return concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getProcedimentCodiNotib() {
		return procedimentCodiNotib;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public String getProcedimentDescripcioSia() {
		return procedimentDescripcioSia;
	}
//	public Integer getRetardPostal() {
//		return retardPostal;
//	}
	public Date getCaducitat() {
		return caducitat;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public String getDocumentArxiuId() {
		return documentArxiuId;
	}
	public String getCsv_uuid() {
		return csv_uuid;
	}
	public String getDocumentHash() {
		return documentHash;
	}
	public boolean isDocumentNormalitzat() {
		return documentNormalitzat;
	}
	public boolean isDocumentGenerarCsv() {
		return documentGenerarCsv;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public NotificacioErrorTipusEnumDto getNotificaErrorTipus() {
		return notificaErrorTipus;
	}
	public Date getNotificaEnviamentData() {
		return notificaEnviamentData;
	}
	public int getNotificaEnviamentIntent() {
		return notificaEnviamentIntent;
	}
	public NotificacioEventEntity getNotificaErrorEvent() {
		return notificaErrorEvent;
	}
	public List<NotificacioEnviamentEntity> getEnviaments() {
		return enviaments;
	}
	public List<NotificacioEventEntity> getEvents() {
		return events;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public String getRegistreOficina() {
		return registreOficina;
	}
	public String getRegistreLlibre() {
		return registreLlibre;
	}
	public String getRegistreExtracte() {
		return registreExtracte;
	}
	public String getRegistreDocFisica() {
		return registreDocFisica;
	}
	public String getRegistreIdioma() {
		return registreIdioma;
	}
	public String getRegistreTipusAssumpte() {
		return registreTipusAssumpte;
	}
	public String getRegistreNumExpedient() {
		return registreNumExpedient;
	}
	public String getRegistreRefExterna() {
		return registreRefExterna;
	}
	public String getRegistreCodiAssumpte() {
		return registreCodiAssumpte;
	}
	public String getRegistreObservacions() {
		return registreObservacions;
	}
	public void addEnviament(
			NotificacioEnviamentEntity enviament) {
		this.enviaments.add(enviament);
	}

	public void updateEstat(
			NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public void updateNotificaNouEnviament(int reintentsPeriodeNotifica) {
		this.notificaEnviamentIntent++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentsPeriodeNotifica*(2^notificaEnviamentIntent));
		this.notificaEnviamentData = cal.getTime();
	}
	public void updateNotificaError(
			NotificacioErrorTipusEnumDto errorTipus,
			NotificacioEventEntity errorEvent) {
		this.notificaErrorTipus = errorTipus;
		this.notificaErrorEvent = errorEvent;
	}
	public void updateEventAfegir(
			NotificacioEventEntity event) {
		events.add(event);
	}
	public static Builder getBuilder(
			EntitatEntity entitat,
			String emisorDir3Codi,
			NotificacioComunicacioTipusEnumDto comunicacioTipus,
			NotificaEnviamentTipusEnumDto enviamentTipus,
			String concepte,
			String documentArxiuNom,
			String documentArxiuId,
			String csv_uuid,
			String documentHash,
			boolean documentNormalitzat,
			boolean documentGenerarCsv) {
		return new Builder(
				entitat,
				emisorDir3Codi,
				comunicacioTipus,
				enviamentTipus,
				concepte,
				documentArxiuNom,
				documentArxiuId,
				csv_uuid,
				documentHash,
				documentNormalitzat,
				documentGenerarCsv);
	}

	public static class Builder {
		NotificacioEntity built;
		Builder(
				EntitatEntity entitat,
				String emisorDir3Codi,
				NotificacioComunicacioTipusEnumDto comunicacioTipus,
				NotificaEnviamentTipusEnumDto enviamentTipus,
				String concepte,
				String documentArxiuNom,
				String documentArxiuId,
				String csv_uuid,
				String documentHash,
				boolean documentNormalitzat,
				boolean documentGenerarCsv) {
			built = new NotificacioEntity();
			built.entitat = entitat;
			built.emisorDir3Codi = emisorDir3Codi;
			built.comunicacioTipus = comunicacioTipus;
			built.enviamentTipus = enviamentTipus;
//			built.enviamentDataProgramada = enviamentDataProgramada;
			built.concepte = concepte;
			built.documentArxiuNom = documentArxiuNom;
			built.documentArxiuId = documentArxiuId;
			built.csv_uuid = csv_uuid;
			built.documentHash = documentHash;
			built.documentNormalitzat = documentNormalitzat;
			built.documentGenerarCsv = documentGenerarCsv;
			built.estat = NotificacioEstatEnumDto.PENDENT;
			built.notificaEnviamentIntent = 0;
			built.notificaEnviamentData = new Date();
		}
		public Builder registreOficina(String registreOficina) {
			built.registreOficina = registreOficina;
			return this;
		}
		public Builder registreLlibre(String registreLlibre) {
			built.registreLlibre = registreLlibre;
			return this;
		}
		public Builder registreExtracte(String registreExtracte) {
			built.registreExtracte = registreExtracte;
			return this;
		}
		public Builder registreDocFisica(String registreDocFisica) {
			built.registreDocFisica = registreDocFisica;
			return this;
		}
		public Builder registreIdioma(String registreIdioma) {
			built.registreIdioma = registreIdioma;
			return this;
		}
		public Builder registreTipusAssumpte(String registreTipusAssumpte) {
			built.registreTipusAssumpte = registreTipusAssumpte;
			return this;
		}
		public Builder registreNumExpedient(String registreNumExpedient) {
			built.registreNumExpedient = registreNumExpedient;
			return this;
		}
		public Builder registreRefExterna(String registreRefExterna) {
			built.registreRefExterna = registreRefExterna;
			return this;
		}
		public Builder registreCodiAssumpte(String registreCodiAssumpte) {
			built.registreCodiAssumpte = registreCodiAssumpte;
			return this;
		}
		public Builder registreObservacions(String registreObservacions) {
			built.registreObservacions = registreObservacions;
			return this;
		}
		
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder procedimentCodiNotib(String procedimentCodiNotib) {
			built.procedimentCodiNotib = procedimentCodiNotib;
			return this;
		}
		public Builder grupCodi(String grupCodi) {
			built.grupCodi = grupCodi;
			return this;
		}
		public Builder procedimentDescripcioSia(String procedimentDescripcioSia) {
			built.procedimentDescripcioSia = procedimentDescripcioSia;
			return this;
		}
//		public Builder retardPostal(Integer retardPostal) {
//			built.retardPostal = retardPostal;
//			return this;
//		}
		public Builder caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public NotificacioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((concepte == null) ? 0 : concepte.hashCode());
		result = prime * result + ((documentHash == null) ? 0 : documentHash.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((enviamentTipus == null) ? 0 : enviamentTipus.hashCode());
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
		NotificacioEntity other = (NotificacioEntity) obj;
		if (concepte == null) {
			if (other.concepte != null)
				return false;
		} else if (!concepte.equals(other.concepte))
			return false;
		if (documentHash == null) {
			if (other.documentHash != null)
				return false;
		} else if (!documentHash.equals(other.documentHash))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (enviamentTipus != other.enviamentTipus)
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
