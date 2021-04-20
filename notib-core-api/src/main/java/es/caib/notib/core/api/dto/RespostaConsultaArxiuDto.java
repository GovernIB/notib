package es.caib.notib.core.api.dto;

import java.io.Serializable;

import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import lombok.Builder;
import lombok.Data;

/**
 * Resposta de la consulta a arxiu d'un document CSV o UUID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
public class RespostaConsultaArxiuDto implements Serializable {
	
	private Boolean documentExistent;
	private Boolean metadadesExistents;
	
	// Metadades
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;

	private static final long serialVersionUID = 331923829273609128L;

}
