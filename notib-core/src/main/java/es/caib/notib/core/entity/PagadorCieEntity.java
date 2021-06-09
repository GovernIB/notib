package es.caib.notib.core.entity;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "not_pagador_cie")
@EntityListeners(AuditingEntityListener.class)
public class PagadorCieEntity extends NotibAuditable<Long> {

	@EqualsAndHashCode.Include
	@Column(name = "dir3_codi", length = 9)
	private String dir3codi;
	
	@Column(name = "contracte_data_vig")
	@Temporal(TemporalType.DATE)
	private Date contracteDataVig;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_pagador_cie_entitat_fk")
	private EntitatEntity entitat;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "organ_gestor")
	@ForeignKey(name = "not_pagcie_organ_fk")
	protected OrganGestorEntity organGestor;

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
				entitat,
				null);
	}
	
	public static Builder getBuilder(
			String dir3codi,
			Date contracteDataVig,
			EntitatEntity entitat,
			OrganGestorEntity organGestor) {
		return new Builder(
				dir3codi,
				contracteDataVig,
				entitat,
				organGestor);
	}
	
	public static class Builder {
		PagadorCieEntity built;
		Builder(
				String dir3codi,
				Date contracteDataVig,
				EntitatEntity entitat,
				OrganGestorEntity organGestor) {
			built = new PagadorCieEntity();
			built.dir3codi = dir3codi;
			built.contracteDataVig = contracteDataVig;
			built.entitat = entitat;
			built.organGestor = organGestor;
		}
		public PagadorCieEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = 8596990469127710436L;
	
}
