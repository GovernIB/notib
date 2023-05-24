package es.caib.notib.persist.entity.cie;

import es.caib.notib.persist.audit.NotibAuditable;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "not_pagador_postal")
@EntityListeners(AuditingEntityListener.class)
public class PagadorPostalEntity extends NotibAuditable<Long> {

	@EqualsAndHashCode.Include
	@Column(name = "contracte_num", length = 20)
	private String contracteNum;

	@EqualsAndHashCode.Include
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

	private static final long serialVersionUID = 4863376704844981591L;


}
