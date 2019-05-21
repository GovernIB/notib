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
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.ParametresSeu;
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

	@Column(name = "usuari_codi", length = 64, nullable = false)
	protected String usuariCodi;
	@Column(name = "emisor_dir3codi", length = 9, nullable = false)
	protected String emisorDir3Codi;
//	@Column(name = "organ_gestor", length = 9, nullable = false)
//	protected String organGestor;
	@Column(name = "com_tipus", nullable = false)
	protected NotificacioComunicacioTipusEnumDto comunicacioTipus;
	
	@Column(name = "env_tipus", nullable = false)
	protected NotificaEnviamentTipusEnumDto enviamentTipus;
	
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	protected Date enviamentDataProgramada;
	
	@Column(name = "concepte", length = 50, nullable = false)
	protected String concepte;
	
	@Column(name = "descripcio", length = 100)
	protected String descripcio;
	
	@Column(name = "retard_postal")
	protected Integer retard;
	
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	protected Date caducitat;
	
	@Column(name = "proc_codi_notib", length = 6, nullable = false)
	protected String procedimentCodiNotib;
	
	@Column(name = "grup_codi", length = 6, nullable = false)
	protected String grupCodi;
	
	@Column(name = "estat", nullable = false)
	protected NotificacioEstatEnumDto estat;
	
//	@Column(name = "estat_date")
//	protected Date estatDate;
	
	@Column(name = "motiu")
	protected String motiu;
	
	@Column(name = "not_error_tipus")
	protected NotificacioErrorTipusEnumDto notificaErrorTipus;
	
	@Column(name = "not_env_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaEnviamentData;
	
	@Column(name = "not_reenv_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date notificaReEnviamentData;
	
	@Column(name = "not_env_intent")
	protected int notificaEnviamentIntent;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "not_error_event_id")
	@ForeignKey(name = "not_noterrevent_notificacio_fk")
	protected NotificacioEventEntity notificaErrorEvent;
	
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.EAGER)
	protected Set<NotificacioEnviamentEntity> enviaments = new LinkedHashSet<NotificacioEnviamentEntity>();
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY)
	protected Set<NotificacioEventEntity> events = new LinkedHashSet<NotificacioEventEntity>();
	
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
	
	/*Parametres de la seu*/
	@Column(name = "seu_exp_serdoc", length = 10)
	protected String seuExpedientSerieDocumental;
	@Column(name = "seu_exp_uniorg", length = 10, nullable = false)
	protected String seuExpedientUnitatOrganitzativa;
	@Column(name = "seu_exp_ideni", length = 52, nullable = false)
	protected String seuExpedientIdentificadorEni;
	@Column(name = "seu_exp_titol", length = 256, nullable = false)
	protected String seuExpedientTitol;
	@Column(name = "seu_proc_codi", length = 256, nullable = false)
	protected String seuProcedimentCodi;
	@Column(name = "seu_reg_oficina", length = 256, nullable = false)
	protected String seuRegistreOficina;
	@Column(name = "seu_reg_llibre", length = 256, nullable = false)
	protected String seuRegistreLlibre;
	@Column(name = "seu_reg_organ", length = 256, nullable = false)
	protected String seuRegistreOrgan;
	@Column(name = "seu_idioma", length = 256, nullable = false)
	protected String seuIdioma;
	@Column(name = "seu_avis_titol", length = 256, nullable = false)
	protected String seuAvisTitol;
	@Column(name = "seu_avis_text", length = 256, nullable = false)
	protected String seuAvisText;
	@Column(name = "seu_avis_mobil", length = 256)
	protected String seuAvisTextMobil;
	@Column(name = "seu_ofici_titol", length = 256, nullable = false)
	protected String seuOficiTitol;
	@Column(name = "seu_ofici_text", length = 256, nullable = false)
	protected String seuOficiText;
	
	
	/*Parametres del registre*/
//	@Column(name = "registre_oficina", length = 52, nullable = false)
//	protected String oficina;
//	@Column(name = "registre_organ", length = 10)
//	protected String organ;
//	@Column(name = "registre_llibre", length = 256)
//	protected String llibre;
	@Column(name = "registre_numero", length = 19)
	protected Integer registreNumero;
	@Column(name = "registre_numero_formatat", length = 200)
	protected String registreNumeroFormatat;
	@Column(name = "registre_data")
	@Temporal(TemporalType.DATE)
	protected Date registreData;
//	@Column(name = "registre_extracte", length = 52, nullable = false)
//	protected String extracte;
//	@Column(name = "registre_doc_fisica", nullable = false)
//	protected RegistreDocumentacioFisicaEnumDto docFisica;
//	@Column(name = "registre_idioma", length = 2, nullable = false)
//	@Enumerated(EnumType.STRING)
//	protected IdiomaEnumDto idioma;
//	@Column(name = "registre_tipus_assumpte", length = 256, nullable = false)
//	protected String tipusAssumpte;
	@Column(name = "registre_num_expedient", length = 256, nullable = false)
	protected String numExpedient;
//	@Column(name = "registre_ref_externa", length = 52, nullable = false)
//	protected String refExterna;
//	@Column(name = "registre_codi_assumpte", length = 256, nullable = false)
//	protected String codiAssumpte;
//	@Column(name = "registre_observacions", length = 256, nullable = false)
//	protected String observacions;
	
	@Transient
	protected boolean permisProcessar;

	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public NotificaEnviamentTipusEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
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
//	public String getProcedimentDescripcioSia() {
//		return procedimentDescripcioSia;
//	}
	public Date getCaducitat() {
		return caducitat;
	}
//	public String getDocumentArxiuNom() {
//		return documentArxiuNom;
//	}
//	public String getDocumentArxiuId() {
//		return documentArxiuId;
//	}
////	public String getCsv_uuid() {
////		return csv_uuid;
////	}
//	public String getDocumentHash() {
//		return documentHash;
//	}
//	public boolean isDocumentNormalitzat() {
//		return documentNormalitzat;
//	}
//	public boolean isDocumentGenerarCsv() {
//		return documentGenerarCsv;
//	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public String getMotiu() {
		return motiu;
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
	public Set<NotificacioEnviamentEntity> getEnviaments() {
		return enviaments;
	}
	public Set<NotificacioEventEntity> getEvents() {
		return events;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public Date getNotificaReEnviamentData() {
		return notificaReEnviamentData;
	}
//	public String getProcedimentCodiSia() {
//		return procedimentCodiSia;
//	}
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
	public String getSeuProcedimentCodi() {
		return seuProcedimentCodi;
	}
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public String getSeuRegistreOrgan() {
		return seuRegistreOrgan;
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
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public PagadorPostalEntity getPagadorPostal() {
		return pagadorPostal;
	}
	public PagadorCieEntity getPagadorCie() {
		return pagadorCie;
	}
	public DocumentEntity getDocument() {
		return document;
	}
	public void addEnviament(
			NotificacioEnviamentEntity enviament) {
		this.enviaments.add(enviament);
	}
	public Integer getRegistreNumero() {
		return registreNumero;
	}
	public String getRegistreNumeroFormatat() {
		return registreNumeroFormatat;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public ProcedimentEntity getProcediment() {
		return procediment;
	}
	public Integer getRetard() {
		return retard;
	}
	public String getNumExpedient() {
		return numExpedient;
	}
	public boolean isPermisProcessar() {
		return permisProcessar;
	}
	public void setPermisProcessar(boolean permisProcessar) {
		this.permisProcessar = permisProcessar;
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
	public void updateEstat(
			NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public void updateMotiu(String motiu) {
		this.motiu = motiu;
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
			List<Enviament>enviaments,
			ParametresSeu parametresSeu) {
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
				enviaments,
				parametresSeu);
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
			String numExpedient) {
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
				numExpedient);
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
				List<Enviament>enviaments,
				ParametresSeu parametresSeu) {
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
//			built.enviaments = enviaments;
			built.seuAvisText = parametresSeu.getAvisText();
			built.seuAvisTextMobil = parametresSeu.getAvisTextMobil();
			built.seuAvisTitol = parametresSeu.getAvisTitol();
			built.seuExpedientIdentificadorEni = parametresSeu.getExpedientIdentificadorEni();
			built.seuExpedientSerieDocumental = parametresSeu.getExpedientSerieDocumental();
			built.seuExpedientTitol = parametresSeu.getExpedientTitol();
			built.seuExpedientUnitatOrganitzativa = parametresSeu.getExpedientUnitatOrganitzativa();
			built.seuIdioma = parametresSeu.getIdioma();
			built.seuOficiText = parametresSeu.getOficiText();
			built.seuOficiTitol = parametresSeu.getOficiTitol();
			built.seuProcedimentCodi = parametresSeu.getProcedimentCodi();
			built.seuRegistreLlibre = parametresSeu.getRegistreLlibre();
			built.seuRegistreOficina = parametresSeu.getRegistreOficina();
			built.seuRegistreOrgan = parametresSeu.getRegistreOrgan();
			
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
		public BuilderV1 seuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
			built.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
			return this;
		}
		public BuilderV1 seuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
			built.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
			return this;
		}
		public BuilderV1 seuAvisTitol(String seuAvisTitol) {
			built.seuAvisTitol = seuAvisTitol;
			return this;
		}
		public BuilderV1 seuAvisText(String seuAvisText) {
			built.seuAvisText = seuAvisText;
			return this;
		}
		public BuilderV1 seuAvisTextMobil(String seuAvisTextMobil) {
			built.seuAvisTextMobil = seuAvisTextMobil;
			return this;
		}
		public BuilderV1 seuOficiTitol(String seuOficiTitol) {
			built.seuOficiTitol = seuOficiTitol;
			return this;
		}
		public BuilderV1 seuOficiText(String seuOficiText) {
			built.seuOficiText = seuOficiText;
			return this;
		}
		public BuilderV1 seuRegistreLlibre(String seuRegistreLlibre) {
			built.seuRegistreLlibre = seuRegistreLlibre;
			return this;
		}
		public BuilderV1 seuRegistreOficina(String seuRegistreOficina) {
			built.seuRegistreOficina = seuRegistreOficina;
			return this;
		}
		public BuilderV1 seuRegistreOrgan(String seuRegistreOrgan) {
			built.seuRegistreOrgan = seuRegistreOrgan;
			return this;
		}
		public BuilderV1 seuIdioma(String seuIdioma) {
			built.seuIdioma = seuIdioma;
			return this;
		}
		public BuilderV1 seuExpedientTitol(String seuExpedientTitol) {
			built.seuExpedientTitol = seuExpedientTitol;
			return this;
		}
		public BuilderV1 seuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
			built.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
			return this;
		}
		public BuilderV1 seuProcedimentCodi(String seuProcedimentCodi) {
			built.seuProcedimentCodi = seuProcedimentCodi;
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
				String numExpedient) {
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
			built.notificaEnviamentData = new Date();
		}
		public BuilderV2 usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}
//		public Builder registreExtracte(String registreExtracte) {
//			built.registreExtracte = registreExtracte;
//			return this;
//		}
//		public Builder registreDocFisica(String registreDocFisica) {
//			built.registreDocFisica = registreDocFisica;
//			return this;
//		}
//		public Builder registreIdioma(String registreIdioma) {
//			built.registreIdioma = registreIdioma;
//			return this;
//		}
//		public Builder registreTipusAssumpte(String registreTipusAssumpte) {
//			built.registreTipusAssumpte = registreTipusAssumpte;
//			return this;
//		}
//		public Builder registreNumExpedient(String registreNumExpedient) {
//			built.registreNumExpedient = registreNumExpedient;
//			return this;
//		}
//		public Builder registreRefExterna(String registreRefExterna) {
//			built.registreRefExterna = registreRefExterna;
//			return this;
//		}
//		public Builder registreCodiAssumpte(String registreCodiAssumpte) {
//			built.registreCodiAssumpte = registreCodiAssumpte;
//			return this;
//		}
//		public Builder registreObservacions(String registreObservacions) {
//			built.registreObservacions = registreObservacions;
//			return this;
//		}
		
		public BuilderV2 enviaments(Set<NotificacioEnviamentEntity> enviaments) {
			built.enviaments = enviaments;
			return this;
		}
//		
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
//		public Builder procedimentDescripcioSia(String procedimentDescripcioSia) {
//			built.procedimentDescripcioSia = procedimentDescripcioSia;
//			return this;
//		}
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
