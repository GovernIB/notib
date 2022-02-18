package es.caib.notib.core.entity;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import lombok.*;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
public class OrganGestorEntity extends AbstractPersistable<Long> {
	
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

	@Column(name = "estat")
	@Enumerated(EnumType.ORDINAL)
	protected OrganGestorEstatEnum estat;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTREGA_CIE_ID")
	@ForeignKey(name = "NOT_ORGAN_ENTREGA_CIE_FK")
	private EntregaCieEntity entregaCie;

	public void update(
			String codi,
			String nom,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom) {
		this.codi = codi;
		this.nom = nom;
		this.llibre = llibre;
		this.llibreNom = llibreNom;
		this.oficina = oficina;
		this.oficinaNom = oficinaNom;
	}

	public void update(String nom) {
		this.nom = nom;
	}

	public void updateCodiPare(String codiPare) {
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

	public void updateEstat(OrganGestorEstatEnum estat) {
		this.estat = estat;
	}
	public static OrganGestorEntityBuilder builder(
			String codi,
			String nom,
			String codiPare,
			EntitatEntity entitat,
			String llibre,
			String llibreNom,
			String oficina,
			String oficinaNom,
			OrganGestorEstatEnum estat) {
		return new OrganGestorEntityBuilder()
				.entitat(entitat)
				.codi(codi)
				.nom(nom)
				.llibre(llibre)
				.llibreNom(llibreNom)
				.oficina(oficina)
				.oficinaNom(oficinaNom)
				.estat(estat)
				.codiPare(codiPare);
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

	private static final long serialVersionUID = 458331024861203562L;

}
