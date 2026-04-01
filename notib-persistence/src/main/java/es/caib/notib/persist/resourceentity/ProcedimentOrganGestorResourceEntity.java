package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ProcedimentOrganGestorResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades de relació procediment - òrgan gestor.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "pro_organ")
@Getter
@Setter
@NoArgsConstructor
public class ProcedimentOrganGestorResourceEntity extends BaseAuditableResourceEntity<ProcedimentOrganGestorResource> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "procediment_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_pro_fk"))
	private ProcedimentResourceEntity procediment;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organgestor_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pro_organ_fk"))
	private OrganGestorResourceEntity organGestor;

	@Builder
	public ProcedimentOrganGestorResourceEntity(
		ProcedimentResourceEntity procediment,
		OrganGestorResourceEntity organGestor) {
		this.procediment = procediment;
		this.organGestor = organGestor;
	}

}
