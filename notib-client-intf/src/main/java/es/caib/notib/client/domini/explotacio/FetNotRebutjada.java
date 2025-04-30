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
public class FetNotRebutjada implements Fet {

    @JsonIgnore
    private Double notRebutjada;

    @Override
    public String getNom() {
        return "Rebutjada a Notific@";
    }

    @Override
    public Double getValor() {
        return notRebutjada;
    }
}
