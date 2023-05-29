
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * Informació d'un enviament d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Enviament {

//    private Long id;
//    @JsonDeserialize(using = TrimStringDeserializer.class)
//    private String referencia;
    private Persona titular;
    private List<Persona> destinataris;
    private boolean entregaPostalActiva;
    private EntregaPostal entregaPostal;
    private boolean entregaDehActiva;
    private EntregaDeh entregaDeh;
    private NotificaServeiTipusEnumDto serveiTipus;
//    private boolean perEmail;

    public List<Persona> getDestinataris() {
        if (destinataris == null) {
            destinataris = new ArrayList<>();
        }
        return this.destinataris;
    }
}