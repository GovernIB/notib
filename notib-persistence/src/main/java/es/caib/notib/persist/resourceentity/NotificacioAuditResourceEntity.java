package es.caib.notib.persist.resourceentity;


import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioAuditResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentAuditResource;
import es.caib.notib.logic.intf.service.AuditService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base d'auditoria d'una notificació
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "notificacio_audit")
@Getter
@Setter
@NoArgsConstructor
public class NotificacioAuditResourceEntity extends BaseAuditableResourceEntity<NotificacioAuditResource> {

	// TODO AQUESTS DOS CAMPS A LA ENTITY CLASSICA VENENE DE EXTESOS DE NotibAuditoria
	@Enumerated(EnumType.STRING)
	protected AuditService.TipusOperacio tipusOperacio;
	protected String joinPoint;
	// TODO ---------------------------

	@Column(name = "notificacio_id")
	private Long notificacioId;

	@Column(name = "sincron")
	@Enumerated(EnumType.STRING)
	private NotificacioComunicacioTipusEnumDto comunicacioTipus;
	@Column(name = "tipus_usuari")
	@Enumerated(EnumType.STRING)
	private TipusUsuariEnumDto tipusUsuari;
	@Column(name = "usuari", length = 64)
	private String usuari;

	@Column(name = "emisor", length = 9)
	private String emisor;
	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private EnviamentTipus tipus;

	@Column(name = "entitat_id")
	private Long entitatId;
	@Column(name = "organ", length = 64)
	private String organ;
	@Column(name = "procediment", length = 64)
	private String procediment;
	@Column(name = "grup", length = 64)
	private String grup;

	@Column(name = "concepte", length = 255)
	private String concepte;
	@Column(name = "descripcio", length = 1000)
	private String descripcio;
	@Column(name = "num_expedient", length = 80)
	private String numExpedient;

	@Column(name = "env_data_prog")
	@Temporal(TemporalType.DATE)
	private Date enviamentDataProgramada;
	@Column(name = "retard_postal")
	private Integer retard;
	@Column(name = "caducitat")
	@Temporal(TemporalType.DATE)
	private Date caducitat;

	@Column(name = "document_id")
	private Long documentId;

	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificacioEstatEnumDto estat;
	@Column(name = "estat_date")
	private Date estatDate;
	@Column(name = "estat_processat_date")
	private Date estatProcessatDate;
	@Column(name = "motiu")
	private String motiu;

//	@Column(name = "pagador_postal_id")
//	private Long pagadorPostalId;
//	@Column(name = "pagador_cie_id")
//	private Long pagadorCieId;

	// Registre
	@Column(name = "registre_env_intent")
	private int registreEnviamentIntent;
	@Column(name = "registre_numero", length = 19)
	private Integer registreNumero;
	@Column(name = "registre_numero_formatat", length = 200)
	private String registreNumeroFormatat;
	@Column(name = "registre_data")
	@Temporal(TemporalType.DATE)
	private Date registreData;

	// Notifica
	@Column(name = "not_env_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificaEnviamentData;
	@Column(name = "not_env_intent")
	private int notificaEnviamentIntent;

	// Errors
//	@Column(name = "not_error_tipus")
//	@Enumerated(EnumType.STRING)
//	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	@Column(name = "callback_error")
	private boolean errorLastCallback;
	@Column(name = "event_error")
	private Long errorEventId;
	@Column(name = "referencia", length = 36)
	protected String referencia;
}
