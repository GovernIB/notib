
package es.caib.notib.logic.intf.dto.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.caib.notib.client.domini.EntregaDeh;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.ServeiTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class Enviament implements Serializable {


    private Long id;
    private EnviamentEstat notificaEstat;

    private Persona titular;
    private List<Persona> destinataris;
    private boolean entregaPostalActiva;
    private EntregaPostal entregaPostal;
    private boolean entregaDehActiva;
    private EntregaDeh entregaDeh;
    private ServeiTipus serveiTipus;
    private boolean perEmail;

    public List<Persona> getDestinataris() {

        if (destinataris == null) {
            destinataris = new ArrayList<>();
        }
        return destinataris;
    }

    public Collection<String> getNifsEnviament() {
        List<String> nifs = new ArrayList<>();
        if (titular != null && !InteressatTipus.FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) && titular.getNif() != null && titular.getNif().trim().length() > 0) {
            nifs.add(titular.getNif());
        }
        if (destinataris != null) {
            for (Persona destinatari: destinataris) {
                if (destinatari != null && (destinatari.getNif() != null && destinatari.getNif().trim().length() > 0)) {
                    nifs.add(destinatari.getNif());
                }
            }
        }
        return nifs;
    }
}