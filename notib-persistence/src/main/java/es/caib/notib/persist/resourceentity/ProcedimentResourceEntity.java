package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades pels recursos de tipus procediment.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "procediment")
@Getter
@Setter
@NoArgsConstructor
public class ProcedimentResourceEntity
	extends BaseAuditableResourceEntity<ProcedimentResource>
	implements AdminEntitatResourceEntity<ProcedimentResource> {

	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 32, nullable = false, updatable = false)
	private ProcSerTipusEnum tipus;
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "retard")
	private Integer retard;
	@Column(name = "caducitat")
	private Integer caducitat;
	@Column(name = "tipusassumpte", length = 255)
	private String tipusAssumpte;
	@Column(name = "tipusassumpte_nom", length = 255)
	private String tipusAssumpteNom;
	@Column(name = "codiassumpte", length = 255)
	private String codiAssumpte;
	@Column(name = "codiassumpte_nom", length = 255)
	private String codiAssumpteNom;
	@Column(name = "agrupar")
	private boolean agrupar;
	@Column(name = "comu")
	private boolean comu;
	@Column(name = "direct_permission_required")
	private boolean requireDirectPermission;
	@Column(name = "manual")
	private boolean manual;
	@Column(name = "organ_no_sinc", nullable = false)
	private boolean organNoSincronitzat;
	@Column(name = "actiu")
	private boolean actiu;
	@Column(name = "ultima_act")
	@Temporal(TemporalType.DATE)
	private Date ultimaActualitzacio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procser_entitat_fk"))
	private EntitatResourceEntity entitat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "organ_gestor",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procser_organ_fk"))
	private OrganGestorResourceEntity organGestor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entrega_cie_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_entrega_cie_fk"))
	private EntregaCieEntity entregaCie;

	@Formula("(select count(*) from " + BaseConfig.DB_PREFIX + "pro_grup pgr where pgr.procediment = id)")
	private Integer grupCount;

	@Builder
	public ProcedimentResourceEntity(
		ProcedimentResource resource,
		EntitatResourceEntity entitat,
		OrganGestorResourceEntity organGestor,
		EntregaCieEntity entregaCie) {
		this.tipus = resource.getTipus();
		this.codi = resource.getCodi();
		this.nom = resource.getNom();
		this.caducitat = resource.getCaducitat();
		this.tipusAssumpte = resource.getTipusAssumpte();
		this.tipusAssumpteNom = resource.getTipusAssumpteNom();
		this.codiAssumpte = resource.getCodiAssumpte();
		this.codiAssumpteNom = resource.getCodiAssumpteNom();
		this.agrupar = resource.isAgrupar();
		this.comu = resource.isComu();
		this.requireDirectPermission = resource.isRequireDirectPermission();
		this.manual = resource.isManual();
		this.organNoSincronitzat = resource.isOrganNoSincronitzat();
		this.actiu = resource.isActiu();
		this.ultimaActualitzacio = resource.getUltimaActualitzacio();
		this.entitat = entitat;
		this.organGestor = organGestor;
		this.entregaCie = entregaCie;
	}

}
