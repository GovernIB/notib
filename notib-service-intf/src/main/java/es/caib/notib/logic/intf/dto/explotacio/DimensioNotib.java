package es.caib.notib.logic.intf.dto.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.model.server.monitoring.Dimensio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DimensioNotib extends Dimensio {

    @JsonIgnore
    private DimEnum tipus;
    private String valor;

    @Override
    public String getCodi() {
        return tipus.name();
    }

    public DimensioNotib(DimEnum tipus, Double valor) {
        this.tipus = tipus;
        this.valor = valor != null ? String.valueOf(valor) : null;
    }

    public DimensioNotib(DimEnum tipus, Long valor) {
        this.tipus = tipus;
        this.valor = valor != null ? String.valueOf(valor) : null;
    }

    public <E extends Enum<E>> DimensioNotib(DimEnum tipus, E valor) {
        this.tipus = tipus;
        this.valor = valor != null ? valor.name() : null;
    }

}
