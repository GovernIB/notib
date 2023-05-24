package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.dto.AdviserResponseDto;
import es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser;

public interface AdviserService {

    AdviserResponseDto sincronitzarEnviament(EnviamentAdviser env);
}
