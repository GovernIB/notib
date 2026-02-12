package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entitat de base de dades pels recursos de tipus pagador postal.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "pagador_postal")
@Getter
@Setter
@NoArgsConstructor
public class PagadorPostalResourceEntity
	extends BaseAuditableResourceEntity<PagadorPostalResource>
	implements AdminEntitatResourceEntity<PagadorPostalResource> {

	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "contracte_num", length = 20)
	private String contracteNum;
	@Column(name = "facturacio_codi_client", length = 20)
	private String facturacioClientCodi;
	@Column(name = "contracte_data_vig")
	private Date contracteDataVig;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagador_postal_entitat_fk"))
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagpostal_organ_fk"))
	private OrganGestorResourceEntity organGestor;

	@Builder
	public PagadorPostalResourceEntity(
		PagadorPostalResource resource,
		EntitatResourceEntity entitat,
		OrganGestorResourceEntity organGestor) {
		this.contracteNum = resource.getContracteNum();
		this.contracteDataVig = resource.getContracteDataVig();
		this.facturacioClientCodi = resource.getFacturacioClientCodi();
		this.nom = resource.getNom();
		this.entitat = entitat;
		this.organGestor = organGestor;
	}

}
