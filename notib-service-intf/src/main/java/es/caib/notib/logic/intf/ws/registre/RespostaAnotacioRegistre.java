package es.caib.notib.logic.intf.ws.registre;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class RespostaAnotacioRegistre {

	private Integer numero;
	private String numeroRegistroFormateado;
	private Date data;
	private String hora;

}
