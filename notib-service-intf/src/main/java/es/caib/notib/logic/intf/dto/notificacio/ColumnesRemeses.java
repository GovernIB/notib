package es.caib.notib.logic.intf.dto.notificacio;

import lombok.Data;

import java.io.Serializable;

@Data
public class ColumnesRemeses implements Serializable {

    private Long id;
    private boolean dataCreacio;
    private boolean dataEnviament;
    private boolean numRegistre;
    private boolean organEmisor;
    private boolean procSerCodi;
    private boolean numExpedient;
    private boolean concepte;
    private boolean creadaPer;
    private boolean interessats;
    private boolean estat;
}
