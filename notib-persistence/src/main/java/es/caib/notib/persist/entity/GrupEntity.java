package es.caib.notib.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.persist.audit.NotibAuditable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Classe de model de dades que conté la informació dels grups admesos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "not_grup")
@EntityListeners(AuditingEntityListener.class)
public class GrupEntity extends NotibAuditable<Long> {

	@EqualsAndHashCode.Include
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	
	@Column(name = "nom", length = 100)
	private String nom;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_entitat_grup_fk")
	protected EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_gestor")
	@ForeignKey(name = "not_grup_organ_fk")
	protected OrganGestorEntity organGestor;
	
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
				entitat,
				null);
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatEntity entitat,
			OrganGestorEntity organGestor) {
		return new Builder(
				codi,
				nom,
				entitat,
				organGestor);
	}
	
	public static class Builder {
		GrupEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat,
				OrganGestorEntity organGestor) {
			built = new GrupEntity();
			built.codi = codi;
			built.nom = nom;
			built.entitat = entitat;
			built.organGestor = organGestor;
		}
		public GrupEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -4924926921877674490L;

}
