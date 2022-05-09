/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class UsuariDto implements Serializable {

	@Include
	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlt;
	private String idioma;
	private String[] rols;
	private String ultimRol;
	private Long ultimaEntitat;
	private Boolean rebreEmailsNotificacio;
	private Boolean rebreEmailsNotificacioCreats;

	private static final long serialVersionUID = -139254994389509932L;

}
