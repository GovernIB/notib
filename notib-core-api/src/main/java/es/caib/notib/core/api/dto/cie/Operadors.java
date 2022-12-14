package es.caib.notib.core.api.dto.cie;

import es.caib.notib.core.api.dto.IdentificadorTextDto;
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
