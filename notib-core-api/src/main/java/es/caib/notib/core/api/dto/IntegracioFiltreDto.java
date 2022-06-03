package es.caib.notib.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IntegracioFiltreDto implements Serializable {

    private String entitatCodi;
    private String aplicacio;


    public boolean filtresOK(IntegracioAccioDto accio, String integracioCodi) {

        return ((entitatCodi == null || entitatCodi == "")
                || ((entitatCodi != null || entitatCodi != "") && accio.getCodiEntitat() != null && accio.getCodiEntitat() != ""
                   && accio.getCodiEntitat().toLowerCase().contains(entitatCodi.toLowerCase())))
                && ((!"CALLBACK".equals(integracioCodi) || aplicacio != null || aplicacio != "")
                    || ((aplicacio != null || aplicacio != "") && accio.getAplicacio() != null && accio.getAplicacio() != ""
                        && accio.getAplicacio().toLowerCase().contains( aplicacio.toLowerCase())));
    }
}
