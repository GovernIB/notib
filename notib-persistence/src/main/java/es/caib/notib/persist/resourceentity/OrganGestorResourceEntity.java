package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.TipusTransicioEnumDto;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat de base de dades pels recursos de tipus òrgan gestor.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "organ_gestor")
@Getter
@Setter
@NoArgsConstructor
public class OrganGestorResourceEntity extends BaseAuditableResourceEntity<OrganGestorResource> {

	@Column(name = "codi", length = 64, nullable = false)
	protected String codi;
	@Column(name = "codi_pare", length = 64)
	protected String codiPare;
	@Column(name = "nom", length = 1000, nullable = false)
	protected String nom;
	@Column(name = "nom_es", length = 1000)
	protected String nomEs;
	@Column(name = "llibre")
	protected String llibre;
	@Column(name = "llibre_nom")
	protected String llibreNom;
	@Column(name = "oficina")
	protected String oficina;
	@Column(name = "oficina_nom")
	protected String oficinaNom;
	@Column(name = "estat", length = 1)
	@Enumerated(EnumType.STRING)
	protected OrganGestorEstatEnum estat;
	@Column(name = "sir")
	private Boolean sir;
	@Column(name = "permetre_sir")
	private boolean permetreSir;
	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;
	@Column(name = "no_vigent")
	private Boolean noVigent;
	@Column(name = "entrega_cie_desactivada")
	private boolean entregaCieDesactivada;
	@Column(name = "sobrescriure_cie_organ_emisor")
	private boolean sobrescriureCieOrganEmisor;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entitat",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "not_organ_entitat_fk"),
		nullable = false)
	protected EntitatResourceEntity entitat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "entrega_cie_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_entrega_cie_fk"))
	private EntregaCieEntity entregaCie;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "not_og_sinc_rel",
		joinColumns = {@JoinColumn(name = "antic_og", referencedColumnName = "id", nullable = false)},
		inverseJoinColumns = {@JoinColumn(name = "nou_og", referencedColumnName = "id", nullable = false)})
	private List<OrganGestorEntity> nous = new ArrayList<>();

	@ManyToMany(mappedBy = "nous")
	private List<OrganGestorEntity> antics = new ArrayList<>();

}
