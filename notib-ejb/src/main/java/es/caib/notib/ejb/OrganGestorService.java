/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.Arbre;
import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class OrganGestorService extends AbstractService<es.caib.notib.logic.intf.service.OrganGestorService> implements es.caib.notib.logic.intf.service.OrganGestorService {

	@Override
	@RolesAllowed("**")
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		return getDelegateService().getProgresActualitzacio(dir3Codi);
	}

	@Override
	@RolesAllowed("**")
	public boolean isUpdatingOrgans(EntitatDto entitatDto) {
		return getDelegateService().isUpdatingOrgans(entitatDto);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void deleteHistoricSincronitzacio() {
		getDelegateService().deleteHistoricSincronitzacio();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void sincronitzar(Long organGestorId) {
		getDelegateService().sincronitzar(organGestorId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public Object[] syncDir3OrgansGestors(EntitatDto entitat) throws Exception {
		return getDelegateService().syncDir3OrgansGestors(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public byte[] getJsonOrgansGestorDir3(Long entitatId) {
		return getDelegateService().getJsonOrgansGestorDir3(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) throws Exception {
		return getDelegateService().predictSyncDir3OrgansGestors(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void syncOficinesSIR(Long entitatId) throws Exception {
		getDelegateService().syncOficinesSIR(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public boolean organGestorEnUs(Long organId) {
		return getDelegateService().organGestorEnUs(organId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganGestorDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed("**")
	public OrganGestorDto findById(Long entitatId, Long id) {
		return getDelegateService().findById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public OrganGestorDto findByCodi(Long entitatId, String codi) {
		return getDelegateService().findByCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorEstatDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		return getDelegateService().findOrgansGestorsCodiByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
		return getDelegateService().findByProcedimentIds(procedimentIds);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findByCodisAndEstat(List<String> codisOrgans, OrganGestorEstatEnum estat) {
		return getDelegateService().findByCodisAndEstat(codisOrgans, estat);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findDescencentsByCodi(Long entitatId, String organCodi) {
		return getDelegateService().findDescencentsByCodi(entitatId, organCodi);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(Long entitatId, String organCodiDir3, OrganGestorFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, organCodiDir3,filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<PermisDto> permisFind(Long entitatId, Long id) throws NotFoundException {
		return getDelegateService().permisFind(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {
		return getDelegateService().findAccessiblesByUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAccessiblesByUsuariAndEntitatActual(Long entitatId) {
		return getDelegateService().findAccessiblesByUsuariAndEntitatActual(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<PermisDto> permisFind(Long entitatId, Long id, PaginacioParamsDto paginacioParams) throws NotFoundException {
		return getDelegateService().permisFind(entitatId, id, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public void permisUpdate(Long entitatId, Long id, boolean isAdminOrgan, PermisDto permis) throws NotFoundException, ValidationException {
		getDelegateService().permisUpdate(entitatId, id, isAdminOrgan, permis);
	}

	@Override
	@RolesAllowed("**")
	public void permisDelete(Long entitatId, Long id, Long permisId) throws NotFoundException {
		getDelegateService().permisDelete(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		return getDelegateService().findOrganismes(entitat);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		return getDelegateService().findOrganismes(entitat, organGestor);
	}

	@Override
	@RolesAllowed("**")
	public LlibreDto getLlibreOrganisme(Long entitatId, String organGestorDir3Codi) {
		return getDelegateService().getLlibreOrganisme(entitatId, organGestorDir3Codi);
	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorEstatDto> getOrgansGestorsDisponiblesConsulta(Long entitatId, String usuari, RolEnumDto rol, String organ) {
		return getDelegateService().getOrgansGestorsDisponiblesConsulta(entitatId, usuari, rol, organ);
	}

	@Override
	@RolesAllowed("**")
	public Arbre<OrganGestorDto> generarArbreOrgans(EntitatDto entitat, OrganGestorFiltreDto filtres, boolean isAdminOrgan, OrganGestorDto organActual) {
		return getDelegateService().generarArbreOrgans(entitat, filtres, isAdminOrgan, organActual);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> getOrgansAsList(EntitatDto entitat) {
		return getDelegateService().getOrgansAsList(entitat);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> getOrgansAsList() {
		return getDelegateService().getOrgansAsList();
	}

	@Override
	@RolesAllowed("**")
	public OrganGestorDto getOrganNou(String codiSia) {
		return getDelegateService().getOrganNou(codiSia);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasPermisOrgan(Long entitatId, String organCodi, PermisEnum permis) {
		return getDelegateService().hasPermisOrgan(entitatId, organCodi, permis);
	}

	@Override
	@RolesAllowed("**")
	public void setServicesForSynctest(Object procSerSyncHelper, Object pluginHelper, Object integracioHelper) {
		getDelegateService().setServicesForSynctest(procSerSyncHelper, pluginHelper, integracioHelper);
	}

	@Override
	@RolesAllowed("**")
	public void sincronitzarOrganNomMultidioma(List<Long> ids) {
		getDelegateService().sincronitzarOrganNomMultidioma(ids);
	}

    @Override
	@RolesAllowed("**")
    public Long getLastPermisosModificatsInstant() {
        return getDelegateService().getLastPermisosModificatsInstant();
    }

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public FitxerDto exportacio(Long entitatId) throws IOException {
		return getDelegateService().exportacio(entitatId);
	}

    @Override
	@RolesAllowed("**")
	public OrganGestorDto update(OrganGestorDto dto) {
		return getDelegateService().update(dto);
	}

	@Override
	@RolesAllowed("**")
	public List<OficinaDto> getOficinesSIR(Long entitatId, String organGestorDir3Codi, boolean isFiltre) {
		return getDelegateService().getOficinesSIR(entitatId, organGestorDir3Codi, isFiltre);
	}

}
