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
import lombok.Getter;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_procediment")
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentEntity extends NotibAuditable<Long> {
	
	@Column(name = "codi", length = 64, nullable = false)
	protected String codi;
	
	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	
	@Column(name = "retard")
	protected Integer retard;
	
	@Column(name = "caducitat")
	protected Integer caducitat;
	
	@Column(name = "llibre")
	protected String llibre;
	
	@Column(name = "oficina")
	protected String oficina;
	
	@Column(name = "tipusassumpte", length = 255)
	protected String tipusAssumpte;
	
	@Column(name = "tipusassumpte_nom", length = 255)
	protected String tipusAssumpteNom;
	
	@Column(name = "codiassumpte", length = 255)
	protected String codiAssumpte;
	
	@Column(name = "codiassumpte_nom", length = 255)
	protected String codiAssumpteNom;
	
	@Column(name = "agrupar", length = 64, nullable = false)
	protected boolean agrupar;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_entitat_fk")
	protected EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pagadorpostal")
	@ForeignKey(name = "not_pagador_postal_fk")
	protected PagadorPostalEntity pagadorpostal;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pagadorcie")
	@ForeignKey(name = "not_pagador_cie_fk")
	protected PagadorCieEntity pagadorcie;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_gestor", referencedColumnName = "codi")
	@ForeignKey(name = "not_proc_organ_fk")
	protected OrganGestorEntity organGestor;
	
	public void update(
			String codi,
			String nom,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorcostal,
			PagadorCieEntity pagadorcie,
			int retard,
			int caducitat,
			boolean agrupar,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom,
			OrganGestorEntity organGestor,
			String tipusAssumpte,
			String tipusAssumpteNom,
			String codiAssumpte,
			String codiAssumpteNom) {
		this.codi = codi;
		this.nom = nom;
		this.entitat = entitat;
		this.pagadorpostal = pagadorcostal;
		this.pagadorcie = pagadorcie;
		this.agrupar = agrupar;
		this.llibre = llibre;
		this.oficina = oficina;
		this.organGestor = organGestor;
		this.retard = retard;
		this.caducitat = caducitat;
		this.tipusAssumpte = tipusAssumpte;
		this.tipusAssumpteNom = tipusAssumpteNom;
		this.codiAssumpte = codiAssumpte;
		this.codiAssumpteNom = codiAssumpteNom;
	}
	
	public void update(
			String nom,
			OrganGestorEntity organGestor) {
		this.nom = nom;
		this.organGestor = organGestor;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			int retard,
			int caducitat,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorpostal,
			PagadorCieEntity pagadorcie,
			boolean agrupar,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom,
			OrganGestorEntity organGestor,
			String tipusAssumpte,
			String tipusAssumpteNom,
			String codiAssumpte,
			String codiAssumpteNom) {
		return new Builder(
				codi,
				nom,
				retard,
				caducitat,
				entitat,
				pagadorpostal,
				pagadorcie,
				agrupar,
				llibre,
				llibreNom,
				oficina,
				oficinaNom,
				organGestor,
				tipusAssumpte,
				tipusAssumpteNom,
				codiAssumpte,
				codiAssumpteNom);
	}
	
	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi,
				String nom,
				int retard,
				int caducitat,
				EntitatEntity entitat,
				PagadorPostalEntity pagadorpostal,
				PagadorCieEntity pagadorcie,
				boolean agrupar,
				String llibre,
				String llibreNom,
				String oficina,
				String oficinaNom,
				OrganGestorEntity organGestor,
				String tipusAssumpte,
				String tipusAssumpteNom,
				String codiAssumpte,
				String codiAssumpteNom) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.retard = retard;
			built.caducitat = caducitat;
			built.entitat = entitat;
			built.pagadorpostal = pagadorpostal;
			built.pagadorcie = pagadorcie;
			built.agrupar = agrupar;
			built.llibre = llibre;
			built.oficina = oficina;
			built.organGestor = organGestor;
			built.tipusAssumpte = tipusAssumpte;
			built.tipusAssumpteNom = tipusAssumpteNom;
			built.codiAssumpte = codiAssumpte;
			built.codiAssumpteNom = codiAssumpteNom;
		}
		public ProcedimentEntity build() {
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
		ProcedimentEntity other = (ProcedimentEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
