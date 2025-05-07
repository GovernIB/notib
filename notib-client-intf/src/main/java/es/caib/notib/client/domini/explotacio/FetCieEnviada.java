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
public class FetCieEnviada implements Fet {

    @JsonIgnore
    private Double cieEnviada;

    @Override
    public String getCodi() {
        return FetEnum.CIE_ENV.name();
    }

    @Override
    public Double getValor() {
        return cieEnviada;
    }
}
