/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Informació sobre l'entrega a la DEH.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDeh {

	private boolean obligat;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String procedimentCodi;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String emisorNif;

}
