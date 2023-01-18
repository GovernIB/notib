package es.caib.notib.logic.intf.dto.cie;

import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operadors {

    private List<IdentificadorTextDto> operadorsPostal;
    private List<IdentificadorTextDto> operadorsCie;

}