package es.caib.notib.logic.intf.statemachine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodiNomDto {

    private String codi;
    private String nom;
}
