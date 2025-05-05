package es.caib.notib.client.domini.explotacio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.comanda.ms.estadistica.model.Dimensio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DimensioOrgan implements Dimensio {

    @JsonIgnore
    private String organCodi;

    @Override
    public String getNom() {
        return "Organ Gestor";
    }

    @Override
    public String getValor() {
        return organCodi;
    }
}
