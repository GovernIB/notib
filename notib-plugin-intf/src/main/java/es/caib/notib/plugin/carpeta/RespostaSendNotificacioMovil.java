package es.caib.notib.plugin.carpeta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespostaSendNotificacioMovil {

    private String code;
    private String message;
}
