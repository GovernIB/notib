/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.ExcepcioLogDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {


	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	void actualitzarEntiatThreadLocal(EntitatDto entitat);
	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public void processarAutenticacioUsuari() throws NotFoundException;

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public UsuariDto getUsuariActual();

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Obté els rols d'un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public List<String> findRolsUsuariAmbCodi(String usuariCodi);
	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public List<String> findRolsUsuariActual();
	
	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Emmagatzema una excepció llençada per un servei.
	 * 
	 * @param exception
	 *             L'excepció a emmagatzemar.
	 */
	public void excepcioSave(Throwable exception);

	/**
	 * Consulta la informació d'una excepció donat el seu índex.
	 * 
	 * @param index
	 *             L'index de l'excepció.
	 * @return L'excepció.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public ExcepcioLogDto excepcioFindOne(Long index);

	/**
	 * Retorna una llista amb les darreres excepcions emmagatzemades.
	 * 
	 * @return La llista amb les darreres excepcions.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	public List<ExcepcioLogDto> excepcioFindAll();

	/**
	 * Retorna una llista amb els diferents rols els quals
	 * tenen assignat algun permis.
	 * 
	 * @return La llista amb els rols.
	 */
	public List<String> permisosFindRolsDistinctAll();

	/**
	 * Retorna el valor d'un paràmetre de configuració per la entitat especificada
	 *
	 * @param property El codi del paràmetre
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	 String propertyGetByEntitat(String property);

	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 * 
	 * @param property
	 *             El codi del paràmetre
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	public String propertyGet(String property);
	
	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 * 
	 * @param property
	 *             El codi del paràmetre
	 * @param defaultValue
	 *             El valor per defecte en cas que el paràmetre no s'hagi definit
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	String propertyGet(String property, String defaultValue);

	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 *
	 * @param property El codi del paràmetre
	 * @param defaultValue El valor per defecte en cas que el paràmetre no s'hagi definit
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL') or hasRole('NOT_CARPETA')")
	String propertyGetByEntitat(String property, String defaultValue);

	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public UsuariDto updateUsuariActual(UsuariDto asDto);
	
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public void updateRolUsuariActual(String rol);
	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public void updateEntitatUsuariActual(Long entitat);
	
	/**
	 * Recupera les mètriques de l'aplicació.
	 * 
	 * @return El registre de les mètriques.
	 */
	public String getMetrics();

	public String getAppVersion();
	public void setAppVersion(String appVersion);

	String getMissatgeErrorAccesAdmin();

	void restartSchedulledTasks();

    public void propagateDbProperties();
}