package es.caib.notib.logic.intf.statemachine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametresSm {

    private String enviamentUuid;
    private Long accioMassivaId;

    public boolean isMassiu() {
        return accioMassivaId != null;
    }
}
