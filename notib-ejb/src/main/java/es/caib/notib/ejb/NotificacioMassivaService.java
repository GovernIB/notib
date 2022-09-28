/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.*;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.springframework.beans.factory.annotation.Autowired;

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
@Stateless
public class NotificacioMassivaService extends AbstractService<es.caib.notib.logic.intf.service.NotificacioMassivaService> implements es.caib.notib.logic.intf.service.NotificacioMassivaService {

	@Autowired
    NotificacioMassivaService delegate;

	@Override
	public void posposar(Long entitatId, Long notificacioMassivaId) {
		delegate.posposar(entitatId, notificacioMassivaId);
	}
	@Override
	public void reactivar(Long entitatId, Long notificacioMassivaId) {
		delegate.reactivar(entitatId, notificacioMassivaId);
	}

	@Override
	public NotificacioMassivaDataDto findById(Long entitatId, Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	public PaginaDto<NotificacioTableItemDto> findNotificacions(Long entitatId, Long notificacioMassivaId, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return delegate.findNotificacions(entitatId, notificacioMassivaId, filtre, paginacioParams);
	}
	@Override
	public NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId) {
		return delegate.getNotificacioMassivaInfo(entitatId, notificacioMassivaId);
	}
	@Override
	public FitxerDto getCSVFile(Long entitatId, Long notificacioMassivaId) {
		return delegate.getCSVFile(entitatId, notificacioMassivaId);
	}
	@Override
	public FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId) {
		return delegate.getZipFile(entitatId, notificacioMassivaId);
	}
	@Override
	public FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId) {
		return delegate.getResumFile(entitatId, notificacioMassivaId);
	}

	@Override
	public FitxerDto getErrorsValidacioFile(Long entitatId, Long notificacioMassivaId) {
		return delegate.getErrorsValidacioFile(entitatId, notificacioMassivaId);
	}

	@Override
	public FitxerDto getErrorsExecucioFile(Long entitatId, Long notificacioMassivaId) {
		return delegate.getErrorsExecucioFile(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException {
		return delegate.getModelDadesCarregaMassiuCSV();
	}

	@Override
	public void cancelar(Long entitatId, Long notificacioMassivaId) throws Exception {
		delegate.cancelar(entitatId, notificacioMassivaId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioMassivaDataDto create(
			Long entitatId,
			String usuariCodi,
			NotificacioMassivaDto notificacioMassiu) throws RegistreNotificaException {
		return delegate.create(entitatId, usuariCodi, notificacioMassiu);
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void delete(
			Long entitatId,
			Long notificacioMassivaId) {
		delegate.delete(entitatId, notificacioMassivaId);
	}
	@Override
	@RolesAllowed({"tothom"})
	public PaginaDto<NotificacioMassivaTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			NotificacioMassivaFiltreDto filtre,
			RolEnumDto rol,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(entitatId, filtre, rol, paginacioParams);
	}
}