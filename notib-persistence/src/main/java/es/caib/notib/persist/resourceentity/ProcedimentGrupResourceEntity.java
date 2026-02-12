package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.ProcedimentGrupResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

/**
 * Entitat de base de dades pels recursos de tipus relació procediment - grup.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "pro_grup")
@Getter
@Setter
@NoArgsConstructor
public class ProcedimentGrupResourceEntity
	extends BaseAuditableResourceEntity<ProcedimentGrupResource>
	implements AdminEntitatResourceEntity<ProcedimentGrupResource> {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
		name = "procediment",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pro_grup_fk"))
	private ProcedimentResourceEntity procediment;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
		name = "grup",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "grup_pro_fk"))
	private GrupResourceEntity grup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinFormula("(select pro.entitat from " + BaseConfig.DB_PREFIX + "procediment pro where pro.id = procediment)")
	private EntitatResourceEntity entitat;

	@Builder
	public ProcedimentGrupResourceEntity(
		ProcedimentResourceEntity procediment,
		GrupResourceEntity grup) {
		this.procediment = procediment;
		this.grup = grup;
	}

}
