package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_procediment")
public class ProcedimentEntity extends NotibAuditable<Long> {
	
	@Column(name = "codi", length = 64, nullable = false)
	protected String codi;
	
	@Column(name = "nom", length = 100, nullable = false)
	protected String nom;
	
	@Column(name = "codisia", length = 64, nullable = false)
	protected String codisia;
	
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
	
	@Column(name = "agrupar", length = 64, nullable = false)
	protected boolean agrupar;
	
	/*@Column(name = "grup", length = 64, nullable = false)
	protected GrupEntity grup;*/

	public String getCodi() {
		return codi;
	}

	public String getNom() {
		return nom;
	}

	public String getCodisia() {
		return codisia;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public PagadorPostalEntity getPagadorpostal() {
		return pagadorpostal;
	}

	public PagadorCieEntity getPagadorcie() {
		return pagadorcie;
	}

	public boolean isAgrupar() {
		return agrupar;
	}
	
	public void update(
			String codi,
			String nom,
			String codisia,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorcostal,
			PagadorCieEntity pagadorcie,
			boolean agrupar) {
		this.codi = codi;
		this.nom = nom;
		this.codisia = codisia;
		this.entitat = entitat;
		this.pagadorpostal = pagadorcostal;
		this.pagadorcie = pagadorcie;
		this.agrupar = agrupar;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus Entitat.
	 * 
	 * @param codi
	 *            El valor de l'atribut codi.
	 * @param contracteNum
	 *            El valor de l'atribut contracteNum.
	 * @param contracteDataVig
	 *            El valor de l'atribut contracteDataVig.
	 * @param facturacioCodiClient
	 *            El valor de l'atribut facturacioCodiClient.
	 * @return Una nova instància del Builder.
	 */
	
	public static Builder getBuilder(
			String codi,
			String nom,
			String codisia,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorpostal,
			PagadorCieEntity pagadorcie,
			boolean agrupar) {
		return new Builder(
				codi,
				nom,
				codisia,
				entitat,
				pagadorpostal,
				pagadorcie,
				agrupar);
	}
	
	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi,
				String nom,
				String codisia,
				EntitatEntity entitat,
				PagadorPostalEntity pagadorpostal,
				PagadorCieEntity pagadorcie,
				boolean agrupar) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.codisia = codisia;
			built.entitat = entitat;
			built.pagadorpostal = pagadorpostal;
			built.pagadorcie = pagadorcie;
			built.agrupar = agrupar;
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
