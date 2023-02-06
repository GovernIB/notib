package es.caib.notib.plugin.registre;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipusDocumentRegistre {

	private String nom;
	private String codi;

	public TipusDocumentRegistre(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}
}
