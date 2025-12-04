package es.caib.notib.client.domini;

import lombok.Getter;

@Getter
public enum EnviamentEstatGrup {

    TRAMITACIO("#777", "fa fa-clock"),
    PENDENT_COMPAREIXENCA("#e67e22", "fa fa-envelope-o"),
    LLEGIDA("#5cb85c", "fa fa-check-circle"),
    REBUTJADA("#6F5647", "fa fa-times"),
    EXPIRADA("#F1D629", "fa fa-asterisk"),
    ANULADA("#337ab7", ""),
    ERROR("#d9534f", "fa fa-exclamation-circle"),
    ESTAT_FICTICI("purple", "fa fa-send-o"),
    FINALITZADA("#1d541d", "fa fa-send-o"),
    PROCESSADA("#1d541d", "fa fa-send-o");

    private String color;
    private String icona;

    EnviamentEstatGrup(String color, String icona) {
        this.color = color;
        this.icona = icona;
    }
}
