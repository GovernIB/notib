/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.RespostaTestAplicacio;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariAplicacioService {

	/**
	 * Registra una nova aplicació.
	 * 
	 * @param aplicacio Informació de l'aplicació a crear.
	 *            
	 * @return L'aplicació creada.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public AplicacioDto create(AplicacioDto aplicacio);

	/**
	 * Actualitza la informació de l'aplicació que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param aplicacio Informació de l'aplicació a modificar.
	 *            
	 * @return L'aplicació modificada.
	 * 
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public AplicacioDto update(AplicacioDto aplicacio) throws NotFoundException;

	/**
	 * Esborra l'aplicació amb el mateix id que l'especificat.
	 * 
	 * @param id Atribut id de l'aplicació a esborrar.
	 * @param entitatId Atribut id de la entitat a la que pertany l'apliació
	 *             
	 * @return L'aplicació esborrada.
	 * 
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public AplicacioDto delete(Long id, Long entitatId) throws NotFoundException;
	
	/**
	 * Consulta una aplicació a partir d'un l'identificador.
	 * 
	 * @param aplicacioId Identificador de l'aplicació a consultar
	 *            
	 * @return L'aplicació amb l'identificador assenyalat.
	 */
	@PreAuthorize("isAuthenticated()")
	public AplicacioDto findById(Long aplicacioId);
	
	/**
	 * Consulta una aplicació a partir d'un l'identificador.
	 * 
	 * @param aplicacioId Identificador de l'aplicació a consultar
	 * @param entitatId Atribut id de la entitat a la que pertany l'apliació
	 *            
	 * @return L'aplicació amb l'identificador assenyalat.
	 */
	@PreAuthorize("isAuthenticated()")
	public AplicacioDto findByEntitatAndId(Long entitatId, Long aplicacioId);
	
	/**
	 * Consulta una aplicació a partir del codi.
	 * @return L'aplicació amb el codi assenyalat.
	 */
	@PreAuthorize("isAuthenticated()")
	public AplicacioDto findByUsuariCodi(String usuariCodi);
	
	/**
	 * Consulta una aplicació a partir del codi.
	 * 
	 * @param usuariCodi Codi de l'usuari a consultar
	 * @param entitatId Atribut id de la entitat a la que pertany l'apliació
	 *            
	 * @return L'aplicació amb el codi assenyalat.
	 */
	@PreAuthorize("isAuthenticated()")
	public AplicacioDto findByEntitatAndUsuariCodi(Long entitatId, String usuariCodi);
	
	/**
	 * Consulta una aplicació a partir d'un text
	 * 
	 * @param text Text a cercar
	 * @param entitatId Atribut id de la entitat a la que pertany l'apliació
	 *            
	 * @return L'aplicació que coincideix  amb el text introduït.
	 */
	@PreAuthorize("isAuthenticated()")
	List<AplicacioDto> findByEntitatAndText(Long entitatId, String text);
	
	/**
	 * Llistat amb totes les aplicacions paginades.
	 * 
	 * @param paginacioParams Paràmetres per a dur a terme la paginació del resultats.
	 *            
	 * @return La pàgina d'aplicacions.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<AplicacioDto> findPaginat(PaginacioParamsDto paginacioParams);
	
	/**
	 * Llistat amb totes les aplicacions paginades.
	 * 
	 * @param paginacioParams Paràmetres per a dur a terme la paginació del resultats.
	 *            
	 * @return La pàgina d'aplicacions.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<AplicacioDto> findPaginatByEntitat(Long entitatId, PaginacioParamsDto paginacioParams);
	
	/**
	 * Marca l'aplicació amb l'id especificat com a activa/inactiva.
	 * 
	 * @param id Atribut id de l'aplicació a modificar.
	 * @param activa true si es vol activar o false en cas contrari.
	 * @return L'aplicació modificada.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	public AplicacioDto updateActiva(Long id, boolean activa);


	@PreAuthorize("hasRole('NOT_ADMIN')")
	RespostaTestAplicacio provarAplicacio(Long aplicacioId);

	@PreAuthorize("hasRole('NOT_SUPER')")
	boolean diagnosticarAplicacions(Map<String, IntegracioDiagnostic> diagnostics);

	@PreAuthorize("hasRole('NOT_SUPER')")
	IntegracioDiagnostic diagnosticarAplicacions(Long entitatId);
}
