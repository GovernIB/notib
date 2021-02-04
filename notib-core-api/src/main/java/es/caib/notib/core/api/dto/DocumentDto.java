package es.caib.notib.core.api.dto;

import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DocumentDto implements Serializable {

	private String id;
	private String arxiuGestdocId;
	private String arxiuNom;
	private String mediaType;
	private Long mida;
	private String contingutBase64;
	private String hash;
	private String url;
//	private Map<String, String> metadades = new HashMap<String, String>();
	private boolean normalitzat;
	private boolean generarCsv;
	private String uuid;
	private String csv;
	private OrigenEnum origen;
	private ValidesaEnum validesa;
	private TipusDocumentalEnum tipoDocumental;
	private Boolean modoFirma;
	
	private static final long serialVersionUID = 299966599434094856L;

}
