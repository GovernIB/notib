package es.caib.notib.logic.intf.dto;

import lombok.Getter;

@Getter
public enum SiNo {

    SI(1),
    NO(0);

    private int valor;

    SiNo(int valor) {
        this.valor = valor;
    }
}
