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
public class Datat {

    private EnviamentEstatEnum estat;
    private Date data;
    private String origen;
    private String receptorNif;
    private String receptorNom;
    private String errorDescripcio;
    private String numSeguiment;

}
