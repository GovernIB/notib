package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ForeignKey;
import es.caib.notib.core.api.dto.TipusAssumpteEnumDto;
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
	
	@Column(name = "data_programada")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date enviamentDataProgramada;
	
	@Column(name = "retard")
	protected Integer retard;
	
	@Column(name = "llibre")
	protected String llibre;
	
	@Column(name = "oficina")
	protected String oficina;
	
	@Column(name = "tipusassumpte")
	protected TipusAssumpteEnumDto tipusAssumpte;
	
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
	
	
	
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setCodisia(String codisia) {
		this.codisia = codisia;
	}

	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}

	public void setRetard(Integer retard) {
		this.retard = retard;
	}

	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public void setTipusAssumpte(TipusAssumpteEnumDto tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}

	public void setEntitat(EntitatEntity entitat) {
		this.entitat = entitat;
	}

	public void setPagadorpostal(PagadorPostalEntity pagadorpostal) {
		this.pagadorpostal = pagadorpostal;
	}

	public void setPagadorcie(PagadorCieEntity pagadorcie) {
		this.pagadorcie = pagadorcie;
	}

	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}

	@Column(name = "agrupar", length = 64, nullable = false)
	protected boolean agrupar;

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

	public Integer getRetard() {
		return retard;
	}

	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}

	public String getLlibre() {
		return llibre;
	}

	public String getOficina() {
		return oficina;
	}

	public TipusAssumpteEnumDto getTipusAssumpte() {
		return tipusAssumpte;
	}

	public void update(
			String codi,
			String nom,
			String codisia,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorcostal,
			PagadorCieEntity pagadorcie,
			int retard,
			boolean agrupar,
			String llibre,
			String oficina,
			TipusAssumpteEnumDto tipusAssumpte) {
		this.codi = codi;
		this.nom = nom;
		this.codisia = codisia;
		this.entitat = entitat;
		this.pagadorpostal = pagadorcostal;
		this.pagadorcie = pagadorcie;
		this.agrupar = agrupar;
		this.llibre = llibre;
		this.oficina = oficina;
		this.retard = retard;
		this.tipusAssumpte = tipusAssumpte;
	}
	
	public static Builder getBuilder(
			String codi,
			String nom,
			String codisia,
			int retard,
			EntitatEntity entitat,
			PagadorPostalEntity pagadorpostal,
			PagadorCieEntity pagadorcie,
			boolean agrupar,
			String llibre,
			String oficina,
			TipusAssumpteEnumDto tipusAssumpte) {
		return new Builder(
				codi,
				nom,
				codisia,
				retard,
				entitat,
				pagadorpostal,
				pagadorcie,
				agrupar,
				llibre,
				oficina,
				tipusAssumpte);
	}
	
	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi,
				String nom,
				String codisia,
				int retard,
				EntitatEntity entitat,
				PagadorPostalEntity pagadorpostal,
				PagadorCieEntity pagadorcie,
				boolean agrupar,
				String llibre,
				String oficina,
				TipusAssumpteEnumDto tipusAssumpte) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.codisia = codisia;
			built.retard = retard;
			built.entitat = entitat;
			built.pagadorpostal = pagadorpostal;
			built.pagadorcie = pagadorcie;
			built.agrupar = agrupar;
			built.llibre = llibre;
			built.oficina = oficina;
			built.tipusAssumpte = tipusAssumpte;
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
