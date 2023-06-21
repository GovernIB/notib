package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta Procediments
 *
 */

@Getter @Setter
public class RespostaServeis {

	@JsonProperty("numeroElementos")
    private Integer numeroElementos;
    @JsonProperty("status")
    private String status;
    @JsonProperty("mensaje")
    private String mensaje;
    @JsonProperty("resultado")
    private List<Servei> resultado = new ArrayList<>();
    
}