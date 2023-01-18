/**
 * 
 */
package es.caib.notib.ejb;


import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PermisosService extends AbstractService<es.caib.notib.logic.intf.service.PermisosService> implements es.caib.notib.logic.intf.service.PermisosService {

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisNotificacio(Long entitatId, String usuariCodi) {
		return getDelegateService().hasPermisNotificacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacio(Long entitatId, String usuariCodi) {
		return getDelegateService().hasPermisComunicacio(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi) {
		return getDelegateService().hasPermisComunicacioSir(entitatId, usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorDto> getOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return getDelegateService().getOrgansAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	public boolean hasUsrPermisOrgan(Long entitatId, String usr, String organCodi, PermisEnum permis) {
		return getDelegateService().hasUsrPermisOrgan(entitatId, usr, organCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<String> getOrgansCodisAmbPermisPerProcedimentComu(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerDto procSetDto) {
		return getDelegateService().getOrgansCodisAmbPermisPerProcedimentComu(entitatId, usuariCodi, permis, procSetDto);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
    public List<String> getProcedimentsOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
        return getDelegateService().getProcedimentsOrgansAmbPermis(entitatId, usuariCodi, permis);
    }

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getProcSersAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return getDelegateService().getProcSersAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getProcedimentsAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return getDelegateService().getProcedimentsAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public List<CodiValorOrganGestorComuDto> getServeisAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		return getDelegateService().getServeisAmbPermis(entitatId, usuariCodi, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public boolean hasNotificacioPermis(Long notId, Long entitat, String usuari, PermisEnum permis) {
		return getDelegateService().hasNotificacioPermis(notId, entitat, usuari, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public void evictGetOrgansAmbPermis() {

	}
}
