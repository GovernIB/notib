package es.caib.notib.logic.intf.dto.organisme;

import es.caib.notib.logic.intf.dto.ArbreNode;
import es.caib.notib.logic.intf.dto.AuditoriaDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private boolean isFiltre;

	private static final long serialVersionUID = -2393511650074099319L;

	public boolean isEmpty() {
		return (codi == null || codi == "") /*&& (codiPare == null || codiPare == "")*/ && (nom == null || nom == "")
				&& (oficina == null || oficina == "") && estat == null && !entregaCie;
	}

	public boolean filtresOk(OrganGestorDto organ) {

		boolean ok = organ != null && (codi == null || codi.isEmpty() || organ.getCodi() != null &&  organ.getCodi().contains(codi.toUpperCase()))
				&& (nom == null || nom.isEmpty() || organ.getNom() != null && organ.getNom().toLowerCase().contains(nom.toLowerCase()))
				&& (!entregaCie || organ.getCieId() != null)
				&& (estat == null || estat.equals(organ.getEstat()))
				&& (oficina == null || oficina.isEmpty() || organ.getOficina() != null && oficina.equals(organ.getOficina().getCodi()));
		if (organ != null && entregaCie && organ.isEntregaCieActiva()) {
			ok = ok && true;
		}
		return ok;
	}

	public boolean filtrarOkOrganPare(OrganGestorDto organ) {

		return codiPare != null && codiPare.equals(organ.getCodi());
	}

	public boolean filtrarOrganPare(ArbreNode<OrganGestorDto> arrel) {

		if (filtrarOkOrganPare(arrel.getDades())) {
			return true;
		}
		List<ArbreNode<OrganGestorDto>> fills = arrel.getFills();
		List<ArbreNode<OrganGestorDto>> fillsFiltrats = new ArrayList<>();
		boolean ok = false;
		for (ArbreNode<OrganGestorDto> fill : fills) {
			if (filtrarOkOrganPare(fill.getDades())) {
				fillsFiltrats.add(fill);
				ok = true;
				continue;
			}
			boolean o = filtrarOrganPare(fill);
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
