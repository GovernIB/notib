package es.caib.notib.plugin.registre;

import java.util.Date;

import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Resposta a una consulta de registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaConsultaRegistre extends RespostaBase {

	private String registreNumero;
	private Date registreData;
	private Date sirRecepecioData;
	private Date sirRegistreDestiData;
	private String oficinaCodi;
	private String oficinaDenominacio;
	private String entitatCodi;
	private String entitatDenominacio;
	private String registreNumeroFormatat;
	private String codiLlibre;
	private NotificacioRegistreEstatEnumDto estat;
	private String codiError;
	private String descripcioError;
	
}
