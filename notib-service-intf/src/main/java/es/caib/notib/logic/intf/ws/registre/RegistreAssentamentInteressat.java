package es.caib.notib.logic.intf.ws.registre;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa un interessat d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class RegistreAssentamentInteressat {

	private String documentTipus;
	private String documentNum;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String pais;
	private String provincia;
	private String municipi;
	private String adresa;
	private String codiPostal;
	private String email;
	private String telefon;
	private String emailHabilitat;
	private String canalPreferent;
	private String observacions;
	private RegistreAssentamentInteressat representant;

}
