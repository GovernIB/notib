
package es.caib.notib.client.domini;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de numeració de domicili per a un destinatari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EntregaPostalVia implements Serializable {

    ALAMEDA("ALMDA"),
    CALLE("CALLE"),
    CAMINO("CAMNO"),
    CARRER("CARR"),
    CARRETERA("CTRA"),
    GLORIETA("GTA"),
    KALEA("KALEA"),
    PASAJE("PSJ"),
    PASEO("PASEO"),
    PLAÇA("PLAÇA"),
    PLAZA("PLAZA"),
    RAMBLA("RAMBL"),
    RONDA("RONDA"),
    RUA("RÚA"),
    SECTOR("SECT"),
    TRAVESIA("TRAV"),
    URBANIZACION("URB"),
    AVENIDA("AVDA"),
    AVINGUDA("AVGDA"),
    BARRIO("BAR"),
    CALLEJA("CJA"),
    CAMI("CAMÍ"),
    CAMPO("CAMPO"),
    CARRERA("CRA"),
    CUESTA("CSTA"),
    EDIFICIO("EDIF"),
    ENPARANTZA("EPTZA"),
    ESTRADA("ESTR"),
    JARDINES("JARD"),
    JARDINS("JARDI"),
    PARQUE("PRQUE"),
    PASSEIG("PSG"),
    PRAZA("PRAZA"),
    PLAZUELA("PLZA"),
    PLACETA("PLCTA"),
    POBLADO("POBL"),
    VIA("VIA"),
    TRAVESSERA("TRAVS"),
    PASSATGE("PASTG"),
    BULEVAR("BVR"),
    POLIGONO("POLIG"),
    OTROS("OTROS");

    private String val;

    EntregaPostalVia(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
