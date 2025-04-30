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
public class FetCieError implements Fet {

    @JsonIgnore
    private Double cieError;

    @Override
    public String getNom() {
        return "Error en l'entrega per CIE";
    }

    @Override
    public Double getValor() {
        return cieError;
    }
}
