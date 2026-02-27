package es.caib.notib.logic.intf.dto.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.model.server.monitoring.Fet;
import lombok.Getter;

@Getter
public class FetNotib extends Fet {

    @JsonIgnore
    private FetEnum tipus;
    private Double valor;

    @Override
    public String getCodi() {
        return tipus.name();
    }

    public FetNotib(FetEnum tipus, Double valor) {
        this.tipus = tipus;
        this.valor = valor;
    }

    public FetNotib(FetEnum tipus, Long valor) {
        this.tipus = tipus;
        this.valor = valor != null ? valor.doubleValue() : null;
    }
}
