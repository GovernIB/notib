/**
 * 
 */
package es.caib.notib.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
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

	@Column(name = "env_tipus", nullable = false)
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	private Date enviamentDataProgramada;
	@Column(name = "concepte", length = 50, nullable = false)
	private String concepte;
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
	@Column(name = "doc_arxiu_nom", length = 256, nullable = false)
	private String documentArxiuNom;
	@Column(name = "doc_arxiu_id", length = 64, nullable = false)
	private String documentArxiuId;
	@Column(name = "doc_sha1", length = 20, nullable = false)
	private String documentSha1;
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
	@Column(name = "seu_reg_oficina", length = 256, nullable = false)
	private String seuRegistreOficina;
	@Column(name = "seu_reg_llibre", length = 256, nullable = false)
	private String seuRegistreLlibre;
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
	@Column(name = "error", nullable = false)
	private boolean error;
	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "error_event_id")
	@ForeignKey(name = "not_noteve_notificacio_fk")
	private NotificacioEventEntity errorEvent;
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<NotificacioDestinatariEntity> destinataris = new ArrayList<NotificacioDestinatariEntity>();
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

	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public String getConcepte() {
		return concepte;
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
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public String getDocumentArxiuId() {
		return documentArxiuId;
	}
	public String getDocumentSha1() {
		return documentSha1;
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
	public boolean isError() {
		return error;
	}
	public NotificacioEventEntity getErrorEvent() {
		return errorEvent;
	}
	public List<NotificacioDestinatariEntity> getDestinataris() {
		return destinataris;
	}
	public List<NotificacioEventEntity> getEvents() {
		return events;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	
	public void updateDestinataris(
			List<NotificacioDestinatariEntity> destinataris) {
		this.destinataris = destinataris;
	}

	public void updateEstat(
			NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public void updateError(
			boolean error,
			NotificacioEventEntity errorEvent) {
		this.error = error;
		this.errorEvent = errorEvent;
	}
	public void updateEventAfegir(
			NotificacioEventEntity event) {
		events.add(event);
	}

	public static Builder getBuilder(
			NotificaEnviamentTipusEnumDto enviamentTipus,
			Date enviamentDataProgramada,
			String concepte,
			String documentArxiuNom,
			String documentArxiuId,
			String documentSha1,
			String seuAvisText,
			String seuAvisTitol,
			String seuOficiTitol,
			String seuOficiText,
			String seuIdioma,
			String seuRegistreLlibre,
			String seuRegistreOficina,
			String seuExpedientTitol,
			String seuExpedientIdentificadorEni,
			String seuExpedientUnitatOrganitzativa,
			String seuExpedientSerieDocumental,
			boolean documentNormalitzat,
			boolean documentGenerarCsv,
			List<NotificacioDestinatariEntity> destinataris,
			EntitatEntity entitat) {
		return new Builder(
				enviamentTipus,
				enviamentDataProgramada,
				concepte,
				documentArxiuNom,
				documentArxiuId,
				documentSha1,
				seuAvisText,
				seuAvisTitol,
				seuOficiTitol,
				seuOficiText,
				seuIdioma,
				seuRegistreLlibre,
				seuRegistreOficina,
				seuExpedientTitol,
				seuExpedientIdentificadorEni,
				seuExpedientUnitatOrganitzativa,
				seuExpedientSerieDocumental,
				documentNormalitzat,
				documentGenerarCsv,
				destinataris,
				entitat);
	}

	public static class Builder {
		NotificacioEntity built;
		Builder(
				NotificaEnviamentTipusEnumDto enviamentTipus,
				Date enviamentDataProgramada,
				String concepte,
				String documentArxiuNom,
				String documentArxiuId,
				String documentSha1,
				String seuAvisText,
				String seuAvisTitol,
				String seuOficiTitol,
				String seuOficiText,
				String seuIdioma,
				String seuRegistreLlibre,
				String seuRegistreOficina,
				String seuExpedientTitol,
				String seuExpedientIdentificadorEni,
				String seuExpedientUnitatOrganitzativa,
				String seuExpedientSerieDocumental,
				boolean documentNormalitzat,
				boolean documentGenerarCsv,
				List<NotificacioDestinatariEntity> destinataris,
				EntitatEntity entitat) {
			built = new NotificacioEntity();
			built.enviamentTipus = enviamentTipus;
			built.enviamentDataProgramada = enviamentDataProgramada;
			built.concepte = concepte;
			built.documentArxiuNom = documentArxiuNom;
			built.documentArxiuId = documentArxiuId;
			built.documentSha1 = documentSha1;
			built.seuAvisText = seuAvisText;
			built.seuAvisTitol = seuAvisTitol;
			built.seuOficiTitol = seuOficiTitol;
			built.seuOficiText = seuOficiText;
			built.seuIdioma = seuIdioma;
			built.seuRegistreLlibre = seuRegistreLlibre;
			built.seuRegistreOficina = seuRegistreOficina;
			built.seuExpedientTitol = seuExpedientTitol;
			built.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
			built.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
			built.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
			built.documentNormalitzat = documentNormalitzat;
			built.documentGenerarCsv = documentGenerarCsv;
			built.destinataris = destinataris;
			built.entitat = entitat;
			built.estat = NotificacioEstatEnumDto.PENDENT;
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
		public Builder seuAvisTextMobil(String seuAvisTextMobil) {
			built.seuAvisTextMobil = seuAvisTextMobil;
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
		result = prime * result + ((destinataris == null) ? 0 : destinataris.hashCode());
		result = prime * result + ((documentArxiuId == null) ? 0 : documentArxiuId.hashCode());
		result = prime * result + (documentGenerarCsv ? 1231 : 1237);
		result = prime * result + (documentNormalitzat ? 1231 : 1237);
		result = prime * result + ((documentSha1 == null) ? 0 : documentSha1.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((enviamentTipus == null) ? 0 : enviamentTipus.hashCode());
		result = prime * result + ((events == null) ? 0 : events.hashCode());
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
		if (destinataris == null) {
			if (other.destinataris != null)
				return false;
		} else if (!destinataris.equals(other.destinataris))
			return false;
		if (documentArxiuId == null) {
			if (other.documentArxiuId != null)
				return false;
		} else if (!documentArxiuId.equals(other.documentArxiuId))
			return false;
		if (documentGenerarCsv != other.documentGenerarCsv)
			return false;
		if (documentNormalitzat != other.documentNormalitzat)
			return false;
		if (documentSha1 == null) {
			if (other.documentSha1 != null)
				return false;
		} else if (!documentSha1.equals(other.documentSha1))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (enviamentTipus != other.enviamentTipus)
			return false;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
