package es.caib.notib.core.entity.cie;

import es.caib.notib.core.audit.NotibAuditable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import lombok.*;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "not_pagador_cie")
@EntityListeners(AuditingEntityListener.class)
public class PagadorCieEntity extends NotibAuditable<Long> {

	@EqualsAndHashCode.Include
	@Column(name = "dir3_codi", length = 9)
	private String organismePagadorCodi;

	@Column(name = "NOM", length = 256)
	private String nom;

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
			String organismePagador,
			Date contracteDataVig) {
		this.organismePagadorCodi = organismePagador;
		this.contracteDataVig = contracteDataVig;
	}
	
	public static PagadorCieEntityBuilder builder(
			String organismePagadorCodi,
			String nom,
			Date contracteDataVig,
			EntitatEntity entitat) {
		return hiddenBuilder()
				.organismePagadorCodi(organismePagadorCodi)
				.nom(nom)
				.contracteDataVig(contracteDataVig)
				.entitat(entitat);
	}

	private static final long serialVersionUID = 8596990469127710436L;
	
}
