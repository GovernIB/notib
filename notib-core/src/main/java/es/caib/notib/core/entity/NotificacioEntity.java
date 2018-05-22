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
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	private Date enviamentDataProgramada;
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
	@Column(name = "proc_codi_sia", length = 6, nullable = false)
	private String procedimentCodiSia;
	@Column(name = "proc_desc_sia", length = 256)
	private String procedimentDescripcioSia;
	@Column(name = "retard_postal")
	private Integer retardPostal;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	private Date caducitat;
	@Column(name = "doc_arxiu_nom", length = 256, nullable = false)
	private String documentArxiuNom;
	@Column(name = "doc_arxiu_id", length = 64, nullable = false)
	private String documentArxiuId;
	@Column(name = "doc_hash", length = 40, nullable = false)
	private String documentHash;
	@Column(name = "doc_normalitzat", nullable = false)
	private boolean documentNormalitzat;
	@Column(name = "doc_gen_csv", nullable = false)
	private boolean documentGenerarCsv;
	@Column(name = "seu_exp_serdoc", length = 10, nullable = false)
	private String seuExpedientSerieDocumental;
	@Column(name = "seu_exp_uniorg", length = 10, nullable = false)
	private String seuExpedientUnitatOrganitzativa;
	@Column(name = "seu_exp_ideni", length = 52, nullable = false)
	private String seuExpedientIdentificadorEni;
	@Column(name = "seu_exp_titol", length = 256, nullable = false)
	private String seuExpedientTitol;
	@Column(name = "seu_proc_codi", length = 256, nullable = false)
	private String seuProcedimentCodi;
	@Column(name = "seu_reg_oficina", length = 256, nullable = false)
	private String seuRegistreOficina;
	@Column(name = "seu_reg_llibre", length = 256, nullable = false)
	private String seuRegistreLlibre;
	@Column(name = "seu_reg_organ", length = 256, nullable = false)
	private String seuRegistreOrgan;
	@Column(name = "seu_idioma", length = 256, nullable = false)
	private String seuIdioma;
	@Column(name = "seu_avis_titol", length = 256, nullable = false)
	private String seuAvisTitol;
	@Column(name = "seu_avis_text", length = 256, nullable = false)
	private String seuAvisText;
	@Column(name = "seu_avis_mobil", length = 256)
	private String seuAvisTextMobil;
	@Column(name = "seu_ofici_titol", length = 256, nullable = false)
	private String seuOficiTitol;
	@Column(name = "seu_ofici_text", length = 256, nullable = false)
	private String seuOficiText;
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
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public String getConcepte() {
		return concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getPagadorCorreusCodiDir3() {
		return pagadorCorreusCodiDir3;
	}
	public String getPagadorCorreusContracteNum() {
		return pagadorCorreusContracteNum;
	}
	public String getPagadorCorreusCodiClientFacturacio() {
		return pagadorCorreusCodiClientFacturacio;
	}
	public Date getPagadorCorreusDataVigencia() {
		return pagadorCorreusDataVigencia;
	}
	public String getPagadorCieCodiDir3() {
		return pagadorCieCodiDir3;
	}
	public Date getPagadorCieDataVigencia() {
		return pagadorCieDataVigencia;
	}
	public String getProcedimentCodiSia() {
		return procedimentCodiSia;
	}
	public String getProcedimentDescripcioSia() {
		return procedimentDescripcioSia;
	}
	public Integer getRetardPostal() {
		return retardPostal;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public String getDocumentArxiuId() {
		return documentArxiuId;
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
	public String getSeuExpedientSerieDocumental() {
		return seuExpedientSerieDocumental;
	}
	public String getSeuExpedientUnitatOrganitzativa() {
		return seuExpedientUnitatOrganitzativa;
	}
	public String getSeuExpedientIdentificadorEni() {
		return seuExpedientIdentificadorEni;
	}
	public String getSeuExpedientTitol() {
		return seuExpedientTitol;
	}
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
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
	public String getSeuProcedimentCodi() {
		return seuProcedimentCodi;
	}
	public String getSeuRegistreOrgan() {
		return seuRegistreOrgan;
	}
	public List<NotificacioEventEntity> getEvents() {
		return events;
	}
	public EntitatEntity getEntitat() {
		return entitat;
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
			Date enviamentDataProgramada,
			String concepte,
			String documentArxiuNom,
			String documentArxiuId,
			String documentHash,
			boolean documentNormalitzat,
			boolean documentGenerarCsv) {
		return new Builder(
				entitat,
				emisorDir3Codi,
				comunicacioTipus,
				enviamentTipus,
				enviamentDataProgramada,
				concepte,
				documentArxiuNom,
				documentArxiuId,
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
				Date enviamentDataProgramada,
				String concepte,
				String documentArxiuNom,
				String documentArxiuId,
				String documentHash,
				boolean documentNormalitzat,
				boolean documentGenerarCsv) {
			built = new NotificacioEntity();
			built.entitat = entitat;
			built.emisorDir3Codi = emisorDir3Codi;
			built.comunicacioTipus = comunicacioTipus;
			built.enviamentTipus = enviamentTipus;
			built.enviamentDataProgramada = enviamentDataProgramada;
			built.concepte = concepte;
			built.documentArxiuNom = documentArxiuNom;
			built.documentArxiuId = documentArxiuId;
			built.documentHash = documentHash;
			built.documentNormalitzat = documentNormalitzat;
			built.documentGenerarCsv = documentGenerarCsv;
			built.estat = NotificacioEstatEnumDto.PENDENT;
			built.notificaEnviamentIntent = 0;
			built.notificaEnviamentData = new Date();
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder pagadorCorreusCodiDir3(String pagadorCorreusCodiDir3) {
			built.pagadorCorreusCodiDir3 = pagadorCorreusCodiDir3;
			return this;
		}
		public Builder pagadorCorreusContracteNum(String pagadorCorreusContracteNum) {
			built.pagadorCorreusContracteNum = pagadorCorreusContracteNum;
			return this;
		}
		public Builder pagadorCorreusCodiClientFacturacio(String pagadorCorreusCodiClientFacturacio) {
			built.pagadorCorreusCodiClientFacturacio = pagadorCorreusCodiClientFacturacio;
			return this;
		}
		public Builder pagadorCorreusDataVigencia(Date pagadorCorreusDataVigencia) {
			built.pagadorCorreusDataVigencia = pagadorCorreusDataVigencia;
			return this;
		}
		public Builder pagadorCieCodiDir3(String pagadorCieCodiDir3) {
			built.pagadorCieCodiDir3 = pagadorCieCodiDir3;
			return this;
		}
		public Builder pagadorCieDataVigencia(Date pagadorCieDataVigencia) {
			built.pagadorCieDataVigencia = pagadorCieDataVigencia;
			return this;
		}
		public Builder procedimentCodiSia(String procedimentCodiSia) {
			built.procedimentCodiSia = procedimentCodiSia;
			return this;
		}
		public Builder procedimentDescripcioSia(String procedimentDescripcioSia) {
			built.procedimentDescripcioSia = procedimentDescripcioSia;
			return this;
		}
		public Builder retardPostal(Integer retardPostal) {
			built.retardPostal = retardPostal;
			return this;
		}
		public Builder caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public Builder seuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
			built.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
			return this;
		}
		public Builder seuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
			built.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
			return this;
		}
		public Builder seuAvisTitol(String seuAvisTitol) {
			built.seuAvisTitol = seuAvisTitol;
			return this;
		}
		public Builder seuAvisText(String seuAvisText) {
			built.seuAvisText = seuAvisText;
			return this;
		}
		public Builder seuAvisTextMobil(String seuAvisTextMobil) {
			built.seuAvisTextMobil = seuAvisTextMobil;
			return this;
		}
		public Builder seuOficiTitol(String seuOficiTitol) {
			built.seuOficiTitol = seuOficiTitol;
			return this;
		}
		public Builder seuOficiText(String seuOficiText) {
			built.seuOficiText = seuOficiText;
			return this;
		}
		public Builder seuRegistreLlibre(String seuRegistreLlibre) {
			built.seuRegistreLlibre = seuRegistreLlibre;
			return this;
		}
		public Builder seuRegistreOficina(String seuRegistreOficina) {
			built.seuRegistreOficina = seuRegistreOficina;
			return this;
		}
		public Builder seuRegistreOrgan(String seuRegistreOrgan) {
			built.seuRegistreOrgan = seuRegistreOrgan;
			return this;
		}
		public Builder seuIdioma(String seuIdioma) {
			built.seuIdioma = seuIdioma;
			return this;
		}
		public Builder seuExpedientTitol(String seuExpedientTitol) {
			built.seuExpedientTitol = seuExpedientTitol;
			return this;
		}
		public Builder seuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
			built.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
			return this;
		}
		public Builder seuProcedimentCodi(String seuProcedimentCodi) {
			built.seuProcedimentCodi = seuProcedimentCodi;
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
