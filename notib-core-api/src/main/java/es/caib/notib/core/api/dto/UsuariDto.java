/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String idioma;
	private String[] rols;
	private Boolean rebreEmailsNotificacio;

	private static final long serialVersionUID = -139254994389509932L;

}
