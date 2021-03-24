/**
 * 
 */
package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;


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
	
	@Column(name = "proc_codi_notib", length = 9)
	protected String procedimentCodiNotib;
	
	@Column(name = "grup_codi", length = 64)
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
	
	@Column(name = "not_env_data_notifica")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEnviamentNotificaData;
	
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
	
	@Column(name = "registre_num_expedient", length = 80)
	protected String numExpedient;
	
	@Column(name = "callback_error")
	protected boolean errorLastCallback;

	@Column(name = "idioma")
	protected IdiomaEnumDto idioma;

	@Setter
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
	@JoinColumn(name = "not_error_event_id")
	@ForeignKey(name = "not_noterrevent_notificacio_fk")
	protected NotificacioEventEntity notificaErrorEvent;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_entitat_notificacio_fk")
	protected EntitatEntity entitat;
	
	/*pagador a Postal*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pagador_postal_id")
	@ForeignKey(name = "not_pagador_postal_not_fk")
	protected PagadorPostalEntity pagadorPostal;
	
	/*pagador CIE*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pagador_cie_id")
	@ForeignKey(name = "not_pagador_cie_not_fk")
	protected PagadorCieEntity pagadorCie;
	
	/*Procediment*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id")
	@ForeignKey(name = "not_procediment_not_fk")
	protected ProcedimentEntity procediment;
	
	/*Procediment*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_organ_id")
	@ForeignKey(name = "not_procorgan_not_fk")
	protected ProcedimentOrganEntity procedimentOrgan;
	
	/*document*/
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	@Index(name = "NOT_NOTIF_DOCUMENT_ID_INDEX")
	protected DocumentEntity document;

	/*document*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document2_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	protected DocumentEntity document2;

	/*document*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document3_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	protected DocumentEntity document3;

	/*document*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document4_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	protected DocumentEntity document4;

	/*document*/
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "document5_id")
	@ForeignKey(name = "not_document_notificacio_fk")
	protected DocumentEntity document5;

	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_gestor", referencedColumnName = "codi")
	@ForeignKey(name = "not_not_organ_fk")
	protected OrganGestorEntity organGestor;
	
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	protected Set<NotificacioEnviamentEntity> enviaments = new LinkedHashSet<NotificacioEnviamentEntity>();
	
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH},
			orphanRemoval = true)
	protected Set<NotificacioEventEntity> events = new LinkedHashSet<NotificacioEventEntity>();

	@Setter
	@Column(name = "registre_oficina_nom")
	private String registreOficinaNom;

	@Setter
	@Column(name = "registre_llibre_nom")
	private String registreLlibreNom;

	@Column(name = "IS_ERROR_LAST_EVENT")
	protected Boolean errorLastEvent;

	@Transient
	protected boolean permisProcessar;
	@Transient
	protected boolean hasEnviamentsPendents;
	@Transient
	protected boolean hasEnviamentsPendentsRegistre;

//	@Transient
//	protected NotificacioEnviamentEstatEnumDto notificaEstat;

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
	
	public void setHasEnviamentsPendents(boolean hasEnviamentsPendents) {
		this.hasEnviamentsPendents = hasEnviamentsPendents;
	}

	public void setHasEnviamentsPendentsRegistre(boolean hasEnviamentsPendentsRegistre) {
		this.hasEnviamentsPendentsRegistre = hasEnviamentsPendentsRegistre;
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
	
	public void updateNotificaEnviamentData() {
		this.notificaEnviamentNotificaData = new Date();
	}
	
	public void updateRegistreNouEnviament(int reintentsPeriodeRegistre) {
		this.registreEnviamentIntent++;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MILLISECOND, reintentsPeriodeRegistre*(2^registreEnviamentIntent));
		this.registreData = cal.getTime();
	}
	
	public void refreshRegistre() {
		this.registreEnviamentIntent = 0;	
		Calendar cal = GregorianCalendar.getInstance();
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
	
	public void update (
			EntitatEntity entitat,
			String emisorDir3Codi,
			OrganGestorEntity organGestor,
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
			TipusUsuariEnumDto tipusUsuari,
			DocumentEntity document,
			DocumentEntity document2,
			DocumentEntity document3,
			DocumentEntity document4,
			DocumentEntity document5,
			ProcedimentOrganEntity procedimentOrgan,
			IdiomaEnumDto idioma) {
		this.entitat = entitat;
		this.emisorDir3Codi = emisorDir3Codi;
		this.organGestor = organGestor;
		this.comunicacioTipus = comunicacioTipus;
		this.enviamentTipus = enviamentTipus;
		this.concepte = concepte;
		this.descripcio = descripcio;
		this.enviamentDataProgramada = enviamentDataProgramada;
		this.retard = retard;
		this.caducitat = caducitat;
		this.usuariCodi = usuariCodi;
		this.procedimentCodiNotib = procedimentCodi;
		this.procediment = procediment;
		this.grupCodi = grup;
		this.numExpedient = numExpedient;
		this.tipusUsuari = tipusUsuari;
		this.document = document;
		this.document2 = document2;
		this.document3 = document3;
		this.document4 = document4;
		this.document5 = document5;
		this.procedimentOrgan = procedimentOrgan;
		this.idioma = idioma;
		
		this.registreEnviamentIntent = 0;
		this.notificaEnviamentIntent = 0;
	}
	
	public static BuilderV2 getBuilderV2(
			EntitatEntity entitat,
			String emisorDir3Codi,
			OrganGestorEntity organGestor,
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
			TipusUsuariEnumDto tipusUsuari,
			ProcedimentOrganEntity procedimentOrgan,
			IdiomaEnumDto idioma) {
		return new BuilderV2(
				entitat,
				emisorDir3Codi,
				organGestor,
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
				tipusUsuari,
				procedimentOrgan,
				idioma);
	}
	

	public static class BuilderV2 {
		NotificacioEntity built;
		BuilderV2(
				EntitatEntity entitat,
				String emisorDir3Codi,
				OrganGestorEntity organGestor,
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
				TipusUsuariEnumDto tipusUsuari,
				ProcedimentOrganEntity procedimentOrgan,
				IdiomaEnumDto idioma) {
			built = new NotificacioEntity();
			built.entitat = entitat;
			built.emisorDir3Codi = emisorDir3Codi;
			built.organGestor = organGestor;
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
			built.procedimentOrgan = procedimentOrgan;
			built.idioma = idioma == null ? IdiomaEnumDto.CA : idioma;
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
		public BuilderV2 document2(DocumentEntity document2) {
			built.document2 = document2;
			return this;
		}
		public BuilderV2 document3(DocumentEntity document3) {
			built.document3 = document3;
			return this;
		}
		public BuilderV2 document4(DocumentEntity document4) {
			built.document4 = document4;
			return this;
		}
		public BuilderV2 document5(DocumentEntity document5) {
			built.document5 = document5;
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

	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}

	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}

	public void setComunicacioTipus(NotificacioComunicacioTipusEnumDto comunicacioTipus) {
		this.comunicacioTipus = comunicacioTipus;
	}

	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}

	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}

	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public void setRetard(Integer retard) {
		this.retard = retard;
	}

	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}

	public void setProcedimentCodiNotib(String procedimentCodiNotib) {
		this.procedimentCodiNotib = procedimentCodiNotib;
	}

	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}

	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}

	public void setEstatDate(Date estatDate) {
		this.estatDate = estatDate;
	}

	public void setTipusUsuari(TipusUsuariEnumDto tipusUsuari) {
		this.tipusUsuari = tipusUsuari;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	public void setNotificaErrorTipus(NotificacioErrorTipusEnumDto notificaErrorTipus) {
		this.notificaErrorTipus = notificaErrorTipus;
	}

	public void setNotificaEnviamentData(Date notificaEnviamentData) {
		this.notificaEnviamentData = notificaEnviamentData;
	}

	public void setNotificaEnviamentIntent(int notificaEnviamentIntent) {
		this.notificaEnviamentIntent = notificaEnviamentIntent;
	}

	public void setRegistreEnviamentIntent(int registreEnviamentIntent) {
		this.registreEnviamentIntent = registreEnviamentIntent;
	}

	public void setRegistreNumero(Integer registreNumero) {
		this.registreNumero = registreNumero;
	}

	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}

	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}

	public void setNumExpedient(String numExpedient) {
		this.numExpedient = numExpedient;
	}

	public void setErrorLastCallback(boolean errorLastCallback) {
		this.errorLastCallback = errorLastCallback;
	}

	public void setNotificaErrorEvent(NotificacioEventEntity notificaErrorEvent) {
		this.notificaErrorEvent = notificaErrorEvent;
	}

	public void setEntitat(EntitatEntity entitat) {
		this.entitat = entitat;
	}

	public void setPagadorPostal(PagadorPostalEntity pagadorPostal) {
		this.pagadorPostal = pagadorPostal;
	}

	public void setPagadorCie(PagadorCieEntity pagadorCie) {
		this.pagadorCie = pagadorCie;
	}

	public void setProcediment(ProcedimentEntity procediment) {
		this.procediment = procediment;
	}

	public void setDocument(DocumentEntity document) {
		this.document = document;
	}

	public void setOrganGestor(OrganGestorEntity organGestor) {
		this.organGestor = organGestor;
	}

	public void setEnviaments(Set<NotificacioEnviamentEntity> enviaments) {
		this.enviaments = enviaments;
	}

	public void setEvents(Set<NotificacioEventEntity> events) {
		this.events = events;
	}

//	public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
//		this.notificaEstat = notificaEstat;
//	}

	public boolean isTipusUsuariAplicacio() {
		return this.tipusUsuari != null && this.tipusUsuari.equals(TipusUsuariEnumDto.APLICACIO);
	}

	@PreRemove
	private void preRemove() {
		this.enviaments = null;
		this.events = null;
	}

	private static final long serialVersionUID = 7206301266966284277L;

}
