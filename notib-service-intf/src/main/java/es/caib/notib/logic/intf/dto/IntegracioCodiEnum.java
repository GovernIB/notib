package es.caib.notib.logic.intf.dto;

import java.util.stream.Stream;

public enum IntegracioCodiEnum {

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
    EMAIL;

//    private final String descripcio;
//
//    IntegracioCodiEnum(String descripcio) {
//        this.descripcio = descripcio;
//    }
//
//    public String getDescripcio() {
//        return descripcio;
//    }

    public static Stream<IntegracioCodiEnum> stream() {
        return Stream.of(IntegracioCodiEnum.values());
    }

}
