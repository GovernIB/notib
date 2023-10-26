/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.GrupFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de GrupService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class GrupService extends AbstractService<es.caib.notib.logic.intf.service.GrupService> implements es.caib.notib.logic.intf.service.GrupService {

	@Override
	@RolesAllowed("**")
	public GrupDto create(Long entitatId, GrupDto grup) {
		return getDelegateService().create(entitatId, grup);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto update(GrupDto grup) throws NotFoundException {
		return getDelegateService().update(grup);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto delete(Long id) throws NotFoundException {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public List<GrupDto> deleteGrupsProcediment(List<GrupDto> grups) throws NotFoundException {
		return getDelegateService().deleteGrupsProcediment(grups);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto findById(Long entitatId, Long id) {
		return getDelegateService().findById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ProcSerGrupDto> findByProcSer(Long entitatId, Long procedimentId, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findByProcSer(entitatId, procedimentId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<GrupDto> findByProcedimentAndUsuariGrups(Long procedimentId) {
		return getDelegateService().findByProcedimentAndUsuariGrups(procedimentId);
	}

	@Override
	@RolesAllowed("**")
	public ProcSerGrupDto findProcedimentGrupById(Long entitatId, Long procedimentGrupId) {
		return getDelegateService().findProcedimentGrupById(entitatId, procedimentGrupId);
	}
	
	@Override
	@RolesAllowed("**")
	public Boolean existProcedimentGrupByGrupId(Long entitatId, Long grupId) {
		return getDelegateService().existProcedimentGrupByGrupId(entitatId, grupId);
	}

	@Override
	@RolesAllowed("**")
	public List<GrupDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<GrupDto> findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		return getDelegateService().findByEntitatAndOrganGestor(entitat, organGestor);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<GrupDto> findAmbFiltrePaginat(Long entitatId, GrupFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<GrupDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<GrupDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto findByCodi(String grupCodi, Long entitatId) {
		return getDelegateService().findByCodi(grupCodi,entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<GrupDto> findGrupsByProcSer(Long procedimentId) {
		return getDelegateService().findGrupsByProcSer(procedimentId);
	}

}
