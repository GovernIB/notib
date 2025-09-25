package es.caib.notib.logic.intf.dto;

import java.util.stream.Stream;

public enum IntegracioCodi {

    USUARIS,
    REGISTRE,
    NOTIFICA,
    ARXIU,
    CALLBACK,
    GESDOC,
    UNITATS,
    GESCONADM,
    PROCEDIMENTS,
    FIRMASERV,
    VALIDASIG,
    CARPETA,
    EMAIL,
    CIE,
    EXPLOTACIO,
    DIGITALITZACIO;

//    private final String descripcio;
//
//    IntegracioCodiEnum(String descripcio) {
//        this.descripcio = descripcio;
//    }
//
//    public String getDescripcio() {
//        return descripcio;
//    }

    public static Stream<IntegracioCodi> stream() {
        return Stream.of(IntegracioCodi.values());
    }

}
