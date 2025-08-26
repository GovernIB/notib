/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaTableItemDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class NotificacioMassivaService extends AbstractService<es.caib.notib.logic.intf.service.NotificacioMassivaService> implements es.caib.notib.logic.intf.service.NotificacioMassivaService {

	@Override
	@RolesAllowed("**")
	public void posposar(Long entitatId, Long notificacioMassivaId) {
		getDelegateService().posposar(entitatId, notificacioMassivaId);
	}
	@Override
	@RolesAllowed("**")
	public void reactivar(Long entitatId, Long notificacioMassivaId) {
		getDelegateService().reactivar(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public NotificacioMassivaDataDto findById(Long entitatId, Long id) {
		return getDelegateService().findById(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<NotificacioTableItemDto> findNotificacions(Long entitatId, Long notificacioMassivaId, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findNotificacions(entitatId, notificacioMassivaId, filtre, paginacioParams);
	}
	@Override
	@RolesAllowed("**")
	public NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId) {
		return getDelegateService().getNotificacioMassivaInfo(entitatId, notificacioMassivaId);
	}
	@Override
	@RolesAllowed("**")
	public FitxerDto getCSVFile(Long entitatId, Long notificacioMassivaId) {
		return getDelegateService().getCSVFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId) {
		return getDelegateService().getZipFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId) {
		return getDelegateService().getResumFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getErrorsValidacioFile(Long entitatId, Long notificacioMassivaId) throws IOException {
		return getDelegateService().getErrorsValidacioFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getErrorsExecucioFile(Long entitatId, Long notificacioMassivaId) throws IOException {
		return getDelegateService().getErrorsExecucioFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException {
		return getDelegateService().getModelDadesCarregaMassiuCSV();
	}

    @Override
	@RolesAllowed("**")
    public byte[] getCodisEntregaPostal() throws IOException {
        return getDelegateService().getCodisEntregaPostal();
    }

    @Override
	@RolesAllowed("**")
	public void cancelar(Long entitatId, Long notificacioMassivaId) throws Exception {
		getDelegateService().cancelar(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public void iniciar(Long id) {
		getDelegateService().iniciar(id);
	}

	@Override
	@RolesAllowed("**")
	@TransactionTimeout(value = 3600)
	public NotificacioMassivaDataDto create(Long entitatId, String usuariCodi,
			NotificacioMassivaDto notificacioMassiu) throws RegistreNotificaException {
		return getDelegateService().create(entitatId, usuariCodi, notificacioMassiu);
	}
	@Override
	@RolesAllowed("**")
	public void delete(Long entitatId, Long notificacioMassivaId) {
		getDelegateService().delete(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<NotificacioMassivaTableItemDto> findAmbFiltrePaginat(Long entitatId, NotificacioMassivaFiltreDto filtre, RolEnumDto rol, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, filtre, rol, paginacioParams);
	}
}