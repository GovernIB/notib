package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.explotacio.EnviamentOrigen;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

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
	private String usuariCodi;
	@Column(name = "emisor_dir3codi", length = 9, nullable = false)
	private String emisorDir3Codi;
	@Column(name = "com_tipus", nullable = false)
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	@Column(name = "env_tipus", nullable = false)
	private EnviamentTipus enviamentTipus;
	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	private Date enviamentDataProgramada;
	@Column(name = "concepte", length = 240, nullable = false)
	private String concepte;
	@Column(name = "descripcio", length = 1000)
	private String descripcio;
	@Column(name = "retard_postal")
	private Integer retard;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	private Date caducitat;
	@Column(name = "caducitat_original")
	@Temporal(TemporalType.DATE)
	private Date caducitatOriginal;
	@Column(name = "proc_codi_notib", length = 9)
	private String procedimentCodiNotib;
	@Column(name = "grup_codi", length = 64)
	private String grupCodi;
	@Column(name = "estat", nullable = false)
	private NotificacioEstatEnumDto estat;
	@Column(name = "estat_date")
	private Date estatDate;
	@Column(name = "tipus_usuari")
	private TipusUsuariEnumDto tipusUsuari;
	@Column(name = "motiu")
	private String motiu;
	@Column(name = "not_env_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEnviamentData;
	@Column(name = "not_env_data_notifica")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEnviamentNotificaData;
	@Column(name = "not_env_intent")
	private int notificaEnviamentIntent;
	@Column(name = "registre_env_intent")
	private int registreEnviamentIntent;
	@Column(name = "registre_numero")
	private Integer registreNumero;
	@Column(name = "registre_numero_formatat", length = 200)
	private String registreNumeroFormatat;
	@Column(name = "registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registreData;
	@Column(name = "registre_num_expedient", length = 80)
	private String numExpedient;
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
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_gestor_fk"),
		nullable = false)
	private OrganGestorResourceEntity organGestor;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "procediment_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_not_fk"),
		nullable = false)
	private ProcedimentResourceEntity procediment;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "document_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "document_notificacio_fk"),
		nullable = false)
	@org.hibernate.annotations.Index(name = BaseConfig.DB_PREFIX + "notif_document_id_index")
	private DocumentResourceEntity document;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "document2_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "document2_notificacio_fk"))
	protected DocumentResourceEntity document2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "document3_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "document3_notificacio_fk"))
	protected DocumentResourceEntity document3;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "document4_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "document4_notificacio_fk"))
	protected DocumentResourceEntity document4;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "document5_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "document5_notificacio_fk"))
	protected DocumentResourceEntity document5;

	@Formula("(select ntb.enviada_date from " + BaseConfig.DB_PREFIX + "notificacio_table ntb where ntb.id = id)")
	private Date enviadaDate;
	@Formula("(select ntb.estat_string from " + BaseConfig.DB_PREFIX + "notificacio_table ntb where ntb.id = id)")
	private String estatString;

	@Builder
	public NotificacioResourceEntity(
		NotificacioResource resource,
		EntitatResourceEntity entitat,
		OrganGestorResourceEntity organGestor,
		ProcedimentResourceEntity procediment) {
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
