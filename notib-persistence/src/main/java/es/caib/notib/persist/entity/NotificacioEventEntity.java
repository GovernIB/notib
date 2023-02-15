/**
 * 
 */
package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.persist.audit.NotibAuditable;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Classe del model de dades que representa un event
 * d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
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
	
	@Column(name = "descripcio", length = 256)
	private String descripcio;
	
	@Column(name = "error", nullable = false)
	@Builder.Default
	private boolean error = false;
	
	@Column(name = "error_desc", length = ERROR_DESC_MAX_LENGTH)
	private String errorDescripcio;
	
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
	
	@Column(name = "callback_estat", length = 10, nullable = true)
	@Enumerated(EnumType.STRING)
	private CallbackEstatEnumDto callbackEstat;
	
	@Column(name = "callback_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date callbackData;

	@Column(name = "callback_intents")
	private Integer callbackIntents;
	
	@Column(name = "callback_error_desc", length = ERROR_DESC_MAX_LENGTH)
	private String callbackError;

	@Column(name = "NOTIFICA_ERROR_TIPUS")
	protected NotificacioErrorTipusEnumDto errorTipus;

	public NotificacioEventEntity() {
		this.data = new Date();
		this.error = false;
	}

	public int getCallbackIntents() {
		return callbackIntents != null? callbackIntents : 0;
	}
	public void updateCallbackClient(
			CallbackEstatEnumDto estat,
			Integer intents,
			String error,
			int reintentsPeriode) {
		this.callbackIntents = intents;
		this.callbackEstat = estat;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.SECOND, (int) ((reintentsPeriode/7200)*Math.pow(3, callbackIntents)));
		this.callbackData = cal.getTime();
		this.callbackError = StringUtils.abbreviate(error, ERROR_DESC_MAX_LENGTH - 5);
	}
	
	public void callbackInicialitza() {
		this.callbackEstat = CallbackEstatEnumDto.PENDENT;
		this.callbackIntents = 0;
		this.callbackData = new Date();
	}


	public static BuilderOld getBuilder(
			NotificacioEventTipusEnumDto tipus,
			NotificacioEntity notificacio) {
		return new BuilderOld(
				tipus,
				notificacio);
	}
	public static class BuilderOld {
		NotificacioEventEntity built;
		BuilderOld(
				NotificacioEventTipusEnumDto tipus,
				NotificacioEntity notificacio) {
			built = new NotificacioEventEntity();
			built.tipus = tipus;
			built.notificacio = notificacio;
		}
		public BuilderOld descripcio(String descripcio) {
			if (descripcio.length() > 256) {
				descripcio = descripcio.substring(0, 256);
			}
			built.descripcio = descripcio;
			return this;
		}
		public BuilderOld error(boolean error) {
			built.error = error;
			return this;
		}
		public BuilderOld errorDescripcio(String errorDescripcio) {
			built.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH/2);
			return this;
		}
		public BuilderOld enviament(NotificacioEnviamentEntity enviament) {
			built.enviament = enviament;
			return this;
		}
		/** Inicialitza els camps pel callback cap al client. */
		public BuilderOld callbackInicialitza() {
			this.built.callbackInicialitza();
			return this;
		}
		public NotificacioEventEntity build() {
			return built;
		}
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