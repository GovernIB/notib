/**
 * 
 */
package es.caib.notib.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariAplicacioService {

	/**
	 * Registra una nova aplicació.
	 * 
	 * @param aplicacio
	 *            Informació de l'aplicació a crear.
	 *            
	 * @return L'aplicació creada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public AplicacioDto create(AplicacioDto aplicacio);

	/**
	 * Actualitza la informació de l'aplicació que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param aplicacio
	 *            Informació de l'aplicació a modificar.
	 *            
	 * @return L'aplicació modificada.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException;

	/**
	 * Esborra l'aplicació amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'aplicació a esborrar.
	 *            
	 * @return L'aplicació esborrada.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public AplicacioDto delete(
			Long id) throws NotFoundException;
	
	/**
	 * Consulta una aplicació a partir d'un l'identificador.
	 * 
	 * @param aplicacioId
	 *            Identificador de l'aplicació a consultar
	 *            
	 * @return L'aplicació amb l'identificador assenyalat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public AplicacioDto findById(Long aplicacioId);
	
	/**
	 * Consulta una aplicació a partir del codi.
	 * 
	 * @param aplicacioCodi
	 *            Codi de l'aplicació a consultar
	 *            
	 * @return L'aplicació amb el codi assenyalat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public AplicacioDto findByCodi(String aplicacioCodi);
	
	/**
	 * Llistat amb totes les aplicacions paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 *            
	 * @return La pàgina d'aplicacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams);
	
}
