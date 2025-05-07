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
public class FetNotEnviada implements Fet {

    @JsonIgnore
    private Double notEnviada;

    @Override
    public String getCodi() {
        return FetEnum.NOT_ENV.name();
    }

    @Override
    public Double getValor() {
        return notEnviada;
    }
}
