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
public class FetCieRebutjada implements Fet {

    @JsonIgnore
    private Double cieRebutjada;

    @Override
    public String getCodi() {
        return FetEnum.CIE_REB.name();
    }

    @Override
    public Double getValor() {
        return cieRebutjada;
    }
}
