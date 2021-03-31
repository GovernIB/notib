/**
 * 
 */
package es.caib.notib.core.entity.auditoria;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


/**
 * Classe del model de dades que representa la informació de auditoria d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="not_notificacio_audit")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioAudit extends NotibAuditoria<Long> {

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
	private NotificaEnviamentTipusEnumDto tipus;
	
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
	@Column(name = "motiu")
	private String motiu;
	
	@Column(name = "pagador_postal_id")
	private Long pagadorPostalId;
	@Column(name = "pagador_cie_id")
	private Long pagadorCieId;

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
	@Column(name = "not_error_tipus")
	@Enumerated(EnumType.STRING)
	private NotificacioErrorTipusEnumDto notificaErrorTipus;
	@Column(name = "callback_error")
	private boolean errorLastCallback;
	@Column(name = "event_error")
	private Long errorEventId;
	
	
	
	public NotificacioAudit (
			NotificacioEntity notificacioEntity,
			NotificacioEventEntity lastErrorEvent,
			TipusOperacio tipusOperacio, 
			String joinPoint
			) {
		this.tipusOperacio = tipusOperacio;
		this.joinPoint = joinPoint;
		this.notificacioId = notificacioEntity.getId();
		this.comunicacioTipus = notificacioEntity.getComunicacioTipus();
		this.tipusUsuari = notificacioEntity.getTipusUsuari();
		this.usuari = notificacioEntity.getUsuariCodi();
		this.emisor = notificacioEntity.getEmisorDir3Codi();
		this.tipus = notificacioEntity.getEnviamentTipus();
		this.entitatId = notificacioEntity.getEntitat().getId();
		this.procediment = notificacioEntity.getProcediment() != null ? notificacioEntity.getProcediment().getCodi() : null;
		this.grup = notificacioEntity.getGrupCodi();
		this.concepte = notificacioEntity.getConcepte();
		this.descripcio = notificacioEntity.getDescripcio();
		this.numExpedient = notificacioEntity.getNumExpedient();
		this.enviamentDataProgramada = notificacioEntity.getEnviamentDataProgramada();
		this.retard = notificacioEntity.getRetard();
		this.caducitat = notificacioEntity.getCaducitat();
		this.documentId = notificacioEntity.getDocument() != null ? notificacioEntity.getDocument().getId() : null;
		this.estat = notificacioEntity.getEstat();
		this.estatDate = notificacioEntity.getEstatDate();
		this.motiu = notificacioEntity.getMotiu();
		this.pagadorPostalId = notificacioEntity.getPagadorPostal() != null ? notificacioEntity.getPagadorPostal().getId() : null;
		this.pagadorCieId = notificacioEntity.getPagadorCie() != null ? notificacioEntity.getPagadorCie().getId() : null;
		this.registreEnviamentIntent = notificacioEntity.getRegistreEnviamentIntent();
		this.registreNumero = notificacioEntity.getRegistreNumero();
		this.registreNumeroFormatat = notificacioEntity.getRegistreNumeroFormatat();
		this.registreData = notificacioEntity.getRegistreData();
		this.notificaEnviamentData = notificacioEntity.getNotificaEnviamentData();
		this.notificaEnviamentIntent = notificacioEntity.getNotificaEnviamentIntent();
		this.notificaErrorTipus = lastErrorEvent != null ? lastErrorEvent.getErrorTipus() : null;
		this.errorLastCallback = notificacioEntity.isErrorLastCallback();
		this.errorEventId = lastErrorEvent != null ? lastErrorEvent.getId() : null;
	}

	private static final long serialVersionUID = 7206301266966284277L;

}
