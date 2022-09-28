package es.caib.notib.plugin.gesconadm;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Respuesta Procediments
 *
 */

@Getter @Setter
public class RespostaProcediments {

	@JsonProperty("numeroElementos")
    private Integer numeroElementos;
    @JsonProperty("status")
    private String status;
    @JsonProperty("mensaje")
    private String mensaje;
    @JsonProperty("resultado")
    private List<Procediment> resultado = new ArrayList<Procediment>();
    
}