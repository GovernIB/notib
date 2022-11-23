/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.service.PermisosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PermisosServiceBean implements PermisosService {

	@Autowired
	PermisosService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisNotificacio(Long entitatId, String usuariCodi) {
		return delegate.hasPermisNotificacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacio(Long entitatId, String usuariCodi) {
		return delegate.hasPermisComunicacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi) {
		return delegate.hasPermisComunicacioSir(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorDto> getOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.getOrgansAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<String> getOrgansCodisAmbPermisPerProcedimentComu(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerDto procSetDto) {
		return delegate.getOrgansCodisAmbPermisPerProcedimentComu(entitatId, usuariCodi, permis, procSetDto);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<String> getProcedimentsOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        return delegate.getProcedimentsOrgansAmbPermis(entitatId, usuariCodi, permis);
    }

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getProcSersAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.getProcSersAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getProcedimentsAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.getProcedimentsAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getServeisAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return delegate.getServeisAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	public boolean hasNotificacioPermisProcessar(Long notId, Long entitat, String usuari, PermisEnum permis) {
		return delegate.hasNotificacioPermisProcessar(notId, entitat, usuari, permis);
	}

}
