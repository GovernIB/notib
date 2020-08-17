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
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Classe de model de dades que conté la informació dels pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "not_pagador_postal")
@EntityListeners(AuditingEntityListener.class)
public class PagadorPostalEntity extends NotibAuditable<Long> {
	
	@EqualsAndHashCode.Include
	@Column(name = "dir3_codi", length = 9)
	private String dir3codi;
	
	@Column(name = "contracte_num", length = 20)
	private String contracteNum;
	
	@Column(name = "contracte_data_vig")
	@Temporal(TemporalType.DATE)
	private Date contracteDataVig;
	
	@Column(name = "facturacio_codi_client", length = 20)
	private String facturacioClientCodi;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_pagador_postal_entitat_fk")
	private EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_gestor")
	@ForeignKey(name = "not_pagpostal_organ_fk")
	protected OrganGestorEntity organGestor;


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
			String facturacioClientCodi,
			EntitatEntity entitat) {
		return new Builder(
				dir3codi,
				contracteNum,
				contracteDataVig,
				facturacioClientCodi,
				entitat,
				null);
	}
	
	public static Builder getBuilder(
			String dir3codi,
			String contracteNum,
			Date contracteDataVig,
			String facturacioClientCodi,
			EntitatEntity entitat,
			OrganGestorEntity organGestor) {
		return new Builder(
				dir3codi,
				contracteNum,
				contracteDataVig,
				facturacioClientCodi,
				entitat,
				organGestor);
	}
	
	public static class Builder {
		PagadorPostalEntity built;
		Builder(
				String dir3codi,
				String contracteNum,
				Date contracteDataVig,
				String facturacioClientCodi,
				EntitatEntity entitat,
				OrganGestorEntity organGestor) {
			built = new PagadorPostalEntity();
			built.dir3codi = dir3codi;
			built.contracteNum = contracteNum;
			built.contracteDataVig = contracteDataVig;
			built.facturacioClientCodi = facturacioClientCodi;
			built.entitat = entitat;
			built.organGestor = organGestor;
		}
		public PagadorPostalEntity build() {
			return built;
		}
	}
	

	private static final long serialVersionUID = 4863376704844981591L;

	
}
