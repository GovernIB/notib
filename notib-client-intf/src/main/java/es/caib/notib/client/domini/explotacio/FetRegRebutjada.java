package es.caib.notib.client.domini.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.ms.estadistica.model.Fet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FetRegRebutjada implements Fet {

    @JsonIgnore
    private Double regRebutjada;

    @Override
    public String getCodi() {
        return FetEnum.SIR_REB.name();
    }

    @Override
    public Double getValor() {
        return regRebutjada;
    }
}
