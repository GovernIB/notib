package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades pels recursos de tipus pagador CIE.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "pagador_cie")
@Getter
@Setter
@NoArgsConstructor
public class PagadorCieResourceEntity
	extends BaseAuditableResourceEntity<PagadorCieResource>
	implements AdminEntitatResourceEntity<PagadorCieResource> {

	@EqualsAndHashCode.Include
	@Column(name = "dir3_codi", length = 9, nullable = false)
	private String organismePagadorCodi; // Organ gestor pagador
	@Column(name = "nom", length = 256)
	private String nom;
	@Column(name = "api_key")
	private String apiKey;
	@Column(name = "cie_extern", nullable = false)
	private boolean cieExtern;
	@Temporal(TemporalType.DATE)
	@Column(name = "contracte_data_vig")
	private Date contracteDataVig;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagador_cie_entitat_fk"))
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagcie_organ_fk"))
	private OrganGestorResourceEntity organGestor; // Organ gestor emisor

	@Builder
	public PagadorCieResourceEntity(
		PagadorCieResource resource,
		OrganGestorResourceEntity organGestor,
		EntitatResourceEntity entitat) {
		this.nom = resource.getNom();
		this.contracteDataVig = resource.getContracteDataVig();
		this.apiKey = resource.getApiKey();
		this.cieExtern = resource.isCieExtern();
		this.organGestor = organGestor;
		this.entitat = entitat;
	}

}
