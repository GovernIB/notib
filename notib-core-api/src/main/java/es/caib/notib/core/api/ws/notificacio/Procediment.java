package es.caib.notib.core.api.ws.notificacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Procediment {

    private String codiSia;
    private String nom;

}
