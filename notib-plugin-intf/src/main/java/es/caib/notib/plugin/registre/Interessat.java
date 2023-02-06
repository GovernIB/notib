package es.caib.notib.plugin.registre;

import es.caib.notib.logic.intf.dto.RegistreInteressatDocumentTipusDtoEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'interessat d'una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class Interessat {

	private String entitatCodi;
	private boolean autenticat;
	private Long tipusInteressat;
	private String nif;
	private String nom;
	private String cognom1;
	private String cognom2;
	private String nomAmbCognoms;
	private String paisCodi;
	private String paisNom;
	private String provinciaCodi;
	private String provinciaNom;
	private String municipiCodi;
	private String municipiNom;
	private RegistreInteressatDocumentTipusDtoEnum tipusDocumentIdentificacio;


}
