package es.caib.notib.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;

public interface RegistreService {

	
	/**
	 * Registra una sortida..
	 * @return El procediment creat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void registrarSortida(RegistreAnotacioDto registreAnotacio);
}
