package es.caib.notib.persist.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Classe de model de dades que conté la informació dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("SERVEI")
public class ServeiEntity extends ProcSerEntity {
	
	public static ServeiEntity.ServeiEntityBuilder getBuilder(
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
			boolean requireDirectPermission,
			boolean manual) {
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
				.actiu(true)
				.manual(manual);
	}

	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		var other = (ServeiEntity) obj;
		if (codi == null) {
			return other.codi == null;
		}
		return codi.equals(other.codi);
	}

	@Override
	public String toString() {
		return "ServeiEntity{codi='" + codi + '\'' + ", nom='" + nom + '\'' + '}';
	}

	private static final long serialVersionUID = -5588155683744582417L;

}
