
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

    private Persona titular;
    private List<Persona> destinataris;
    private boolean entregaPostalActiva;
    private EntregaPostal entregaPostal;
    private boolean entregaDehActiva;
    private EntregaDeh entregaDeh;
    private NotificaServeiTipusEnumDto serveiTipus;

    public List<Persona> getDestinataris() {
        return destinataris != null ? destinataris : new ArrayList<Persona>();
    }
}