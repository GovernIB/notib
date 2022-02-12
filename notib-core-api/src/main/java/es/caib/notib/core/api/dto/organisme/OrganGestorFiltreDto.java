package es.caib.notib.core.api.dto.organisme;

import es.caib.notib.core.api.dto.AuditoriaDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Locale;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreDto extends AuditoriaDto implements Serializable {
	
	private String codi;
	private String nom;
	private String oficina;
	private OrganGestorEstatEnum estat;
	private boolean entregaCieActiva;

	private static final long serialVersionUID = -2393511650074099319L;

	public boolean filtresOk(OrganGestorDto organ) {

		return organ != null && (codi == null || organ.getCodi() != null &&  organ.getCodi().contains(codi.toUpperCase()))
				&& (nom == null || organ.getNom() != null && organ.getNom().toLowerCase().contains(nom.toLowerCase()))
				&& (estat == null || estat.equals(organ.getEstat())
				&& (organ.isEntregaCieActiva()) == entregaCieActiva)
				&& (oficina == null || oficina.equals(organ.getOficina()));
	}
}
