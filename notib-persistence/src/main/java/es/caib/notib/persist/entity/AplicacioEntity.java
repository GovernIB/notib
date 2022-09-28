/**
 * 
 */
package es.caib.notib.persist.entity;

import es.caib.notib.persist.audit.NotibAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa una aplicació amb
 * accés a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="not_aplicacio")
@EntityListeners(AuditingEntityListener.class)
public class AplicacioEntity extends NotibAuditable<Long> {

	@Column(name = "usuari_codi", length = 64)
	protected String usuariCodi;
	@Column(name = "callback_url", length = 256)
	private String callbackUrl;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id", nullable = false)
	@ForeignKey(name = "not_aplicacio_entitat_fk")
	protected EntitatEntity entitat;

	public void update(
			String usuariCodi,
			String callbackUrl) {
		this.usuariCodi = usuariCodi;
		this.callbackUrl = callbackUrl;
	}
	
	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			String codi,
			String urlCallback) {
		return new Builder(
				entitat,
				codi,
				urlCallback);
	}

	public static class Builder {
		AplicacioEntity built;
		Builder(
				EntitatEntity entitat,
				String usuariCodi,
				String callbackUrl) {
			built = new AplicacioEntity();
			built.entitat = entitat;
			built.usuariCodi = usuariCodi;
			built.callbackUrl = callbackUrl;
			built.activa = true;
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
