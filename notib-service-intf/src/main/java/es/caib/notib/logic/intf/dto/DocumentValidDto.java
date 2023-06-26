package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentValidDto {

	private String arxiuNom;
	private String mediaType;
	private String arxiuGestdocId;
	private String uuid;
	private String csv;
	private Long mida;

	private boolean normalitzat;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;

	private boolean errorFitxer = false;
	private boolean errorMetadades = false;
	private boolean errorFirma = false;
	private String errorFirmaMsg;
}
