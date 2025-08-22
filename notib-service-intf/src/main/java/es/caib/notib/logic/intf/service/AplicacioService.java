/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.ExcepcioLogDto;
import es.caib.notib.logic.intf.dto.ProcessosInicialsEnum;
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


	@PreAuthorize("isAuthenticated()")
	void actualitzarEntitatThreadLocal(String entitat);

	/**
	 * Processa l'autenticació d'un usuari.
	 * @throws NotFoundException Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PreAuthorize("isAuthenticated()")
	void processarAutenticacioUsuari() throws NotFoundException;

	/**
	 * Obté l'usuari actual.
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	UsuariDto getUsuariActual();

	/**
	 * Obté un usuari donat el seu codi.
	 * @param codi Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	UsuariDto findUsuariAmbCodi(String codi);

	@PreAuthorize("isAuthenticated()")
	String getIdiomaUsuariActual();

	/**
	 * Obté els rols d'un usuari donat el seu codi.
	 * @param usuariCodi Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	List<String> findRolsUsuariAmbCodi(String usuariCodi);
	
	@PreAuthorize("isAuthenticated()")
	List<String> findRolsUsuariActual();
	
	/**
	 * Consulta els usuaris donat un text.
	 * @param text Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("isAuthenticated()")
	List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Emmagatzema una excepció llençada per un servei.
	 * @param exception L'excepció a emmagatzemar.
	 */
	void excepcioSave(Throwable exception);

	/**
	 * Consulta la informació d'una excepció donat el seu índex.
	 * @param index L'index de l'excepció.
	 * @return L'excepció.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	ExcepcioLogDto excepcioFindOne(Long index);

	/**
	 * Retorna una llista amb les darreres excepcions emmagatzemades.
	 * @return La llista amb les darreres excepcions.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	List<ExcepcioLogDto> excepcioFindAll();

	/**
	 * Retorna una llista amb els diferents rols els quals tenen assignat algun permis.
	 * @return La llista amb els rols.
	 */
	List<String> permisosFindRolsDistinctAll();

	/**
	 * Retorna el valor d'un paràmetre de configuració per la entitat especificada
	 * @param property El codi del paràmetre
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("isAuthenticated()")
	 String propertyGetByEntitat(String property);

	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 * @param property El codi del paràmetre
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("isAuthenticated()")
	String propertyGet(String property);
	
	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 * @param property El codi del paràmetre
	 * @param defaultValue El valor per defecte en cas que el paràmetre no s'hagi definit
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("isAuthenticated()")
	String propertyGet(String property, String defaultValue);

	/**
	 * Retorna el valor d'un paràmetre de configuració de l'aplicació.
	 *
	 * @param property El codi del paràmetre
	 * @param defaultValue El valor per defecte en cas que el paràmetre no s'hagi definit
	 * @return el valor del paràmetre
	 */
	@PreAuthorize("isAuthenticated()")
	String propertyGetByEntitat(String property, String defaultValue);

	/**
	 * @param codi Codi de l'usuari
	 * @return true si existeix a la taula not_usuari
	 */
	@PreAuthorize("isAuthenticated()")
	boolean existeixUsuariNotib(String codi);

	/**
	 * @param codi Codi de l'usuari
	 * @return true si existeix Seycon
	 */
	@PreAuthorize("isAuthenticated()")
	boolean existeixUsuariSeycon(String codi);

	/**
	 * Crea la configuració de l'usuari
	 */
	@PreAuthorize("isAuthenticated()")
	void crearUsuari(String nom);

	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	UsuariDto updateUsuariActual(UsuariDto asDto);
	
	@PreAuthorize("isAuthenticated()")
	void updateRolUsuariActual(String rol);
	@PreAuthorize("isAuthenticated()")
	void updateEntitatUsuariActual(Long entitat);
	
	/**
	 * Recupera les mètriques de l'aplicació.
	 * 
	 * @return El registre de les mètriques.
	 */
	String getMetrics();

	String getAppVersion();
	void setAppVersion(String appVersion);

	String getMissatgeErrorAccesAdmin();

	@PreAuthorize("hasRole('NOT_SUPER')")
	void restartSchedulledTasks();

	public void propagateDbProperties();

	// PROCESSOS INICIALS
	@PreAuthorize("hasRole('NOT_SUPER')")
	List<ProcessosInicialsEnum> getProcessosInicialsPendents();

	@PreAuthorize("hasRole('NOT_SUPER')")
	void updateProcesInicialExecutat(ProcessosInicialsEnum proces);

	@PreAuthorize("hasRole('NOT_SUPER')")
	void restartSmBroker() throws Exception;

	@PreAuthorize("isAuthenticated()")
    Integer getNumElementsPaginaDefecte();

	@PreAuthorize("hasRole('NOT_SUPER')")
    boolean startRecording();

	@PreAuthorize("hasRole('NOT_SUPER')")
	boolean stopRecording() throws Exception;

	@PreAuthorize("hasRole('NOT_SUPER')")
	String analyzeRecording() throws Exception;

	@PreAuthorize("hasRole('NOT_SUPER')")
    ArxiuDto getRecordingFile() throws Exception;

	@PreAuthorize("hasRole('NOT_SUPER')")
	boolean isRecording();

    String getMetriques() throws Exception;
}
