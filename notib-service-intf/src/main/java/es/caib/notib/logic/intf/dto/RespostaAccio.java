package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaAccio<T> {
    @Singular
    private List<T> executades;
    @Singular
    private List<T> errors;
    @Singular
    private List<T> noExecutables;

    public boolean isEmpty() {
        return executades.isEmpty() && errors.isEmpty() && noExecutables.isEmpty();
    }
}
