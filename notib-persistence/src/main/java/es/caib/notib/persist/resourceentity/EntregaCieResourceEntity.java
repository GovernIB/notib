package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.EntregaCieResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades per a les combinacions pagador CIE - pagador postal.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "entrega_cie")
@Getter
@Setter
@NoArgsConstructor
public class EntregaCieResourceEntity extends BaseAuditableResourceEntity<EntregaCieResource> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "cie_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entrega_cie_cie_fk"),
		nullable = false)
	protected PagadorCieResourceEntity pagadorCie;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "operador_postal_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entrega_cie_operador_fk"),
		nullable = false)
	protected PagadorPostalResourceEntity pagadorPostal;

	@Builder
	public EntregaCieResourceEntity(
		EntregaCieResource resource,
		PagadorCieResourceEntity pagadorCie,
		PagadorPostalResourceEntity pagadorPostal) {
		this.pagadorCie = pagadorCie;
		this.pagadorPostal = pagadorPostal;
	}

}
