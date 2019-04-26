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
 * Classe de model de dades que conté la informació dels grups admesos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_grup")
@EntityListeners(AuditingEntityListener.class)
public class GrupEntity extends NotibAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	
	@Column(name = "nom", length = 100)
	private String nom;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_entitat_grup_fk")
	protected EntitatEntity entitat;
	
	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	
	public EntitatEntity getEntitat() {
		return entitat;
	}
	
	public void update(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				entitat);
	}
	
	public static class Builder {
		GrupEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat) {
			built = new GrupEntity();
			built.codi = codi;
			built.nom = nom;
			built.entitat = entitat;
		}
		public GrupEntity build() {
			return built;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrupEntity other = (GrupEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
