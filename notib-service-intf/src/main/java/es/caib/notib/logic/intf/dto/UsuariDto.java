/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
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
