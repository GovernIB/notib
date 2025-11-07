package es.caib.notib.logic.intf.dto;

import lombok.Getter;

public enum AvisDescripcio {

    ENVIAMENT_NOTIFICA("Enviament a Notifica."),
    ACTUALTIZAR_ESTAT_NOTIFICA("Actualitzar estat Notifica."),
    ACTUALITZAR_ESTAT_REGISTRE("Actualitzar estat Registre."),
    ACTUALITZAR_ESTAT_ENTREGA_POSTAL("Actualitzar estat entrega postal.");

    @Getter
    private String descripcio;

    AvisDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
