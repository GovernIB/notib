/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
public class Persona {

	private Long id;
	private boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String telefon;
	private String email;
	private String raoSocial;
	private String dir3Codi;
}
