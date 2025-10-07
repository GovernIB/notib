package es.caib.notib.logic.intf.dto.escaneig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalitzacioTransaccioResposta {

    private String idTransaccio;
    private String urlRedireccio;
    private boolean returnScannedFile;
    private boolean returnSignedFile;
}
