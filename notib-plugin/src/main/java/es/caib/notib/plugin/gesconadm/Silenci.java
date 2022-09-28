package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Silencios.
 * 
 */
@Getter @Setter
public class Silenci {
	 
	@JsonProperty("codigo")
	private Long codigo;
	@JsonProperty("descripcion")
	private String descripcion;
	@JsonProperty("nombre")
	private String nombre;
	
}
