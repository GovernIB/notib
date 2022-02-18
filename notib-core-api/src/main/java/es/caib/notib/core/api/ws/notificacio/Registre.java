package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
public class Registre {

    private Integer numero;
    private Date data;
    private String numeroFormatat;
    private RegistreEstatEnum estat;
    private String oficina;
    private String llibre;

}
