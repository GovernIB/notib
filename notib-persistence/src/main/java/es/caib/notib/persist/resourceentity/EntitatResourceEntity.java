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
import org.hibernate.annotations.Formula;

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

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "logo_cap2")
	private byte[] logoCapsalera;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entrega_cie_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_entrega_cie_fk"))
	private EntregaCieEntity entregaCie;

	@Formula("(select count(*) from " + BaseConfig.DB_PREFIX + "aplicacio apl where apl.entitat_id = id)")
	private Integer aplicacioCount;

	@Builder
	public EntitatResourceEntity(
			EntitatResource resource,
			EntregaCieEntity entregaCie) {
		this.codi = resource.getCodi();
		this.nom = resource.getNom();
		this.tipus = resource.getTipus();
		this.dir3Codi = resource.getDir3Codi();
		this.dir3CodiReg = resource.getDir3CodiReg();
		this.apiKey = resource.getApiKey();
		this.ambEntregaDeh = resource.isAmbEntregaDeh();
		this.descripcio = resource.getDescripcio();
		this.activa = resource.isActiva();
		this.colorFons = resource.getColorFons();
		this.colorLletra = resource.getColorLletra();
		this.tipusDocDefault = resource.getTipusDocDefault();
		this.nomOficinaVirtual = resource.getNomOficinaVirtual();
		this.oficina = resource.getOficina();
		this.llibreEntitat = resource.isLlibreEntitat();
		this.llibre = resource.getLlibre();
		this.llibreNom = resource.getLlibreNom();
		this.oficinaEntitat = resource.isOficinaEntitat();
		this.entregaCie = entregaCie;
	}

}
