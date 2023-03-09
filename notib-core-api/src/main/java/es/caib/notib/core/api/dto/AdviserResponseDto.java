package es.caib.notib.core.api.dto;

import es.caib.notib.core.api.dto.adviser.Opciones;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdviserResponseDto {
    String identificador;
    String codigoRespuesta;
    String descripcionRespuesta;
    Opciones opcionesResultadoSincronizarEnvio;
}
