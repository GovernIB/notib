/**
 * 
 */
package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Classe del model de dades que representa un event
 * d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name="not_notificacio_event")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioEventEntity extends NotibAuditable<Long> {

	private static final int ERROR_DESC_MAX_LENGTH = 2048;

	@Column(name = "tipus", nullable = false)
	private NotificacioEventTipusEnumDto tipus;

	@Builder.Default
	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data = new Date();
	
//	@Column(name = "descripcio", length = 256)
//	private String descripcio;
	
	@Column(name = "error", nullable = false)
	@Builder.Default
	private boolean error = false;
	
	@Column(name = "error_desc", length = ERROR_DESC_MAX_LENGTH)
	private String errorDescripcio;

	@Column(name = "fi_reintents")
	protected Boolean fiReintents;

	@Column(name = "intents")
	protected int intents;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_id")
	@ForeignKey(name = "NOT_NOTIFICACIO_NOTEVENT_FK")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private NotificacioEntity notificacio;

	@Setter
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_env_id")
	@ForeignKey(name = "not_notenv_noteve_fk")
	private NotificacioEnviamentEntity enviament;
	
//	@Column(name = "callback_estat", length = 10, nullable = true)
//	@Enumerated(EnumType.STRING)
//	private CallbackEstatEnumDto callbackEstat;
//
//	@Column(name = "callback_data")
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date callbackData;
//
//	@Column(name = "callback_intents")
//	private Integer callbackIntents;
//
//	@Column(name = "callback_error_desc", length = ERROR_DESC_MAX_LENGTH)
//	private String callbackError;
//
//	@Column(name = "NOTIFICA_ERROR_TIPUS")
//	protected NotificacioErrorTipusEnumDto errorTipus;

	public NotificacioEventEntity() {
		this.data = new Date();
		this.error = false;
	}

	public void update(boolean error, String errorDescripcio, Boolean fiReintents) {
		this.data = new Date();
		this.error = error;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH/2);;
		this.fiReintents = fiReintents;
		this.intents++;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
		result = prime * result + (error ? 1231 : 1237);
		result = prime * result + ((notificacio == null) ? 0 : notificacio.hashCode());
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
		NotificacioEventEntity other = (NotificacioEventEntity) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (tipus != other.tipus)
			return false;
		if (error != other.error)
			return false;
		if (notificacio == null) {
			if (other.notificacio != null)
				return false;
		} else if (!notificacio.equals(other.notificacio))
			return false;
		return true;
	}
	@PreRemove
	private void preRemove() {
		this.enviament = null;
	}
	private static final long serialVersionUID = -2299453443943600172L;

	// Custom builder setters
	public static class NotificacioEventEntityBuilder {
		private String errorDescripcio;
		public NotificacioEventEntityBuilder errorDescripcio(String errorDescripcio){
			this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH/2);
			return this;
		}
	}
}
