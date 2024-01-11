package es.caib.notib.logic.intf.dto.notificacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class NotificacioMassivaInfoDto extends NotificacioMassivaDataDto {
    @Setter
    private List<NotificacioInfo> summary;

    @Getter
    @Builder
    public static class NotificacioInfo {

        private String codiDir3UnidadRemisora;
        private String concepto;
        private String descripcio;
        private String enviamentTipus;
        private String referenciaEmisor;
        private String nombreFichero;
        private String uuidFichero;
        private String csvFichero;
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
        private String errorsExecucio;
        private boolean cancelada;
        private String estat;
    }
}
