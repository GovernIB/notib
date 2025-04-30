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
public class FetRegAcceptada implements Fet {

    @JsonIgnore
    private Double regAcceptada;

    @Override
    public String getNom() {
        return "Registre SIR acceptat";
    }

    @Override
    public Double getValor() {
        return regAcceptada;
    }
}
