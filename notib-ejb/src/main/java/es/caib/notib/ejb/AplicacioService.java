/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.ExcepcioLogDto;
import es.caib.notib.logic.intf.dto.ProcessosInicialsEnum;
import es.caib.notib.logic.intf.dto.UsuariDto;
import org.springframework.context.annotation.Primary;

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
@Primary
@Stateless
public class AplicacioService extends AbstractService<es.caib.notib.logic.intf.service.AplicacioService> implements es.caib.notib.logic.intf.service.AplicacioService {

	@Override
	@RolesAllowed("**")
	public void actualitzarEntitatThreadLocal(String entitatCodi) {
		getDelegateService().actualitzarEntitatThreadLocal(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public void processarAutenticacioUsuari() {
		getDelegateService().processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getUsuariActual() {
		return getDelegateService().getUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public List<String> findRolsUsuariAmbCodi(String usuariCodi) {
		return getDelegateService().findRolsUsuariAmbCodi(usuariCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<String> findRolsUsuariActual() {
		return getDelegateService().findRolsUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariAmbCodi(String codi) {
		return getDelegateService().findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public String getIdiomaUsuariActual() {
		return getDelegateService().getIdiomaUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbText(String text) {
		return getDelegateService().findUsuariAmbText(text);
	}

	@Override
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
	public List<String> permisosFindRolsDistinctAll() {
		return getDelegateService().permisosFindRolsDistinctAll();
	}

	@Override
	@RolesAllowed("**")
	public String propertyGetByEntitat(String property) {
		return getDelegateService().propertyGetByEntitat(property);
	}

	@Override
	@RolesAllowed("**")
	public String propertyGet(String property) {
		return getDelegateService().propertyGet(property);
	}

	@Override
	@RolesAllowed("**")
	public String propertyGet(String property, String defaultValue) {
		return getDelegateService().propertyGet(property, defaultValue);
	}

	@Override
	@RolesAllowed("**")
	public String propertyGetByEntitat(String property, String defaultValue) {
		return getDelegateService().propertyGetByEntitat(property, defaultValue);
	}

	@Override
	@RolesAllowed("**")
	public boolean existeixUsuariNotib(String codi) {
		return getDelegateService().existeixUsuariNotib(codi);
	}

	@Override
	@RolesAllowed("**")
	public boolean existeixUsuariSeycon(String codi) {
		return getDelegateService().existeixUsuariSeycon(codi);
	}

	@Override
	@RolesAllowed("**")
	public void crearUsuari(String codi) {
		getDelegateService().crearUsuari(codi);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuariActual(UsuariDto usuariDto) {
		return getDelegateService().updateUsuariActual(usuariDto);
	}

	@Override
	@RolesAllowed("**")
	public void updateRolUsuariActual(String rol) {
		getDelegateService().updateRolUsuariActual(rol);
	}

	@Override
	@RolesAllowed("**")
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

	// PROCESSOS INICIALS
    @Override
	@RolesAllowed({"NOT_SUPER"})
    public List<ProcessosInicialsEnum> getProcessosInicialsPendents() {
        return getDelegateService().getProcessosInicialsPendents();
    }

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void updateProcesInicialExecutat(ProcessosInicialsEnum proces) {
		getDelegateService().updateProcesInicialExecutat(proces);
	}

	@Override
	@PermitAll
	public Integer getNumElementsPaginaDefecte() {
		return getDelegateService().getNumElementsPaginaDefecte();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean startRecording() {
		return getDelegateService().startRecording();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean stopRecording() throws Exception {
		return getDelegateService().stopRecording();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public String analyzeRecording() throws Exception {
		return getDelegateService().analyzeRecording();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public ArxiuDto getRecordingFile() {
		return getDelegateService().getRecordingFile();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean isRecording() {
		return getDelegateService().isRecording();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void restartSmBroker() throws Exception {
		getDelegateService().restartSmBroker();
	}

}
