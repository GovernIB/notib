package es.caib.notib.core.api.dto.organisme;

import com.sun.org.apache.xpath.internal.operations.Or;
import es.caib.notib.core.api.dto.Arbre;
import es.caib.notib.core.api.dto.ArbreNode;
import es.caib.notib.core.api.dto.AuditoriaDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreDto extends AuditoriaDto implements Serializable {
	
	private String codi;
	private String codiPare;
	private String nom;
	private String oficina;
	private OrganGestorEstatEnum estat;
	private boolean entregaCie;

	private static final long serialVersionUID = -2393511650074099319L;

	public boolean isEmpty() {
		return (codi == null || codi == "") && (codiPare == null || codiPare == "") && (nom == null || nom == "")
				&& (oficina == null || oficina == "") && estat == null;
	}

	public boolean filtresOk(OrganGestorDto organ) {

		return organ != null && (codi == null || codi.isEmpty() || organ.getCodi() != null &&  organ.getCodi().contains(codi.toUpperCase()))
//				&& (codiPare == null || codiPare.isEmpty() || organ.getCodiPare() != null && organ.getCodiPare().equals(codiPare.toUpperCase()))
				&& (nom == null || nom.isEmpty() || organ.getNom() != null && organ.getNom().toLowerCase().contains(nom.toLowerCase()))
				&& (!entregaCie || organ.getCieId() != null)
				&& (oficina == null || oficina.isEmpty() || organ.getOficina() != null && oficina.equals(organ.getOficina().getCodi()));
	}

	public boolean filtrar(ArbreNode<OrganGestorDto> arrel) {

		if (filtresOk(arrel.getDades())) {
			return true;
		}
		List<ArbreNode<OrganGestorDto>> fills = arrel.getFills();
		List<ArbreNode<OrganGestorDto>> fillsFiltrats = new ArrayList<>();
		boolean ok = false;
		for (ArbreNode<OrganGestorDto> fill : fills) {
			if (filtresOk(fill.getDades())) {
				fillsFiltrats.add(fill);
				ok = true;
				continue;
			}
			boolean o = filtrar(fill);
			if (o) {
				ok = true;
				fillsFiltrats.add(fill);
			}
		}
		if (!fillsFiltrats.isEmpty()) {
			arrel.setFills(fillsFiltrats);
		}
		return ok;
 	}
}
