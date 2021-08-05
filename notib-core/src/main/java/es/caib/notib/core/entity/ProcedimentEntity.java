package es.caib.notib.core.entity;

import es.caib.notib.core.audit.NotibAuditable;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "not_procediment")
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentEntity extends NotibAuditable<Long> {
	
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	protected String codi;
	
	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	
	@Column(name = "retard")
	protected Integer retard;
	
	@Column(name = "caducitat")
	protected Integer caducitat;
	
	@Column(name = "tipusassumpte", length = 255)
	protected String tipusAssumpte;
	
	@Column(name = "tipusassumpte_nom", length = 255)
	protected String tipusAssumpteNom;
	
	@Column(name = "codiassumpte", length = 255)
	protected String codiAssumpte;
	
	@Column(name = "codiassumpte_nom", length = 255)
	protected String codiAssumpteNom;
	
	@Column(name = "agrupar")
	protected boolean agrupar;
	
	@Column(name = "comu")
	protected boolean comu;

	@Column(name = "DIRECT_PERMISSION_REQUIRED")
	protected boolean requireDirectPermission;

	@Column(name = "ultima_act")
	@Temporal(TemporalType.DATE)
	protected Date ultimaActualitzacio;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "not_entitat_fk")
	protected EntitatEntity entitat;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTREGA_CIE_ID")
	@ForeignKey(name = "NOT_PROCEDIMENT_ENTREGA_CIE_FK")
	private EntregaCieEntity entregaCie;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "organ_gestor", referencedColumnName = "codi")
	@ForeignKey(name = "not_proc_organ_fk")
	protected OrganGestorEntity organGestor;

//	@Formula( "(case "
//			+ "		WHEN entregaCie is not null then 1 "
//			+ "		WHEN organGestor.entregaCie is not null then 1 "
//			+ "		WHEN entitat.entregaCie is not null then 1 "
//			+ "		else 0 "
//			+ " end)")
//	private boolean entregaCieActivaAlgunNivell;

	public boolean isEntregaCieActivaAlgunNivell() {
		if (entregaCie != null) {
			return true;
		}

		if (organGestor != null && organGestor.getEntregaCie() != null) {
			return true;
		}

		if (entitat != null && entitat.getEntregaCie() != null) {
			return true;
		}

		return false;
	}

	public EntregaCieEntity getEntregaCieEfectiva() {
		if (entregaCie != null) {
			return entregaCie;
		}

		if (organGestor != null && organGestor.getEntregaCie() != null) {
			return organGestor.getEntregaCie();
		}

		if (entitat != null && entitat.getEntregaCie() != null) {
			return entitat.getEntregaCie();
		}

		return null;
	}

	public void update(
			String codi,
			String nom,
			EntitatEntity entitat,
			EntregaCieEntity entregaCie,
			int retard,
			int caducitat,
			boolean agrupar,
			OrganGestorEntity organGestor,
			String tipusAssumpte,
			String tipusAssumpteNom,
			String codiAssumpte,
			String codiAssumpteNom,
			boolean comu,
			boolean requireDirectPermission) {
		this.codi = codi;
		this.nom = nom;
		this.entitat = entitat;
		this.entregaCie = entregaCie;
		this.agrupar = agrupar;
		this.organGestor = organGestor;
		this.retard = retard;
		this.caducitat = caducitat;
		this.tipusAssumpte = tipusAssumpte;
		this.tipusAssumpteNom = tipusAssumpteNom;
		this.codiAssumpte = codiAssumpte;
		this.codiAssumpteNom = codiAssumpteNom;
		this.comu=comu;
		this.requireDirectPermission = requireDirectPermission;
	}
	
	public void update(
			String nom,
			OrganGestorEntity organGestor,
			boolean comu) {
		this.nom = nom;
		this.organGestor = organGestor;
		this.comu= comu;
	}
	
	public void updateDataActualitzacio(Date dataActualitzacio) {
		this.ultimaActualitzacio = dataActualitzacio;
	}
	
	public static ProcedimentEntityBuilder getBuilder(
			String codi,
			String nom,
			int retard,
			int caducitat,
			EntitatEntity entitat,
			boolean agrupar,
			OrganGestorEntity organGestor,
			String tipusAssumpte,
			String tipusAssumpteNom,
			String codiAssumpte,
			String codiAssumpteNom,
			boolean comu,
			boolean requireDirectPermission) {
		return builder()
				.codi(codi)
				.nom(nom)
				.retard(retard)
				.caducitat(caducitat)
				.entitat(entitat)
				.agrupar(agrupar)
				.organGestor(organGestor)
				.tipusAssumpte(tipusAssumpte)
				.tipusAssumpteNom(tipusAssumpteNom)
				.codiAssumpte(codiAssumpte)
				.codiAssumpteNom(codiAssumpteNom)
				.comu(comu)
				.requireDirectPermission(requireDirectPermission);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcedimentEntity other = (ProcedimentEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	
	private static final long serialVersionUID = 458331024861203562L;

}
