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
 * Classe de model de dades que conté la informació dels pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "not_pagador_postal")
@EntityListeners(AuditingEntityListener.class)
public class PagadorPostalEntity extends NotibAuditable<Long> {
	
	@EqualsAndHashCode.Include
	@Column(name = "dir3_codi", length = 9)
	private String organismePagadorCodi;

	@Column(name = "contracte_num", length = 20)
	private String contracteNum;

	@Column(name = "NOM", length = 256)
	private String nom;

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

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "dir3_codi", referencedColumnName = "codi", insertable = false, updatable = false)
//	@ForeignKey(name = "not_not_organ_fk") // TODO: Definir FK
	private OrganGestorEntity organismePagador;

	public void update(
			String organismePagadorCodi,
			String contracteNum,
			Date contracteDataVig,
			String facturacioClientCodi) {
		this.organismePagadorCodi = organismePagadorCodi;
		this.contracteNum = contracteNum;
		this.contracteDataVig = contracteDataVig;
		this.facturacioClientCodi = facturacioClientCodi;
	}

	public static PagadorPostalEntityBuilder builder(
			String organismePagadorCodi,
			String nom,
			String contracteNum,
			Date contracteDataVig,
			String facturacioClientCodi,
			EntitatEntity entitat) {
		return hiddenBuilder()
				.organismePagadorCodi(organismePagadorCodi)
				.nom(nom)
				.contracteNum(contracteNum)
				.contracteDataVig(contracteDataVig)
				.facturacioClientCodi(facturacioClientCodi)
				.entitat(entitat);
	}

	private static final long serialVersionUID = 4863376704844981591L;

	
}
