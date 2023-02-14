/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.ExcepcioLogDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void actualitzarEntitatThreadLocal(String entitatCodi) {
		delegate.actualitzarEntitatThreadLocal(entitatCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void processarAutenticacioUsuari() {
		delegate.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto getUsuariActual() {
		return delegate.getUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<String> findRolsUsuariAmbCodi(String usuariCodi) {
		return delegate.findRolsUsuariAmbCodi(usuariCodi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<String> findRolsUsuariActual() {
		return delegate.findRolsUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegate.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String getIdiomaUsuariActual() {
		return delegate.getIdiomaUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegate.findUsuariAmbText(text);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		delegate.excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegate.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegate.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegate.permisosFindRolsDistinctAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGetByEntitat(String property) {
		return delegate.propertyGetByEntitat(property);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGet(String property) {
		return delegate.propertyGet(property);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGet(String property, String defaultValue) {
		return delegate.propertyGet(property, defaultValue);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public String propertyGetByEntitat(String property, String defaultValue) {
		return delegate.propertyGetByEntitat(property, defaultValue);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public UsuariDto updateUsuariActual(UsuariDto usuariDto) {
		return delegate.updateUsuariActual(usuariDto);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void updateRolUsuariActual(String rol) {
		delegate.updateRolUsuariActual(rol);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL", "NOT_CARPETA"})
	public void updateEntitatUsuariActual(Long entitat) {
		delegate.updateEntitatUsuariActual(entitat);
	}
	
	@Override
	@RolesAllowed({"NOT_SUPER"})
	public String getMetrics() {
		return delegate.getMetrics();
	}

	@Override
	public String getAppVersion() {
		return delegate.getAppVersion();
	}
	@Override
	public void setAppVersion(String appVersion) {
		delegate.setAppVersion(appVersion);
	}

	@Override
	public String getMissatgeErrorAccesAdmin() {
		return delegate.getMissatgeErrorAccesAdmin();
	}

}
