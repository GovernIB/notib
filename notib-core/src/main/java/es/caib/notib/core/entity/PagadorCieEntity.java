package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.Getter;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "not_pagador_cie")
@EntityListeners(AuditingEntityListener.class)
public class PagadorCieEntity extends NotibAuditable<Long> {

	@Column(name = "dir3_codi", length = 9)
	private String dir3codi;
	
	@Column(name = "contracte_data_vig")
	@Temporal(TemporalType.DATE)
	private Date contracteDataVig;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_pagador_cie_entitat_fk")
	private EntitatEntity entitat;

	public void update(
			String dir3codi,
			Date contracteDataVig) {
		this.dir3codi = dir3codi;
		this.contracteDataVig = contracteDataVig;
	}
	
	public static Builder getBuilder(
			String dir3codi,
			Date contracteDataVig,
			EntitatEntity entitat) {
		return new Builder(
				dir3codi,
				contracteDataVig,
				entitat);
	}
	
	public static class Builder {
		PagadorCieEntity built;
		Builder(
				String dir3codi,
				Date contracteDataVig,
				EntitatEntity entitat) {
			built = new PagadorCieEntity();
			built.dir3codi = dir3codi;
			built.contracteDataVig = contracteDataVig;
			built.entitat = entitat;
		}
		public PagadorCieEntity build() {
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
		PagadorCieEntity other = (PagadorCieEntity) obj;
		if (dir3codi == null) {
			if (other.dir3codi != null)
				return false;
		} else if (!dir3codi.equals(other.dir3codi))
			return false;
		return true;
	}
	
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}

	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}

	private static final long serialVersionUID = 8596990469127710436L;
	
}
