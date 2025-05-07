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
public class FetCieEnviamentError implements Fet {

    @JsonIgnore
    private Double cieEnviamentError;

    @Override
    public String getCodi() {
        return FetEnum.CIE_ERR.name();
    }

    @Override
    public Double getValor() {
        return cieEnviamentError;
    }
}
