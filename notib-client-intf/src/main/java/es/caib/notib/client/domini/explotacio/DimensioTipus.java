package es.caib.notib.client.domini.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.ms.estadistica.model.Dimensio;
import es.caib.notib.client.domini.EnviamentTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DimensioTipus implements Dimensio {

    @JsonIgnore
    private EnviamentTipus tipus;

    @Override
    public String getNom() {
        return "Tipus";
    }

    @Override
    public String getValor() {
        return tipus.name();
    }
}
