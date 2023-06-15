package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IntegracioFiltreDto implements Serializable {

    private String entitatCodi;
    private String aplicacio;


    public boolean filtresOK(IntegracioAccioDto accio, String integracioCodi) {

        return ((entitatCodi == null || entitatCodi.equals(""))
                || ((entitatCodi != null || !entitatCodi.equals("")) && accio.getCodiEntitat() != null && !"".equals(accio.getCodiEntitat())
                   && accio.getCodiEntitat().toLowerCase().contains(entitatCodi.toLowerCase())))
                && ((!"CALLBACK".equals(integracioCodi) || aplicacio != null || !"".equals(aplicacio))
                    || ((aplicacio != null || !aplicacio.equals("")) && accio.getAplicacio() != null && !accio.getAplicacio().equals("")
                        && accio.getAplicacio().toLowerCase().contains( aplicacio.toLowerCase())));
    }
}
