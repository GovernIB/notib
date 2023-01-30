/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.ExcepcioLogDto;
import es.caib.notib.logic.intf.dto.UsuariDto;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioService extends AbstractService<es.caib.notib.logic.intf.service.AplicacioService> implements es.caib.notib.logic.intf.service.AplicacioService {

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void actualitzarEntitatThreadLocal(String entitatCodi) {
		getDelegateService().actualitzarEntitatThreadLocal(entitatCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void processarAutenticacioUsuari() {
		getDelegateService().processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto getUsuariActual() {
		return getDelegateService().getUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<String> findRolsUsuariAmbCodi(String usuariCodi) {
		return getDelegateService().findRolsUsuariAmbCodi(usuariCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<String> findRolsUsuariActual() {
		return getDelegateService().findRolsUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return getDelegateService().findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return getDelegateService().findUsuariAmbText(text);
	}

	@Override
	@PermitAll
	public void excepcioSave(Throwable exception) {
		getDelegateService().excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return getDelegateService().excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return getDelegateService().excepcioFindAll();
	}

	@Override
	@PermitAll
	public List<String> permisosFindRolsDistinctAll() {
		return getDelegateService().permisosFindRolsDistinctAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGetByEntitat(String property) {
		return getDelegateService().propertyGetByEntitat(property);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGet(String property) {
		return getDelegateService().propertyGet(property);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGet(String property, String defaultValue) {
		return getDelegateService().propertyGet(property, defaultValue);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGetByEntitat(String property, String defaultValue) {
		return getDelegateService().propertyGetByEntitat(property, defaultValue);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto updateUsuariActual(UsuariDto usuariDto) {
		return getDelegateService().updateUsuariActual(usuariDto);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void updateRolUsuariActual(String rol) {
		getDelegateService().updateRolUsuariActual(rol);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void updateEntitatUsuariActual(Long entitat) {
		getDelegateService().updateEntitatUsuariActual(entitat);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public String getMetrics() {
		return getDelegateService().getMetrics();
	}

	@Override
	@PermitAll
	public String getAppVersion() {
		return getDelegateService().getAppVersion();
	}
	@Override
	@PermitAll
	public void setAppVersion(String appVersion) {
		getDelegateService().setAppVersion(appVersion);
	}

	@Override
	@PermitAll
	public String getMissatgeErrorAccesAdmin() {
		return getDelegateService().getMissatgeErrorAccesAdmin();
	}

	@Override
	@PermitAll
	public void restartSchedulledTasks() {
		getDelegateService().restartSchedulledTasks();
	}

    @Override
	@PermitAll
    public void propagateDbProperties() {
        getDelegateService().propagateDbProperties();
    }

}
