package es.caib.notib.persist.resourceentity;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entitat de base de dades pels recursos de tipus entitat.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(
		name = BaseConfig.DB_PREFIX + "entitat",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "codi"),
				@UniqueConstraint(columnNames = "dir3_codi")
		}
)
@Getter
@Setter
@NoArgsConstructor
public class EntitatResourceEntity extends BaseAuditableResourceEntity<EntitatResource> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "tipus", length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private EntitatTipusEnumDto tipus;
	@Column(name = "dir3_codi", length = 9, nullable = false, unique = true)
	private String dir3Codi;
	@Column(name = "dir3_codi_reg", length = 9)
	private String dir3CodiReg;
	@Column(name = "api_key", length = 64, nullable = false)
	private String apiKey;
	@Column(name = "amb_entrega_deh", nullable = false)
	private boolean ambEntregaDeh;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@Column(name = "color_fons", length = 1024)
	private String colorFons;
	@Column(name = "color_lletra", length = 1024)
	private String colorLletra;
	@Column(name = "tipus_doc_default")
	private TipusDocumentEnumDto tipusDocDefault;
	@Column(name = "nom_oficina_virtual")
	private String nomOficinaVirtual;
	@Column(name = "oficina")
	private String oficina;
	@Column(name = "llibre_entitat")
	private boolean llibreEntitat;
	@Column(name = "llibre")
	protected String llibre;
	@Column(name = "llibre_nom")
	protected String llibreNom;
	@Column(name = "oficina_entitat")
	private boolean oficinaEntitat;
	@Column(name = "data_sincronitzacio")
	LocalDate dataSincronitzacio;
	@Column(name = "data_actualitzacio")
	LocalDate dataActualitzacio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entrega_cie_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_entrega_cie_fk"))
	private EntregaCieEntity entregaCie;


	@Builder
	public EntitatResourceEntity(
			EntitatResource entitatResource,
			EntregaCieEntity entregaCie) {
		this.codi = entitatResource.getCodi();
		this.nom = entitatResource.getNom();
		this.tipus = entitatResource.getTipus();
		this.dir3Codi = entitatResource.getDir3Codi();
		this.dir3CodiReg = entitatResource.getDir3CodiReg();
		this.apiKey = entitatResource.getApiKey();
		this.ambEntregaDeh = entitatResource.isAmbEntregaDeh();
		this.descripcio = entitatResource.getDescripcio();
		this.activa = entitatResource.isActiva();
		this.colorFons = entitatResource.getColorFons();
		this.colorLletra = entitatResource.getColorLletra();
		this.tipusDocDefault = entitatResource.getTipusDocDefault();
		this.nomOficinaVirtual = entitatResource.getNomOficinaVirtual();
		this.oficina = entitatResource.getOficina();
		this.llibreEntitat = entitatResource.isLlibreEntitat();
		this.llibre = entitatResource.getLlibre();
		this.llibreNom = entitatResource.getLlibreNom();
		this.oficinaEntitat = entitatResource.isOficinaEntitat();
		this.entregaCie = entregaCie;
	}

}
