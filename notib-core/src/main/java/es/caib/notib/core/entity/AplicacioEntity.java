/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa una aplicació amb
 * accés a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_aplicacio")
@EntityListeners(AuditingEntityListener.class)
public class AplicacioEntity extends NotibAuditable<Long> {

	@Column(name = "usuari_codi", length = 64)
	protected String usuariCodi;
	@Column(name = "callback_url", length = 256)
	private String callbackUrl;

	public String getUsuariCodi() {
		return usuariCodi;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	
	public void update(
			String usuariCodi,
			String callbackUrl) {
		this.usuariCodi = usuariCodi;
		this.callbackUrl = callbackUrl;
	}
	
	public static Builder getBuilder(
			String codi,
			String urlCallback) {
		return new Builder(
				codi,
				urlCallback);
	}

	public static class Builder {
		AplicacioEntity built;
		Builder(
				String usuariCodi,
				String callbackUrl) {
			built = new AplicacioEntity();
			built.usuariCodi = usuariCodi;
			built.callbackUrl = callbackUrl;
		}
		public AplicacioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (int) (prime * result + getId());
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
		AplicacioEntity other = (AplicacioEntity) obj;
		
		return getId() == other.getId();
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
