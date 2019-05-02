package es.caib.notib.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;

/**
 * Classe de model de dades que conté la informació dels pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "not_pagador_postal")
@EntityListeners(AuditingEntityListener.class)
public class PagadorPostalEntity extends NotibAuditable<Long> {
	
	@Column(name = "dir3_codi", length = 9)
	private String dir3codi;
	
	@Column(name = "contracte_num", length = 20)
	private String contracteNum;
	
	@Column(name = "contracte_data_vig")
	@Temporal(TemporalType.DATE)
	private Date contracteDataVig;
	
	@Column(name = "facturacio_codi_client", length = 20)
	private String facturacioClientCodi;

	

	public String getDir3codi() {
		return dir3codi;
	}
	
	public String getContracteNum() {
		return contracteNum;
	}

	public Date getContracteDataVig() {
		return contracteDataVig;
	}

	public String getFacturacioClientCodi() {
		return facturacioClientCodi;
	}
	
	public void update(
			String dir3codi,
			String contracteNum,
			Date contracteDataVig,
			String facturacioClientCodi) {
		this.dir3codi = dir3codi;
		this.contracteNum = contracteNum;
		this.contracteDataVig = contracteDataVig;
		this.facturacioClientCodi = facturacioClientCodi;
	}
	
	public static Builder getBuilder(
			String dir3codi,
			String contracteNum,
			Date contracteDataVig,
			String facturacioClientCodi) {
		return new Builder(
				dir3codi,
				contracteNum,
				contracteDataVig,
				facturacioClientCodi);
	}
	
	public static class Builder {
		PagadorPostalEntity built;
		Builder(
				String dir3codi,
				String contracteNum,
				Date contracteDataVig,
				String facturacioClientCodi) {
			built = new PagadorPostalEntity();
			built.dir3codi = dir3codi;
			built.contracteNum = contracteNum;
			built.contracteDataVig = contracteDataVig;
			built.facturacioClientCodi = facturacioClientCodi;
		}
		public PagadorPostalEntity build() {
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
		PagadorPostalEntity other = (PagadorPostalEntity) obj;
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

	public void setContracteNum(String contracteNum) {
		this.contracteNum = contracteNum;
	}

	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}

	public void setFacturacioClientCodi(String facturacioClientCodi) {
		this.facturacioClientCodi = facturacioClientCodi;
	}


	private static final long serialVersionUID = 4863376704844981591L;

	
}
