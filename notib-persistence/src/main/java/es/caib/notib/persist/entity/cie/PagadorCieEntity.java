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
 * Classe de model de dades que conté la informació dels pagadors CIE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "not_pagador_cie")
@EntityListeners(AuditingEntityListener.class)
public class PagadorCieEntity extends NotibAuditable<Long> {

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

	@Column(name = "api_key")
	private String apiKey;

	@Column(name = "salt")
	private String salt;

	@Column(name = "cie_extern", nullable = false)
	private boolean cieExtern;



//	@Column(name = "USUARI", length = 256)
//	private String usuari;
//
//	@Column(name = "PASSWORD", length = 256)
//	private String password;



	private static final long serialVersionUID = 8596990469127710436L;

}
