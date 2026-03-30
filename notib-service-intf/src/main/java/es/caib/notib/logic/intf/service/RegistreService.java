package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;

public interface RegistreService {

	RespostaSirAdviser sincronitzarEnviamentSir(SirAdviser adviser);

}
