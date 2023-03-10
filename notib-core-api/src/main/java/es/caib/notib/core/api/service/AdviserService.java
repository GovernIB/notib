package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.AdviserResponseDto;
import es.caib.notib.core.api.dto.adviser.EnviamentAdviser;


public interface AdviserService {

    AdviserResponseDto sincronitzarEnviament(EnviamentAdviser env);
}
