package es.caib.notib.core.entity;

import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe de model de dades que conté la informació dels òrgans gestors.
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
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_organ_entitat_fk")
	protected EntitatEntity entitat;
	
	@Column(name = "llibre")
	protected String llibre;
	
	@Column(name = "llibre_nom")
	protected String llibreNom;
	
	@Column(name = "oficina")
	protected String oficina;
	
	@Column(name = "oficina_nom")
	protected String oficinaNom;
	
	public void update(
			String codi,
			String nom,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom) {
		this.codi = codi;
		this.nom = nom;
		this.llibre = llibre;
		this.llibreNom = llibreNom;
		this.oficina = oficina;
		this.oficinaNom = oficinaNom;
	}
	public void update(
			String nom) {
		this.nom = nom;
	}
	
	public void updateLlibre(
			String llibre,
			String llibreNom) {
		this.llibre = llibre;
		this.llibreNom = llibreNom;
	}
	
	public void updateOficina(
			String oficina,
			String oficinaNom) {
		this.oficina = oficina;
		this.oficinaNom = oficinaNom;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatEntity entitat,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom) {
		return new Builder(
				codi,
				nom,
				entitat,
				llibre,
				llibreNom,
				oficina,
				oficinaNom);
	}
	
	public static class Builder {
		OrganGestorEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat,
				String llibre,
				String llibreNom,
				String oficina,
				String oficinaNom) {
			built = new OrganGestorEntity();
			built.codi = codi;
			built.nom = nom;
			built.entitat = entitat;
			built.llibre = llibre;
			built.llibreNom = llibreNom;
			built.oficina = oficina;
			built.oficinaNom = oficinaNom;
		}
		public OrganGestorEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
