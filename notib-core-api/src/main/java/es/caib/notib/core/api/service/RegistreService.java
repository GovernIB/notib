package es.caib.notib.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.RegistreAnotacioDto;

public interface RegistreService {

	
	/**
	 * Registra una sortida.
	 * 
	 * @param procediment
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public void registrarSortida(RegistreAnotacioDto registreAnotacio);
}
