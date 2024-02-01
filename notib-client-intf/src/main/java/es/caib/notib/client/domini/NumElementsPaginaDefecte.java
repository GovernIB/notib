package es.caib.notib.client.domini;

import lombok.Getter;

@Getter
public enum NumElementsPaginaDefecte {

    DEU(10),
    VINT(20),
    CINQUANTA(50),
    CENT(100),
    DOSCENTSCINQUANTA(250);

    private int elements;
    NumElementsPaginaDefecte(int elements) {
        this.elements = elements;
    }
}
