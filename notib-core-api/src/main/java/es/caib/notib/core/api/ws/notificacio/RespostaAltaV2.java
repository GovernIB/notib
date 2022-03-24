/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@XmlRootElement
public class RespostaAltaV2 extends RespostaBase {
	private String identificador;
	private NotificacioEstatEnum estat;
	private List<EnviamentReferenciaV2> referencies;
	private Date dataCreacio;

	public List<EnviamentReferencia> getReferenciesAsV1() {
		List<EnviamentReferencia> v1 = new ArrayList<>();
		if (referencies == null || referencies.isEmpty()) {
			return v1;
		}
		for (EnviamentReferenciaV2 ref : referencies) {
			v1.add(EnviamentReferencia.builder().referencia(ref.getReferencia()).titularNif(ref.getTitularNif()).build());
		}
		return v1;
	}
}
