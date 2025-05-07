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
public class FetNotEnviamentError implements Fet {

    @JsonIgnore
    private Double notEnviamentError;

    @Override
    public String getCodi() {
        return FetEnum.NOT_ERR.name();
    }

    @Override
    public Double getValor() {
        return notEnviamentError;
    }
}
