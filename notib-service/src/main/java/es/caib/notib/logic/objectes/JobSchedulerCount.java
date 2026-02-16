package es.caib.notib.logic.objectes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JobSchedulerCount {

    private int mida;
    private int count;

    public void incrementer(int mida) {

        this.mida += mida;
        count++;
    }
}
