package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;

public interface RegistreService {

	
	/**
	 * Registra una sortida..
	 * @return El procediment creat.
	 */
	@PreAuthorize("isAuthenticated()")
	void registrarSortida(RegistreAnotacioDto registreAnotacio);

	@PreAuthorize("isAuthenticated()")
	boolean enviarRegistre(EnviamentRegistreRequest enviamentRegistreRequest);

	@PreAuthorize("isAuthenticated()")
	boolean consultaSir(ConsultaSirDto enviament);

	@PreAuthorize("isAuthenticated()")
	RespostaSirAdviser sincronitzarEnviamentSir(SirAdviser adviser);
}
