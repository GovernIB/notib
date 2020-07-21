package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Silencios.
 * 
 */
@Getter @Setter
public class Tratament {
	 
	@JsonProperty("codigo")
	private Long codigo;
	@JsonProperty("cargoF")
	private String cargoF;
	@JsonProperty("cargoM")
	private String cargoM;
	@JsonProperty("codigoEstandar")
	private String codigoEstandar;
	@JsonProperty("tipo")
	private String tipo;
	@JsonProperty("tratamientoF")
	private String tratamientoF;
	@JsonProperty("tratamientoM")
	private String tratamientoM;
	
}
