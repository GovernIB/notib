package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació sobre l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificacioCanviClient {

	private String identificador;
	private String referenciaEnviament;
	private Date data;
	
	public NotificacioCanviClient(
			String identificador,
			String referenciaEnviament) {
		super();
		this.identificador = identificador;
		this.referenciaEnviament = referenciaEnviament;
		this.data = new Date();
	}
	
}
