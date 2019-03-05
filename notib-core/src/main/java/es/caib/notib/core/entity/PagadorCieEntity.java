package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_pagador_cie")
public class PagadorCieEntity extends NotibAuditable<Long> {

	@Column(name = "dir3_codi", length = 9, nullable = false)
	private String dir3codi;
	
	@Column(name = "contracte_data_vig")
	@Temporal(TemporalType.TIMESTAMP)
	private Date contracteDataVig;

	public String getDir3codi() {
		return dir3codi;
	}

	public Date getContracteDataVig() {
		return contracteDataVig;
	}
	
	public void update(
			String dir3codi,
			Date contracteDataVig) {
		this.dir3codi = dir3codi;
		this.contracteDataVig = contracteDataVig;
	}
	
	public static Builder getBuilder(
			String dir3codi,
			Date contracteDataVig) {
		return new Builder(
				dir3codi,
				contracteDataVig);
	}
	
	public static class Builder {
		PagadorCieEntity built;
		Builder(
				String dir3codi,
				Date contracteDataVig) {
			built = new PagadorCieEntity();
			built.dir3codi = dir3codi;
			built.contracteDataVig = contracteDataVig;
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
