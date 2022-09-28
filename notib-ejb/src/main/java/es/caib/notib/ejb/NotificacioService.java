/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.notificacio.*;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class NotificacioService extends AbstractService<es.caib.notib.logic.intf.service.NotificacioService> implements es.caib.notib.logic.intf.service.NotificacioService {

	@Autowired
	NotificacioService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDtoV2 findAmbId(
			Long id,
			boolean isAdministrador) {
		return delegate.findAmbId(
				id,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador) {
		return delegate.findNotificacioInfo(id, isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId,
			Long notificacioId) {
		return delegate.eventFindAmbNotificacio(
				entitatId,
				notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioAuditDto> historicFindAmbNotificacio(Long entitatId, Long notificacioId) {
		return delegate.historicFindAmbNotificacio(entitatId, notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})	
	public NotificacioEventDto findUltimEventCallbackByNotificacio(
			Long notificacioId) {
		return delegate.findUltimEventCallbackByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId,
			Long notificacioId,
			Long enviamentId) {
		return delegate.eventFindAmbEnviament(
				entitatId,
				notificacioId,
				enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(
			Long entitatId,
			Long notificacioId,
			Long enviamentId) {
		return delegate.historicFindAmbEnviament(
				entitatId,
				notificacioId,
				enviamentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		return delegate.getDocumentArxiu(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto getDocumentArxiu(
			Long notificacioId,
			Long documentId) {
		return delegate.getDocumentArxiu(notificacioId, documentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
		return delegate.enviamentGetCertificacioArxiu(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean enviar(Long notificacioId) {
		return delegate.enviar(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId,
			Long enviamentId) {
		return delegate.enviamentRefrescarEstat(
				entitatId,
				enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDatabaseDto create(
			Long entitatId,
			NotificacioDatabaseDto notificacio) throws RegistreNotificaException {
		return delegate.create(entitatId, notificacio);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDatabaseDto update(
			Long entitatId,
			NotificacioDatabaseDto notificacio,
			boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {
		return delegate.update(
				entitatId, 
				notificacio,
				isAdministradorEntitat);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {
		delegate.delete(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			RolEnumDto rol,
			String organGestorCodi,
			String usuariCodi,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId,
				rol,
				organGestorCodi,
				usuariCodi,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<Long> findIdsAmbFiltre(Long entitatId,
									   RolEnumDto rol,
									   String organGestorCodi,
									   String usuariCodi,
									   NotificacioFiltreDto filtre) {
		return delegate.findIdsAmbFiltre(
				entitatId,
				rol,
				organGestorCodi,
				usuariCodi,
				filtre);
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public String marcarComProcessada(
			Long enviamentId,
			String motiu,
			boolean isAdministrador) throws Exception {
		return delegate.marcarComProcessada(
				enviamentId,
				motiu,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException {
		return delegate.registrarNotificar(notificacioId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProvinciesDto> llistarProvincies() {
		return delegate.llistarProvincies();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		return delegate.llistarLocalitats(codiProvincia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<PaisosDto> llistarPaisos() {
		return delegate.llistarPaisos();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRegistrar() {
		return delegate.getNotificacionsPendentsRegistrar();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsEnviar() {
		return delegate.getNotificacionsPendentsEnviar();
	}

	@Override
	public void notificacioEnviar(Long notificacioId) {
		delegate.notificacioEnviar(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRefrescarEstat() {
		return delegate.getNotificacionsPendentsRefrescarEstat();
	}
	
	@Override
	public void enviamentRefrescarEstat(Long notificacioId) {
		delegate.enviamentRefrescarEstat(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRefrescarEstatRegistre() {
		return delegate.getNotificacionsPendentsRefrescarEstatRegistre();
	}

	@Override
	public void enviamentRefrescarEstatRegistre(Long notificacioId) {
		delegate.enviamentRefrescarEstatRegistre(notificacioId);		
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<NotificacioDto> findWithCallbackError(
			NotificacioErrorCallbackFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findWithCallbackError(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarConsulta(Long notificacioId) {
		return delegate.reactivarConsulta(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarSir(Long notificacioId) {
		return delegate.reactivarSir(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(
			Long entitatId,
			NotificacioRegistreErrorFiltreDto filtre,
			PaginacioParamsDto paginacioDtoFromRequest) {
		return delegate.findNotificacionsAmbErrorRegistre(
				entitatId,
				filtre, 
				paginacioDtoFromRequest);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<Long> findNotificacionsIdAmbErrorRegistre(
			Long entitatId, 
			NotificacioRegistreErrorFiltreDto filtre) {
		return delegate.findNotificacionsIdAmbErrorRegistre(
				entitatId,
				filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void reactivarRegistre(Long notificacioId) {
		delegate.reactivarRegistre(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId) {
		return delegate.findUltimEventRegistreByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void refrescarEnviamentsExpirats() {
		delegate.refrescarEnviamentsExpirats();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat() {
		return delegate.actualitzacioEnviamentsEstat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio,
                                             Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) {
		return delegate.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
	}
	
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> unitatsPerCodi(String codi) {
		return delegate.unitatsPerCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) {
		return delegate.unitatsPerDenominacio(denominacio);

	}

	@Override
	@RolesAllowed({"tothom"})
	public List<CodiValorDto> llistarNivellsAdministracions() {
		return delegate.llistarNivellsAdministracions();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<CodiValorDto> llistarComunitatsAutonomes() {
		return delegate.llistarComunitatsAutonomes();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProvinciesDto> llistarProvincies(String codiCA) {
		return delegate.llistarProvincies(codiCA);
	}

	@Override
	@RolesAllowed({"tothom"})
	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid) {
		return delegate.consultaDocumentIMetadades(identificador, esUuid);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean validarIdCsv (String idCsv) {
		return delegate.validarIdCsv(idCsv);
	}

	@Override
	public boolean validarFormatCsv(String csv) {
		return delegate.validarFormatCsv(csv);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void actualitzarReferencies() {
		 delegate.actualitzarReferencies();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reenviarNotificacioAmbErrors(Long notificacioId) {
		return delegate.reenviarNotificacioAmbErrors(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarNotificacioAmbErrors(Long notificacioId) {
		return delegate.reactivarNotificacioAmbErrors(notificacioId);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsDEHPendentsRefrescarCert() {
		return delegate.getNotificacionsDEHPendentsRefrescarCert();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsCIEPendentsRefrescarCert() {
		return delegate.getNotificacionsCIEPendentsRefrescarCert();
	}

}