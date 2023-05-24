package es.caib.notib.persist.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("PROCEDIMENT")
public class ProcedimentEntity extends ProcSerEntity {
	
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
				.requireDirectPermission(requireDirectPermission)
				.actiu(true);
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

	@Override
	public String toString() {
		return "ProcedimentEntity{" +
				"codi='" + codi + '\'' +
				", nom='" + nom + '\'' +
				'}';
	}

	private static final long serialVersionUID = 458331024861203562L;

}
