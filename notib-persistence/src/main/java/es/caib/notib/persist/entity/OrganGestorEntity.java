package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.TipusTransicioEnumDto;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de model de dades que conté la informació dels òrgans gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "not_organ_gestor")
@EntityListeners(AuditingEntityListener.class)
public class OrganGestorEntity extends AbstractPersistable<Long> implements Serializable {
	
	@NaturalId
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	protected String codi;

	@Column(name = "codi_pare", length = 64)
	protected String codiPare;
	
	@Column(name = "nom", length = 1000)
	protected String nom;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_organ_entitat_fk")
	protected EntitatEntity entitat;
	
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

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTREGA_CIE_ID")
	@ForeignKey(name = "NOT_ORGAN_ENTREGA_CIE_FK")
	private EntregaCieEntity entregaCie;

	@Column(name = "sir")
	private Boolean sir;

	@JoinTable(name = "not_og_sinc_rel",
			joinColumns = { @JoinColumn(name = "antic_og", referencedColumnName = "id", nullable = false) },
			inverseJoinColumns = { @JoinColumn(name = "nou_og", referencedColumnName = "id", nullable = false) })
	@ManyToMany(cascade = CascadeType.ALL)
	private List<OrganGestorEntity> nous = new ArrayList<>();

	@ManyToMany(mappedBy = "nous", cascade = CascadeType.ALL)
	private List<OrganGestorEntity> antics = new ArrayList<>();

	@Setter
	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;

//	public void update(
//			String codi,
//			String nom,
//			String llibre,
//			String llibreNom,
//			String oficina,
//			String oficinaNom) {
//		this.codi = codi;
//		this.nom = nom;
//		this.llibre = llibre;
//		this.llibreNom = llibreNom;
//		this.oficina = oficina;
//		this.oficinaNom = oficinaNom;
//	}

//	public void update(String nom) {
//		this.nom = nom;
//	}
//
//	public void updateCodiPare(String codiPare) {
//		this.codiPare = codiPare;
//	}

	public void update(
			String nom,
			String estat,
			String codiPare) {
		this.nom = nom;
		this.estat = getEstat(estat);
		this.codiPare = codiPare;
	}

	public void updateLlibre(
			String llibre,
			String llibreNom) {
		this.llibre = llibre;
		this.llibreNom = llibreNom;
	}
	
	public void updateOficina(
			String oficina,
			String oficinaNom) {
		this.oficina = oficina;
		this.oficinaNom = oficinaNom;
	}
	public void updateEntregaCie(EntregaCieEntity entregaCie) {
		this.entregaCie = entregaCie;
	}

	public static OrganGestorEstatEnum getEstat(String estat) {
		switch (estat) {
			case "E": return OrganGestorEstatEnum.E;
			case "A": return OrganGestorEstatEnum.A;
			case "T": return OrganGestorEstatEnum.T;
			case "V":
			default:
				return OrganGestorEstatEnum.V;
		}
	}

//	public void updateEstat(OrganGestorEstatEnum estat) {
//		this.estat = estat;
//	}
//	public static OrganGestorEntityBuilder builder(
//			String codi,
//			String nom,
//			String codiPare,
//			EntitatEntity entitat,
//			String llibre,
//			String llibreNom,
//			String oficina,
//			String oficinaNom,
//			OrganGestorEstatEnum estat,
//			Boolean sir) {
//		return new OrganGestorEntityBuilder()
//				.entitat(entitat)
//				.codi(codi)
//				.nom(nom)
//				.llibre(llibre)
//				.llibreNom(llibreNom)
//				.oficina(oficina)
//				.oficinaNom(oficinaNom)
//				.estat(estat)
//				.codiPare(codiPare)
//				.sir(sir);
//	}

	public void addNou(OrganGestorEntity nou) {
		if (nous == null) {
			nous = new ArrayList<>();
		}
		nous.add(nou);
	}
	public void addAntic(OrganGestorEntity antic) {
		if (antics == null) {
			antics = new ArrayList<>();
		}
		antics.add(antic);
	}

	@Override
	public String toString() {
		return "OrganGestorEntity{" +
				"codi='" + codi + '\'' +
				", nom='" + nom + '\'' +
				", llibre='" + llibre + '\'' +
				", llibreNom='" + llibreNom + '\'' +
				", oficina='" + oficina + '\'' +
				", oficinaNom='" + oficinaNom + '\'' +
				", estat=" + estat +
				'}';
	}

	public static class OrganGestorEntityBuilder {
		public OrganGestorEntityBuilder estat(String estat) {
			this.estat = getEstat(estat);
			return this;
		}
	}

	private static final long serialVersionUID = 458331024861203562L;

}
