/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.util.TrimStringDeserializer;
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
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String nom;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String llinatge1;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String llinatge2;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String nif;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String telefon;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String email;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String raoSocial;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String dir3Codi;
}
