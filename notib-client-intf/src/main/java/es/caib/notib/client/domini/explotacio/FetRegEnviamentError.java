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
public class FetRegEnviamentError implements Fet {

    @JsonIgnore
    private Double regEnviamentError;

    @Override
    public String getCodi() {
        return FetEnum.REG_ERR.name();
    }

    @Override
    public Double getValor() {
        return regEnviamentError;
    }
}
