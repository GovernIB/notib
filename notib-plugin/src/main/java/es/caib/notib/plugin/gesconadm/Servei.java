package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Procediments.
 *
 */
@Getter @Setter
public class Servei {

	private long codigo;
	@JsonProperty("codigoServicio")
	private String codigoServicio; // en el modelo se llama codigo
	@JsonProperty("codigoSIA")
	private String codigoSIA;
	@JsonProperty("correo")
	private String correo;
	@JsonProperty("destinatarios")
	private String destinatarios;
	@JsonProperty("estadoSIA")
	private String estadoSIA;
	@JsonProperty("fechaActualizacion")
	private Date fechaActualizacion;
	@JsonProperty("fechaDespublicacion")
	private Date fechaDespublicacion;
	@JsonProperty("fechaPublicacion")
	private Date fechaPublicacion;
	@JsonProperty("fechaSIA")
	private Date fechaSIA;
	@JsonProperty("id")
	private Long id;
	@JsonProperty("nombre")
	private String nombre;
	@JsonProperty("nombreResponsable")
	private String nombreResponsable;
	@JsonProperty("objeto")
	private String objeto;
	@JsonProperty("observaciones")
	private String observaciones;
	@JsonProperty("requisitos")
	private String requisitos;
	@JsonProperty("telefono")
	private String telefono;
	@JsonProperty("urlTramiteExterno")
	private String urlTramiteExterno;
	@JsonProperty("tramiteId")
	private String tramiteId;
	@JsonProperty("tasaUrl")
	private String tasaUrl;
	@JsonProperty("tramiteVersion")
	private String tramiteVersion;
	@JsonProperty("parametros")
	private String parametros;
	@JsonProperty("telematico")
	private boolean telematico;
	@JsonProperty("validacion")
	private Integer validacion;
	@JsonProperty("link_servicioResponsable")
	private Link servicioResponsable;
	@JsonProperty("link_organoInstructor")
	private Link organoInstructor;
	@JsonProperty("link_unidadAdministrativa")
	private Link unidadAdministrativa;
	@JsonProperty("comun")
	private boolean comun;
	@JsonProperty("link_lopdInfoAdicional")
	private Link lopdInfoAdicional;
	@JsonProperty("lopdResponsable")
	private String lopdResponsable;
	@JsonProperty("lopdFinalidad")
	private String lopdFinalidad;
	@JsonProperty("lopdDestinatario")
	private String lopdDestinatario;
	@JsonProperty("lopdDerechos")
	private String lopdDerechos;
	@JsonProperty("lopdCabecera")
	private String lopdCabecera;

}
