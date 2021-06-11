package es.caib.notib.core.api.dto.organisme;

import lombok.Getter;

public enum OrganGestorEstatEnum {
    VIGENT(1),
    ALTRES(0);

    @Getter
    private final Integer numVal;

    OrganGestorEstatEnum(int numVal) {
        this.numVal = numVal;
    }
}
