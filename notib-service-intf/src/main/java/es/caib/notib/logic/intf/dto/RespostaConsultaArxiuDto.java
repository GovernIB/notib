package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Resposta de la consulta a arxiu d'un document CSV o UUID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
public class RespostaConsultaArxiuDto implements Serializable {
	
	private Boolean validacioIdCsv;
	private Boolean documentExistent;
	private Boolean metadadesExistents;
	
	// Metadades
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;

	private static final long serialVersionUID = 331923829273609128L;

}
