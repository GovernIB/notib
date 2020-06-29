package es.caib.notib.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_organ_gestor")
@EntityListeners(AuditingEntityListener.class)
public class OrganGestorEntity extends AbstractPersistable<Long> {
	
	@NaturalId
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	protected String codi;
	
	@Column(name = "nom", length = 1000)
	protected String nom;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_organ_entitat_fk")
	protected EntitatEntity entitat;
	
	public void update(
			String nom) {
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
		OrganGestorEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat) {
			built = new OrganGestorEntity();
			built.codi = codi;
			built.nom = nom;
			built.entitat = entitat;
		}
		public OrganGestorEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
