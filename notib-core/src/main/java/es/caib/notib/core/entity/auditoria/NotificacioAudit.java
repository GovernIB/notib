/**
 * 
 */
package es.caib.notib.core.entity.auditoria;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.audit.NotibAuditoria;
import es.caib.notib.core.entity.NotificacioEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;


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
	
	
	
	public static Builder getBuilder(
			NotificacioEntity notificacioEntity,
			TipusOperacio tipusOperacio, 
			String joinPoint) {
		return new Builder(
				notificacioEntity,
				tipusOperacio,
				joinPoint);
	}
	
	public static class Builder {
		NotificacioAudit built;
		Builder(
				NotificacioEntity notificacioEntity,
				TipusOperacio tipusOperacio, 
				String joinPoint) {
			built = new NotificacioAudit();
			built.tipusOperacio = tipusOperacio;
			built.joinPoint = joinPoint;
			built.notificacioId = notificacioEntity.getId();
			built.comunicacioTipus = notificacioEntity.getComunicacioTipus();
			built.tipusUsuari = notificacioEntity.getTipusUsuari();
			built.usuari = notificacioEntity.getUsuariCodi();
			built.emisor = notificacioEntity.getEmisorDir3Codi();
			built.tipus = notificacioEntity.getEnviamentTipus();
			built.entitatId = notificacioEntity.getEntitat().getId();
			built.procediment = notificacioEntity.getProcediment() != null ? notificacioEntity.getProcediment().getCodi() : null;
			built.grup = notificacioEntity.getGrupCodi();
			built.concepte = notificacioEntity.getConcepte();
			built.descripcio = notificacioEntity.getDescripcio();
			built.numExpedient = notificacioEntity.getNumExpedient();
			built.enviamentDataProgramada = notificacioEntity.getEnviamentDataProgramada();
			built.retard = notificacioEntity.getRetard();
			built.caducitat = notificacioEntity.getCaducitat();
			built.documentId = notificacioEntity.getDocument() != null ? notificacioEntity.getDocument().getId() : null;
			built.estat = notificacioEntity.getEstat();
			built.estatDate = notificacioEntity.getEstatDate();
			built.motiu = notificacioEntity.getMotiu();
			built.pagadorPostalId = notificacioEntity.getPagadorPostal() != null ? notificacioEntity.getPagadorPostal().getId() : null;
			built.pagadorCieId = notificacioEntity.getPagadorCie() != null ? notificacioEntity.getPagadorCie().getId() : null;
			built.registreEnviamentIntent = notificacioEntity.getRegistreEnviamentIntent();
			built.registreNumero = notificacioEntity.getRegistreNumero();
			built.registreNumeroFormatat = notificacioEntity.getRegistreNumeroFormatat();
			built.registreData = notificacioEntity.getRegistreData();
			built.notificaEnviamentData = notificacioEntity.getNotificaEnviamentData();
			built.notificaEnviamentIntent = notificacioEntity.getNotificaEnviamentIntent();
			built.notificaErrorTipus = notificacioEntity.getNotificaErrorTipus();
			built.errorLastCallback = notificacioEntity.isErrorLastCallback();
			built.errorEventId = notificacioEntity.getNotificaErrorEvent() != null ? notificacioEntity.getNotificaErrorEvent().getId() : null;
		}
		public NotificacioAudit build() {
			return built;
		}
	}

	private static final long serialVersionUID = 7206301266966284277L;

}
