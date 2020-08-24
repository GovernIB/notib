/**
 * 
 */
package es.caib.notib.core.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;


/**
 * Classe del model de dades que representa una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name="not_notificacio")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEntity extends NotibAuditable<Long> {

	@Column(name = "usuari_codi", length = 64, nullable = false)
	protected String usuariCodi;
	
	@Column(name = "emisor_dir3codi", length = 9, nullable = false)
	protected String emisorDir3Codi;
	
	@Column(name = "com_tipus", nullable = false)
	protected NotificacioComunicacioTipusEnumDto comunicacioTipus;
	
	@Column(name = "env_tipus", nullable = false)
	protected NotificaEnviamentTipusEnumDto enviamentTipus;
	
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	protected Date enviamentDataProgramada;
	
	@Column(name = "concepte", length = 255, nullable = false)
	protected String concepte;
	
	@Column(name = "descripcio", length = 1000)
	protected String descripcio;
	
	@Column(name = "retard_postal")
	protected Integer retard;
	
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	protected Date caducitat;
	
	@Column(name = "proc_codi_notib", length = 9, nullable = false)
	protected String procedimentCodiNotib;
	
	@Column(name = "grup_codi", length = 64, nullable = false)
	protected String grupCodi;
	
	@Column(name = "estat", nullable = false)
	protected NotificacioEstatEnumDto estat;
	
	@Column(name = "estat_date")
	protected Date estatDate;
	
	@Column(name = "tipus_usuari")
	protected TipusUsuariEnumDto tipusUsuari;
	
	@Column(name = "motiu")
	protected String motiu;
	
	@Column(name = "not_error_tipus")
	protected NotificacioErrorTipusEnumDto notificaErrorTipus;
	
	@Column(name = "not_env_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEnviamentData;
	
	@Column(name = "not_env_intent")
	protected int notificaEnviamentIntent;
	
	@Column(name = "registre_env_intent")
	protected int registreEnviamentIntent;
	
	@Column(name = "registre_numero", length = 19)
	protected Integer registreNumero;
	
	@Column(name = "registre_numero_formatat", length = 200)
	protected String registreNumeroFormatat;
	
	@Column(name = "registre_data")
	@Temporal(TemporalType.DATE)
	protected Date registreData;
	
	@Column(name = "registre_num_expedient", length = 80, nullable = false)
	protected String numExpedient;
	
	@Column(name = "callback_error")
	protected boolean errorLastCallback;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "not_error_event_id")
	@ForeignKey(name = "not_noterrevent_notificacio_fk")
	protected NotificacioEventEntity notificaErrorEvent;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_entitat_notificacio_fk")
	protected EntitatEntity entitat;
	
	
	/*pagador a Postal*/
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "pagador_postal_id")
	@ForeignKey(name = "not_pagador_postal_not_fk")
	protected PagadorPostalEntity pagadorPostal;
	
	
	/*pagador CIE*/
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "pagador_cie_id")
	@ForeignKey(name = "not_pagador_cie_not_fk")
	protected PagadorCieEntity pagadorCie;
	
	/*Procediment*/
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_procediment_not_fk")
	protected ProcedimentEntity procediment;
	
	/*document*/
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	protected DocumentEntity document;
	
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.EAGER,
			cascade=CascadeType.ALL)
	protected Set<NotificacioEnviamentEntity> enviaments = new LinkedHashSet<NotificacioEnviamentEntity>();
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY)
	protected Set<NotificacioEventEntity> events = new LinkedHashSet<NotificacioEventEntity>();
	

	@Transient
	protected boolean permisProcessar;
	@Transient
	protected boolean errorLastEvent;
	
	
	public void addEnviament(
			NotificacioEnviamentEntity enviament) {
		this.enviaments.add(enviament);
	}
	
	public void setPermisProcessar(boolean permisProcessar) {
		this.permisProcessar = permisProcessar;
	}
	
	public void setErrorLastEvent(boolean errorLastEvent) {
		this.errorLastEvent = errorLastEvent;
	}
	
	public void updateRegistreNumero(Integer registreNumero) {
		this.registreNumero = registreNumero;
	}
	
	public void updateRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}
	
	public void updateRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	
	public void updateEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	
	public void updateEstatDate(
			Date estatDate) {
		this.estatDate = estatDate;
	}
	
	public void updateMotiu(String motiu) {
		this.motiu = motiu;
	}
	
	public void updateLastCallbackError(boolean error) {
		this.errorLastCallback = error;
	}
	
	public TipusUsuariEnumDto getTipusUsuari() {
		return tipusUsuari;
	}
	
	public void updateCodiSia(String codiSia) {
		this.procedimentCodiNotib=codiSia;
	}
	
	public void resetIntentsNotificacio() {
		this.notificaEnviamentIntent = 0;
	}
	
	public void updateNotificaNouEnviament(int reintentsPeriodeNotifica) {
		this.notificaEnviamentIntent++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentsPeriodeNotifica*(2^notificaEnviamentIntent));
		this.notificaEnviamentData = cal.getTime();
	}
	
	public void updateRegistreNouEnviament(int reintentsPeriodeRegistre) {
		this.registreEnviamentIntent++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentsPeriodeRegistre*(2^registreEnviamentIntent));
		this.registreData = cal.getTime();
	}
	
	public void updateNotificaError(
			NotificacioErrorTipusEnumDto errorTipus,
			NotificacioEventEntity errorEvent) {
		this.notificaErrorTipus = errorTipus;
		this.notificaErrorEvent = errorEvent;
	}
	public void cleanNotificaError() {
		this.notificaErrorTipus = null;
		this.notificaErrorEvent = null;
	}
	public void updateEventAfegir(
			NotificacioEventEntity event) {
		events.add(event);
	}
	
	public static Builder getBuilder(
			EntitatEntity entitat,
			String emisorDir3Codi,
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
				enviamentTipus,
				concepte,
				documentArxiuNom,
				documentArxiuId,
				csv_uuid,
				documentHash,
				documentNormalitzat,
				documentGenerarCsv);
	}
	
	public static BuilderV1 getBuilderV1(
			EntitatEntity entitat,
			String emisorDir3Codi,
			NotificacioComunicacioTipusEnumDto comunicacioTipus,
			NotificaEnviamentTipusEnumDto enviamentTipus,
			String concepte,
			String descripcio,
			Date enviamentDataProgramada,
			Integer retard,
			Date caducitat,
			DocumentEntity document,
			PagadorPostalEntity pagadorPostal,
			PagadorCieEntity pagadorCie,
			List<Enviament>enviaments) {
		return new BuilderV1(
				entitat,
				emisorDir3Codi,
				comunicacioTipus,
				enviamentTipus,
				concepte,
				descripcio,
				enviamentDataProgramada,
				retard,
				caducitat,
				document,
				pagadorPostal,
				pagadorCie,
				enviaments);
	}
	
	
	public static BuilderV2 getBuilderV2(
			EntitatEntity entitat,
			String emisorDir3Codi,
			NotificacioComunicacioTipusEnumDto comunicacioTipus,
			NotificaEnviamentTipusEnumDto enviamentTipus,
			String concepte,
			String descripcio,
			Date enviamentDataProgramada,
			Integer retard,
			Date caducitat,
			String usuariCodi,
			String procedimentCodi,
			ProcedimentEntity procediment,
			String grup,
			String numExpedient,
			TipusUsuariEnumDto tipusUsuari) {
		return new BuilderV2(
				entitat,
				emisorDir3Codi,
				comunicacioTipus,
				enviamentTipus,
				concepte,
				descripcio,
				enviamentDataProgramada,
				retard,
				caducitat,
				usuariCodi,
				procedimentCodi,
				procediment,
				grup,
				numExpedient,
				tipusUsuari);
	}
	

	public static class Builder {
		NotificacioEntity built;
		Builder(
				EntitatEntity entitat,
				String emisorDir3Codi,
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
			built.enviamentTipus = enviamentTipus;
			built.concepte = concepte;
			built.estat = NotificacioEstatEnumDto.PENDENT;
			built.notificaEnviamentIntent = 0;
			built.registreEnviamentIntent = 0;
			built.notificaEnviamentData = new Date();
		}
		public Builder numExpedient(String numExpedient) {
			built.numExpedient = numExpedient;
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
		public Builder retard(Integer retard) {
			built.retard = retard;
			return this;
		}
		public Builder caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public NotificacioEntity build() {
			return built;
		}
	}
	
	
	public static class BuilderV1 {
		NotificacioEntity built;
		BuilderV1(
				EntitatEntity entitat,
				String emisorDir3Codi,
				NotificacioComunicacioTipusEnumDto comunicacioTipus,
				NotificaEnviamentTipusEnumDto enviamentTipus,
				String concepte,
				String descripcio,
				Date enviamentDataProgramada,
				Integer retard,
				Date caducitat,
				DocumentEntity document,
				PagadorPostalEntity pagadorPostal,
				PagadorCieEntity pagadorCie,
				List<Enviament>enviaments) {
			built = new NotificacioEntity();
			built.entitat = entitat;
			built.emisorDir3Codi = emisorDir3Codi;
			built.comunicacioTipus = comunicacioTipus;
			built.enviamentTipus = enviamentTipus;
			built.concepte = concepte;
			built.descripcio = descripcio;
			built.enviamentDataProgramada = enviamentDataProgramada;
			built.retard = retard;
			built.caducitat = caducitat;
			built.document = document;
			built.pagadorPostal = pagadorPostal;
			built.pagadorCie = pagadorCie;
			
			built.estat = NotificacioEstatEnumDto.PENDENT;
			built.notificaEnviamentIntent = 0;
			built.notificaEnviamentData = new Date();
		}
		public BuilderV1 descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public BuilderV1 caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public BuilderV1 retardPostal(Integer retard) {
			built.retard = retard;
			return this;
		}
		public BuilderV1 usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}
		public NotificacioEntity build() {
			return built;
		}
	}
	
	public static class BuilderV2 {
		NotificacioEntity built;
		BuilderV2(
				EntitatEntity entitat,
				String emisorDir3Codi,
				NotificacioComunicacioTipusEnumDto comunicacioTipus,
				NotificaEnviamentTipusEnumDto enviamentTipus,
				String concepte,
				String descripcio,
				Date enviamentDataProgramada,
				Integer retard,
				Date caducitat,
				String usuariCodi,
				String procedimentCodi,
				ProcedimentEntity procediment,
				String grup,
				String numExpedient,
				TipusUsuariEnumDto tipusUsuari) {
			built = new NotificacioEntity();
			built.entitat = entitat;
			built.emisorDir3Codi = emisorDir3Codi;
			built.comunicacioTipus = comunicacioTipus;
			built.enviamentTipus = enviamentTipus;
			built.concepte = concepte;
			built.descripcio = descripcio;
			built.enviamentDataProgramada = enviamentDataProgramada;
			built.retard = retard;
			built.caducitat = caducitat;
			built.usuariCodi = usuariCodi;
			built.procedimentCodiNotib = procedimentCodi;
			built.grupCodi = grup;
			built.numExpedient = numExpedient;
			built.procediment = procediment;
			built.estat = NotificacioEstatEnumDto.PENDENT;
			built.notificaEnviamentIntent = 0;
			built.registreEnviamentIntent = 0;
			built.notificaEnviamentData = new Date();
			built.tipusUsuari = tipusUsuari;
		}
		public BuilderV2 usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}
		
		public BuilderV2 enviaments(Set<NotificacioEnviamentEntity> enviaments) {
			built.enviaments = enviaments;
			return this;
		}
		
		public BuilderV2 descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public BuilderV2 procedimentCodiNotib(String procedimentCodiNotib) {
			built.procedimentCodiNotib = procedimentCodiNotib;
			return this;
		}
		public BuilderV2 grupCodi(String grupCodi) {
			built.grupCodi = grupCodi;
			return this;
		}
		public BuilderV2 retard(Integer retard) {
			built.retard = retard;
			return this;
		}
		public BuilderV2 caducitat(Date caducitat) {
			built.caducitat = caducitat;
			return this;
		}
		public BuilderV2 document(DocumentEntity document) {
			built.document = document;
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
		if (document.getHash() == null) {
			if (other.document.getHash() != null)
				return false;
		} else if (!document.getHash().equals(other.document.getHash()))
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

	private static final long serialVersionUID = 7206301266966284277L;
}
