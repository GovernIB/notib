package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegracioDiagnostic {

    private boolean correcte;
    private String errMsg;
    private String prova;
    private Map<String, IntegracioDiagnostic> diagnosticsEntitat;
}
