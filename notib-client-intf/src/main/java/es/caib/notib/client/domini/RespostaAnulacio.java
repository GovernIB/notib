package es.caib.notib.client.domini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespostaAnulacio implements Serializable {

    private String identificador;
    private boolean error;
    private String codiResposta;
    private String descripcioResposta;
}

