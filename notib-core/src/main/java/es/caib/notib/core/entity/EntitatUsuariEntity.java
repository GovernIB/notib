/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa una Entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_entitat_usuari")
@EntityListeners(AuditingEntityListener.class)
public class EntitatUsuariEntity extends NotibAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "not_entitat_entitatusu_fk")
	protected EntitatEntity entitat;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_codi")
	@ForeignKey(name = "not_usuari_entitatusu_fk")
	protected UsuariEntity usuari;
	@Column(name = "url_callback", length = 256)
	private String callback;
	@Column(name = "usuari_aplicacio", nullable = false)
	private boolean usuariAplicacio;

	public EntitatEntity getEntitat() {
		return entitat;
	}
	public UsuariEntity getUsuari() {
		return usuari;
	}
	public Boolean isUsuariAplicacio() {
		return usuariAplicacio;
	}
	public String getCallback() {
		return callback;
	}

	public void update(
			EntitatEntity entitat,
			UsuariEntity usuari,
			String callback,
			Boolean usuari_aplicacio) {
		this.entitat = entitat;
		this.usuari = usuari;
		this.callback = callback;
		this.usuariAplicacio = usuari_aplicacio;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			UsuariEntity usuari,
			Boolean usuari_aplicacio,
			String callback) {
		return new Builder(
				entitat,
				usuari,
				usuari_aplicacio,
				callback);
	}

	public static class Builder {
		EntitatUsuariEntity built;
		Builder(
				EntitatEntity entitat,
				UsuariEntity usuari,
				Boolean usuari_aplicacio,
				String callback) {
			built = new EntitatUsuariEntity();
			built.entitat = entitat;
			built.usuari = usuari;
			built.usuariAplicacio = usuari_aplicacio;
			built.callback = callback;
		}
		public EntitatUsuariEntity build() {
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
		EntitatUsuariEntity other = (EntitatUsuariEntity) obj;
		
		return getId() == other.getId();
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
