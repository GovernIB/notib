package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Iniciacion.
 * 
 */
@Getter @Setter
public class Iniciacio {
	 
	@JsonProperty("codigo")
	private Long codigo;
	@JsonProperty("descripcion")
	private String descripcion;
	@JsonProperty("nombre")
	private String nombre;
	@JsonProperty("codigoEstandar")
	private String codigoEstandar;
	
}
