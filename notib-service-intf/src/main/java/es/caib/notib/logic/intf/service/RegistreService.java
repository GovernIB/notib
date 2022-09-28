package es.caib.notib.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;

public interface RegistreService {

	
	/**
	 * Registra una sortida..
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public void registrarSortida(RegistreAnotacioDto registreAnotacio);
}
