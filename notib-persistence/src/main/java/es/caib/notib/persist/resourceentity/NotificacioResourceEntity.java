package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.persist.entity.ProcSerEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades de notificació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(
	name = BaseConfig.DB_PREFIX + "notificacio",
	uniqueConstraints = @UniqueConstraint(columnNames = { "referencia" }))
@Getter
@Setter
@NoArgsConstructor
public class NotificacioResourceEntity
	extends BaseAuditableResourceEntity<NotificacioResource>
	implements AdminEntitatResourceEntity<NotificacioResource> {

	@Column(name = "usuari_codi", length = 64, nullable = false)
	protected String usuariCodi;
	@Column(name = "emisor_dir3codi", length = 9, nullable = false)
	protected String emisorDir3Codi;
	@Column(name = "com_tipus", nullable = false)
	protected NotificacioComunicacioTipusEnumDto comunicacioTipus;
	@Column(name = "env_tipus", nullable = false)
	protected EnviamentTipus enviamentTipus;
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	protected Date enviamentDataProgramada;
	@Column(name = "concepte", length = 240, nullable = false)
	protected String concepte;
	@Column(name = "descripcio", length = 1000)
	protected String descripcio;
	@Column(name = "retard_postal")
	protected Integer retard;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	protected Date caducitat;
	@Column(name = "caducitat_original")
	@Temporal(TemporalType.DATE)
	protected Date caducitatOriginal;
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
	@Column(name = "registre_numero")
	protected Integer registreNumero;
	@Column(name = "registre_numero_formatat", length = 200)
	protected String registreNumeroFormatat;
	@Column(name = "registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date registreData;
	@Column(name = "registre_num_expedient", length = 80)
	protected String numExpedient;
	@Setter
	@Column(name = "registre_oficina_nom")
	private String registreOficinaNom;
	@Setter
	@Column(name = "registre_llibre_nom")
	private String registreLlibreNom;
	@Column(name = "callback_error")
	protected boolean errorLastCallback;
	@Column(name = "idioma")
	protected Idioma idioma;
	@Column(name = "estat_processat_date")
	protected Date estatProcessatDate;
	@Column(name = "referencia", length = 36)
	protected String referencia;
	@Column(name = "seguent_remesa", length = 36)
	protected String seguentRemesa;
	@Column(name = "num_registre_previ", length = 50)
	protected String numRegistrePrevi;
	@Column(name = "justificant_creat")
	private boolean justificantCreat;
	@Column(name = "origen")
	@Enumerated(EnumType.STRING)
	private EnviamentOrigen origen;
	@Column(name = "deleted")
	private boolean deleted = false;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_entitat_fk"),
		nullable = false)
	protected EntitatResourceEntity entitat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_gestor_fk"),
		nullable = false)
	protected OrganGestorResourceEntity organGestor;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "procediment_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_not_fk"),
		nullable = false)
	protected ProcSerEntity procediment;

	@Builder
	public NotificacioResourceEntity(
		NotificacioResource resource,
		EntitatResourceEntity entitat,
		OrganGestorResourceEntity organGestor,
		ProcSerEntity procediment) {
		this.enviamentDataProgramada = resource.getEnviamentDataProgramada();
		this.concepte = resource.getConcepte();
		this.descripcio = resource.getDescripcio();
		this.retard = resource.getRetard();
		this.caducitat = resource.getCaducitat();
		this.caducitatOriginal = resource.getCaducitatOriginal();
		this.procedimentCodiNotib = resource.getProcedimentCodiNotib();
		this.grupCodi = resource.getGrupCodi();
		this.estat = resource.getEstat();
		this.estatDate = resource.getEstatDate();
		this.tipusUsuari = resource.getTipusUsuari();
		this.motiu = resource.getMotiu();
		this.notificaEnviamentData = resource.getNotificaEnviamentData();
		this.notificaEnviamentNotificaData = resource.getNotificaEnviamentNotificaData();
		this.notificaEnviamentIntent = resource.getNotificaEnviamentIntent();
		this.registreEnviamentIntent = resource.getRegistreEnviamentIntent();
		this.registreNumero = resource.getRegistreNumero();
		this.registreNumeroFormatat = resource.getRegistreNumeroFormatat();
		this.registreData = resource.getRegistreData();
		this.numExpedient = resource.getNumExpedient();
		this.registreOficinaNom = resource.getRegistreOficinaNom();
		this.registreLlibreNom = resource.getRegistreLlibreNom();
		this.errorLastCallback = resource.isErrorLastCallback();
		this.idioma = resource.getIdioma();
		this.estatProcessatDate = resource.getEstatProcessatDate();
		this.referencia = resource.getReferencia();
		this.seguentRemesa = resource.getSeguentRemesa();
		this.numRegistrePrevi = resource.getNumRegistrePrevi();
		this.justificantCreat = resource.isJustificantCreat();
		this.origen = resource.getOrigen();
		this.deleted = resource.isDeleted();
		this.entitat = entitat;
		this.organGestor = organGestor;
		this.procediment = procediment;
	}

}
