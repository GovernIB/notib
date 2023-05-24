package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistreIdDto {

	private Integer numero;
	private Date data;
	private String descripcioError;
	private String hora;
	private String numeroRegistreFormat;

}
