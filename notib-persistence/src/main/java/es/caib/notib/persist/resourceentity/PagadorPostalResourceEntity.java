package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "pagador_postal")
@Getter
@Setter
@NoArgsConstructor
public class PagadorPostalResourceEntity extends BaseAuditableResourceEntity<PagadorPostalResource> {

	@Column(name = "contracte_num", length = 20)
	private String contracteNum;
	@Column(name = "contracte_data_vig")
	private Date contracteDataVig;
	@Column(name = "facturacio_codi_client", length = 20)
	private String facturacioClientCodi;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagador_postal_entitat_fk"))
	private EntitatResourceEntity entitat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pagpostal_organ_fk"))
	private OrganGestorResourceEntity organGestor;

	@Column(name = "nom", length = 256, nullable = false)
	private String nom;

	@Builder
	public PagadorPostalResourceEntity(PagadorPostalResource resource, OrganGestorResourceEntity organGestor, EntitatResourceEntity entitat) {

		this.contracteNum = resource.getContracteNum();
		this.contracteDataVig = resource.getContracteDataVig();
		this.facturacioClientCodi = resource.getFacturacioClientCodi();
		this.nom = resource.getNom();
		this.organGestor = organGestor;
		this.entitat = entitat;
	}

}
