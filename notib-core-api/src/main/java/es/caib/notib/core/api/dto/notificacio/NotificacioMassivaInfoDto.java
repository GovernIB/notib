package es.caib.notib.core.api.dto.notificacio;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NotificacioMassivaInfoDto extends NotificacioMassivaDataDto {
    @Setter
    private List<NotificacioInfo> summary;

    @Getter
    @Builder
    public static class NotificacioInfo {
        private String codiDir3UnidadRemisora;
        private String concepto;
        private String enviamentTipus;
        private String referenciaEmisor;
        private String nombreFichero;
        private String normalizado;
        private String prioridadServicio;
        private String nombre;
        private String apellidos;
        private String cifNif;
        private String email;
        private String codigoDestino;
        private String linea1;
        private String linea2;
        private String codigoPostal;
        private String retardoPostal;
        private String codigoProcedimiento;
        private String fechaEnvioProgramado;
        private String origen;
        private String estadoElaboracion;
        private String tipoDocumental;
        private String pdfFirmado;
        private String errores;
    }
}
