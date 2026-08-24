package es.caib.notib.persist.resourceentity;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.model.NotificacioTableResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Vista de només lectura de "not_notificacio_table" (id compartit amb "not_notificacio"), pensada per fer-hi JOIN
 * des de NotificacioResourceEntity sense que Hibernate en gestioni mai el cicle de vida (INSERT/UPDATE/DELETE):
 * és una entitat @Immutable, l'única escriptura real d'aquesta taula segueix sent NotificacioTableHelper (via
 * l'entitat legacy NotificacioTableEntity). No estén BaseAuditableResourceEntity perquè aquella base força un id
 * autogenerat per seqüència, incompatible amb compartir la clau primària de NotificacioResourceEntity.
 *
 * @author Límit Tecnologies
 */
@Entity
@Immutable
@Table(name = BaseConfig.DB_PREFIX + "notificacio_table")
@Getter
// @Setter només per conveniència en tests (construir un objecte en memòria); com que l'entitat és @Immutable,
// Hibernate mai persisteix cap canvi fet amb aquests setters.
@Setter
@NoArgsConstructor
public class NotificacioTableResourceEntity implements AdminEntitatResourceEntity<NotificacioTableResource> {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "tipus_usuari")
	private TipusUsuariEnumDto tipusUsuari;
	@Column(name = "notifica_error_date")
	private Date notificaErrorData;
	@Column(name = "notifica_error_descripcio")
	private String notificaErrorDescripcio;
	@Column(name = "env_tipus", nullable = false)
	private EnviamentTipus enviamentTipus;
	@Column(name = "registre_num_expedient", length = 80)
	private String numExpedient;
	@Column(name = "registre_env_intent")
	private int registreEnviamentIntent;
	@Column(name = "concepte", nullable = false)
	private String concepte;
	@Column(name = "estat", nullable = false)
	private NotificacioEstatEnumDto estat;
	@Column(name = "estat_date")
	private Date estatDate;
	@Column(name = "entitat_nom")
	private String entitatNom;
	@Column(name = "procediment_codi")
	private String procedimentCodi;
	@Column(name = "procediment_nom")
	private String procedimentNom;
	@Column(name = "procediment_is_comu")
	private boolean procedimentIsComu;
	@Column(name = "procediment_require_permission")
	private boolean procedimentRequirePermission;
	@Column(name = "procediment_tipus")
	@Enumerated(EnumType.STRING)
	private ProcSerTipusEnum procedimentTipus;
	@Column(name = "organ_id")
	private String organId;
	@Column(name = "organ_codi")
	private String organCodi;
	@Column(name = "organ_nom")
	private String organNom;
	@Column(name = "organ_estat")
	private OrganGestorEstatEnum organEstat;
	@Column(name = "last_event_fi_reintents")
	private boolean isLastEventFiReintents;
	@Column(name = "error_last_event")
	private boolean isErrorLastEvent;
	@Column(name = "estat_processat_date")
	private Date estatProcessatDate;
	@Column(name = "enviada_date")
	private Date enviadaDate;
	@Column(name = "referencia", length = 36)
	protected String referencia;
	@Column(name = "titular", length = 1024)
	private String titular;
	@Column(name = "notifica_ids", length = 1024)
	private String notificaIds;
	@Column(name = "registre_nums", length = 1024)
	private String registreNums;
	@Column(name = "estat_mask")
	private Integer estatMask;
	@Column(name = "estat_string", length = 512)
	private String estatString;
	@Column(name = "document_id")
	private Long documentId;
	@Column(name = "env_cer_data")
	private Date envCerData;
	@Column(name = "reg_env_pendents")
	private boolean hasEnviamentsPendentsRegistre;
	@Column(name = "per_actualitzar")
	private boolean perActualitzar;
	@Column(name = "deleted")
	private boolean deleted;
	@Column(name = "entrega_postal")
	private boolean entregaPostal;
	@Column(name = "entrega_postal_error")
	private boolean entregaPostalError;
	@Column(name = "anulable")
	private boolean anulable;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_entitat_fk"),
		nullable = false)
	protected EntitatResourceEntity entitat;

	@Override
	public boolean isNew() {
		return id == null;
	}

}
