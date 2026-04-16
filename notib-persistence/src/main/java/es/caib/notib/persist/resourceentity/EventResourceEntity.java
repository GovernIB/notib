package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.model.EventResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "notificacio_event")
@Getter
@Setter
@NoArgsConstructor
public class EventResourceEntity extends BaseAuditableResourceEntity<EventResource> {

	private static final int ERROR_DESC_MAX_LENGTH = 2048;
	private static final int DESC_MAX_LENGTH = 256;

	@Column(name = "tipus", nullable = false)
	private NotificacioEventTipusEnumDto tipus;

	@Column(name = "data", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data = new Date();


	@Column(name = "error", nullable = false)
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
	private NotificacioResourceEntity notificacio;

	@Setter
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "notificacio_env_id")
	@ForeignKey(name = "not_notenv_noteve_fk")
	private NotificacioEnviamentResourceEntity enviament;

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

		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		var other = (EventResourceEntity) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (tipus != other.tipus) {
			return false;
		}
		if (error != other.error) {
			return false;
		}
		if (notificacio == null) {
			if (other.notificacio != null) {
				return false;
			}
		} else if (!notificacio.equals(other.notificacio)) {
			return false;
		}
		return true;
	}


	@Builder
	public EventResourceEntity(EventResource resource, NotificacioResourceEntity notificacio, NotificacioEnviamentResourceEntity enviament) {

		this.tipus = resource.getTipus();
		this.data = resource.getData();
		this.error = resource.isError();
		this.errorDescripcio = resource.getErrorDescripcio();
		this.fiReintents = resource.getFiReintents();
		this.intents = resource.getIntents();
		this.notificacio = notificacio;
		this.enviament = enviament;
	}


}
