package es.caib.notib.logic.intf.dto.adviser.sir;

import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SirAdviser {

    private String registreNumero;
    private Date registreData;
    private Date sirRecepcioData;
    private Date sirRegistreDestiData;
    private String oficinaCodi;
    private String oficinaDenominacio;
    private String entitatCodi;
    private String entitatDenominacio;
    private String registreNumeroFormatat;
    private String codiLlibre;
    private NotificacioRegistreEstatEnumDto estat;
    private String numeroRegistroDestino;
    private String motivo;
    private String codigoEntidadRegistralProcesado; // Codigo de la oficina que acepta o rechaza, reenvia
    private String decodificacionEntidadRegistralProcesado; // Denominacion de la oficina que acepta o rechaza, reenvia
}
