package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorCieFormatFullaResource;
import lombok.*;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

/**
 * Entitat de base de dades pels recursos de tipus format de fulla d'un pagador CIE.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "formats_fulla")
@Getter
@Setter
@NoArgsConstructor
public class PagadorCieFormatFullaResourceEntity
	extends BaseAuditableResourceEntity<PagadorCieFormatFullaResource>
	implements AdminEntitatResourceEntity<PagadorCieFormatFullaResource> {

	@EqualsAndHashCode.Include
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "pagador_cie_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "formats_fulla_fk"))
	private PagadorCieResourceEntity pagadorCie;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinFormula("(select pag.entitat from " + BaseConfig.DB_PREFIX + "pagador_cie pag where pag.id = pagador_cie_id)")
	private EntitatResourceEntity entitat;

	@Builder
	public PagadorCieFormatFullaResourceEntity(
		PagadorCieFormatFullaResource resource,
		PagadorCieResourceEntity pagadorCie) {
		this.codi = resource.getCodi();
		this.pagadorCie = pagadorCie;
	}

}
