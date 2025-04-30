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
public class FetNotNotificada implements Fet {

    @JsonIgnore
    private Double notNotificada;

    @Override
    public String getNom() {
        return "Acceptada a Notific@";
    }

    @Override
    public Double getValor() {
        return notNotificada;
    }
}
