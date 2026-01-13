package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.logic.intf.model.AvisResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades pels recursos de tipus avís.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "avis")
@Getter
@Setter
@NoArgsConstructor
public class AvisResourceEntity extends BaseAuditableResourceEntity<AvisResource> {

	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final", nullable = false)
	private Date dataFinal;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	@Column(name = "avis_admin", nullable = false)
	private Boolean avisAdministrador;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "avis_entitat_fk"),
			nullable = false)
	private EntitatResourceEntity entitat;

	@Builder
	public AvisResourceEntity(
			AvisResource resource,
			EntitatResourceEntity entitat) {
		this.assumpte = resource.getAssumpte();
		this.missatge = resource.getMissatge();
		this.dataInici = resource.getDataInici();
		this.dataFinal = resource.getDataFinal();
		this.actiu = resource.isActiu();
		this.avisNivell = resource.getAvisNivell();
		this.avisAdministrador = resource.isAvisAdministrador();
		this.entitat = entitat;
	}

}
