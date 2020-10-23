package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

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
	private Map<String, String> metadades = new HashMap<String, String>();
	private boolean normalitzat;
	private boolean generarCsv;
	private String uuid;
	private String csv;
	
	private static final long serialVersionUID = 299966599434094856L;

}
