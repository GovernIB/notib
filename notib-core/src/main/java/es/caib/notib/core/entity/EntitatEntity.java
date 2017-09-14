/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe del model de dades que representa una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_entitat")
@EntityListeners(AuditingEntityListener.class)
public class EntitatEntity extends NotibAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "tipus", length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private EntitatTipusEnumDto tipus;
	@Column(name = "dir3_codi", length = 9, nullable = false)
	private String dir3Codi;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@Version
	private long version = 0;

	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public EntitatTipusEnumDto getTipus() {
		return tipus;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public boolean isActiva() {
		return activa;
	}

	public void update(
			String codi,
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi,
			String descripcio) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.dir3Codi = dir3Codi;
	}

	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatTipusEnumDto tipus,
			String dir3Codi) {
		return new Builder(
				codi,
				nom,
				tipus,
				dir3Codi);
	}

	public static class Builder {
		EntitatEntity built;
		Builder(
				String codi,
				String nom,
				EntitatTipusEnumDto tipus,
				String dir3Codi) {
			built = new EntitatEntity();
			built.codi = codi;
			built.nom = nom;
			built.tipus = tipus;
			built.dir3Codi = dir3Codi;
			built.activa = true;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public EntitatEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		EntitatEntity other = (EntitatEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
