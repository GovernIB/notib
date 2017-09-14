/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEventDto extends AuditoriaDto {

	private Long id;
	private NotificacioEventTipusEnumDto tipus;
	private Date data;
	private String descripcio;
	private boolean error;
	private String errorDescripcio;

	private NotificacioDto notificacio;
	private NotificacioDestinatariDto destinatari;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NotificacioEventTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(NotificacioEventTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public NotificacioDto getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioDto notificacio) {
		this.notificacio = notificacio;
	}
	public NotificacioDestinatariDto getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(NotificacioDestinatariDto destinatari) {
		this.destinatari = destinatari;
	}

	public boolean isDestinatariAssociat() {
		return this.destinatari != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
