package es.caib.notib.plugin.gesconadm;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Procediments.
 *
 */
@Getter @Setter
public class Procediment {
	
	@JsonProperty("codigo")
    private String codigo;
	@JsonProperty("destinatarios")
	private String destinatarios;
	@JsonProperty("fechaActualizacion")
	private Date fechaActualizacion;
	@JsonProperty("fechaCaducidad")
	private Date fechaCaducidad;
	@JsonProperty("fechaPublicacion")
	private Date fechaPublicacion;
	@JsonProperty("indicador")
	private Boolean indicador;
	@JsonProperty("dirElectronica")
	private String dirElectronica;
	@JsonProperty("lugar")
	private String lugar;
	@JsonProperty("nombre")
	private String nombre;
	@JsonProperty("notificacion")
	private String notificacion;
	@JsonProperty("observaciones")
	private String observaciones;
	@JsonProperty("plazos")
	private String plazos;
	@JsonProperty("recursos")
	private String recursos;
	@JsonProperty("requisitos")
	private String requisitos;
	@JsonProperty("resolucion")
	private String resolucion;
	@JsonProperty("responsable")
	private String responsable;
	@JsonProperty("resumen")
	private String resumen;
	@JsonProperty("signatura")
	private String signatura;
	@JsonProperty("taxa")
	private Boolean taxa;
	@JsonProperty("url")
	private String url;
	@JsonProperty("validacion")
	private Integer validacion;
	@JsonProperty("codigoSIA")
	private String codigoSIA;
	@JsonProperty("estadoSIA")
	private String estadoSIA;
	@JsonProperty("fechaSIA")
	private Date fechaSIA;
	@JsonProperty("tramite")
	private String tramite;
	@JsonProperty("version")
	private Long version;
	@JsonProperty("link_servicioResponsable")
	private Link servicioResponsable;
	@JsonProperty("link_unidadAdministrativa")
	private Link unidadAdministrativa;
	@JsonProperty("link_organResolutori")
	private Link organResolutori;
	@JsonProperty("link_familia")
	private Link familia;
	@JsonProperty("silencio")
	private Silenci silencio;
	@JsonProperty("iniciacion")
	private Iniciacio iniciacion;
    @JsonProperty("comun")
    private Boolean comun;

}
