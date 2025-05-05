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
public class FetNotExpirada implements Fet {

    @JsonIgnore
    private Double notExpirada;

    @Override
    public String getNom() {
        return "Expirada a Notific@";
    }

    @Override
    public Double getValor() {
        return notExpirada;
    }
}
