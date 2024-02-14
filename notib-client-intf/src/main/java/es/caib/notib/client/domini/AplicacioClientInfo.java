package es.caib.notib.client.domini;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AplicacioClientInfo {

    private String usuariCodi;
    private AplicacioClientTipus tipus;
    private AplicacioClientVersio versio;
}
