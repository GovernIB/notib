package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.logic.intf.dto.notificacio.Persona;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteressatRegistreDto {

    Persona titular;
    Persona representant;
}
