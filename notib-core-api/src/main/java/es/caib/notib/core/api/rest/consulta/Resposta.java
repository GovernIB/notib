package es.caib.notib.core.api.rest.consulta;

import java.util.ArrayList;
import java.util.List;

import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Resposta {

	int numeroElements;
	List<Transmissio> resultat;
	
	public void setResultat(List<NotificacioEnviamentDto> enviaments, String basePath) {
		resultat = new ArrayList<Transmissio>();
		for (NotificacioEnviamentDto enviament: enviaments) {
			resultat.add(Transmissio.toTransmissio(enviament, basePath));
		}
	}
	
}
