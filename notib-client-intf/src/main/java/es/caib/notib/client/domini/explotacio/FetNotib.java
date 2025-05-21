package es.caib.notib.client.domini.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.ms.estadistica.model.Fet;
import lombok.Getter;

@Getter
public class FetNotib implements Fet {

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
