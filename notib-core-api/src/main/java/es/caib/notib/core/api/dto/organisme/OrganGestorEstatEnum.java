package es.caib.notib.core.api.dto.organisme;

import lombok.Getter;

public enum OrganGestorEstatEnum {
    ALTRES(0),
	VIGENT(1);

    @Getter
    private final Integer numVal;

    OrganGestorEstatEnum(int numVal) {
        this.numVal = numVal;
    }
}
