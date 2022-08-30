package es.caib.notib.client.domini.consulta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericInfo {
    String codi;
    String nom;
    String descripcio;
}
