/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.TipusAutenticacioEnumDto;
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
	@Column(name = "tipus_autenticacio", length = 32)
	private TipusAutenticacioEnumDto tipusAutenticacio;

	public String getUsuariCodi() {
		return usuariCodi;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public TipusAutenticacioEnumDto getTipusAutenticacio() {
		return tipusAutenticacio;
	}
	
	public void update(
			String usuariCodi,
			String callbackUrl,
			TipusAutenticacioEnumDto tipusAutenticacio) {
		this.usuariCodi = usuariCodi;
		this.callbackUrl = callbackUrl;
		this.tipusAutenticacio = tipusAutenticacio;
	}
	
	public static Builder getBuilder(
			String codi,
			String urlCallback,
			TipusAutenticacioEnumDto tipusAutenticacio) {
		return new Builder(
				codi,
				urlCallback,
				tipusAutenticacio);
	}

	public static class Builder {
		AplicacioEntity built;
		Builder(
				String usuariCodi,
				String callbackUrl,
				TipusAutenticacioEnumDto tipusAutenticacio) {
			built = new AplicacioEntity();
			built.usuariCodi = usuariCodi;
			built.callbackUrl = callbackUrl;
			built.tipusAutenticacio = tipusAutenticacio;
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
