/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.LocalitatsDto;
import es.caib.notib.logic.intf.dto.NotificacioAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreErrorFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PaisosDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.logic.intf.dto.ProvinciesDto;
import es.caib.notib.logic.intf.dto.RegistreIdDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;

import javax.annotation.security.PermitAll;
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

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDtoV2 findAmbId(Long id, boolean isAdministrador) {
		return getDelegateService().findAmbId(id, isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador) {
		return getDelegateService().findNotificacioInfo(id, isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long entitatId, Long notificacioId) {
		return getDelegateService().eventFindAmbNotificacio(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioAuditDto> historicFindAmbNotificacio(Long entitatId, Long notificacioId) {
		return getDelegateService().historicFindAmbNotificacio(entitatId, notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})	
	public NotificacioEventDto findUltimEventCallbackByNotificacio(
			Long notificacioId) {
		return getDelegateService().findUltimEventCallbackByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<NotificacioEventDto> eventFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {
		return getDelegateService().eventFindAmbEnviament(entitatId, notificacioId, enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {
		return getDelegateService().historicFindAmbEnviament(entitatId, notificacioId, enviamentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto getDocumentArxiu(Long notificacioId) {
		return getDelegateService().getDocumentArxiu(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto getDocumentArxiu(Long notificacioId, Long documentId) {
		return getDelegateService().getDocumentArxiu(notificacioId, documentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public ArxiuDto enviamentGetCertificacioArxiu(Long enviamentId) {
		return getDelegateService().enviamentGetCertificacioArxiu(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public boolean enviar(Long notificacioId) {
		return getDelegateService().enviar(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(Long entitatId, Long enviamentId) {
		return getDelegateService().enviamentRefrescarEstat(entitatId, enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDatabaseDto create(Long entitatId, NotificacioDatabaseDto notificacio) throws RegistreNotificaException {
		return getDelegateService().create(entitatId, notificacio);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public NotificacioDatabaseDto update(Long entitatId, NotificacioDatabaseDto notificacio, boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {
		return getDelegateService().update(entitatId, notificacio, isAdministradorEntitat);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {
		getDelegateService().delete(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, rol, organGestorCodi, usuariCodi, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre) {
		return getDelegateService().findIdsAmbFiltre(entitatId, rol, organGestorCodi, usuariCodi, filtre);
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public String marcarComProcessada(Long enviamentId, String motiu, boolean isAdministrador) throws Exception {
		return getDelegateService().marcarComProcessada(enviamentId, motiu, isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException {
		return getDelegateService().registrarNotificar(notificacioId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProvinciesDto> llistarProvincies() {
		return getDelegateService().llistarProvincies();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		return getDelegateService().llistarLocalitats(codiProvincia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<PaisosDto> llistarPaisos() {
		return getDelegateService().llistarPaisos();
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsPendentsRegistrar() {
		return getDelegateService().getNotificacionsPendentsRegistrar();
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsPendentsEnviar() {
		return getDelegateService().getNotificacionsPendentsEnviar();
	}

	@Override
	@PermitAll
	public void notificacioEnviar(Long notificacioId) {
		getDelegateService().notificacioEnviar(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsPendentsRefrescarEstat() {
		return getDelegateService().getNotificacionsPendentsRefrescarEstat();
	}
	
	@Override
	@PermitAll
	public void enviamentRefrescarEstat(Long notificacioId) {
		getDelegateService().enviamentRefrescarEstat(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsPendentsRefrescarEstatRegistre() {
		return getDelegateService().getNotificacionsPendentsRefrescarEstatRegistre();
	}

	@Override
	@PermitAll
	public void enviamentRefrescarEstatRegistre(Long notificacioId) {
		getDelegateService().enviamentRefrescarEstatRegistre(notificacioId);		
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<NotificacioDto> findWithCallbackError(NotificacioErrorCallbackFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findWithCallbackError(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarConsulta(Long notificacioId) {
		return getDelegateService().reactivarConsulta(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarSir(Long notificacioId) {
		return getDelegateService().reactivarSir(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre, PaginacioParamsDto paginacioDtoFromRequest) {
		return getDelegateService().findNotificacionsAmbErrorRegistre(entitatId, filtre, paginacioDtoFromRequest);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public List<Long> findNotificacionsIdAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre) {
		return getDelegateService().findNotificacionsIdAmbErrorRegistre(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void reactivarRegistre(Long notificacioId) {
		getDelegateService().reactivarRegistre(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId) {
		return getDelegateService().findUltimEventRegistreByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void refrescarEnviamentsExpirats() {
		getDelegateService().refrescarEnviamentsExpirats();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat() {
		return getDelegateService().actualitzacioEnviamentsEstat();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines,
											 Boolean esUnitatArrel, Long provincia, String municipi) {

		return getDelegateService().cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> unitatsPerCodi(String codi) {
		return getDelegateService().unitatsPerCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) {
		return getDelegateService().unitatsPerDenominacio(denominacio);

	}

	@Override
	@RolesAllowed({"tothom"})
	public List<CodiValorDto> llistarNivellsAdministracions() {
		return getDelegateService().llistarNivellsAdministracions();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<CodiValorDto> llistarComunitatsAutonomes() {
		return getDelegateService().llistarComunitatsAutonomes();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<ProvinciesDto> llistarProvincies(String codiCA) {
		return getDelegateService().llistarProvincies(codiCA);
	}

	@Override
	@RolesAllowed({"tothom"})
	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid) {
		return getDelegateService().consultaDocumentIMetadades(identificador, esUuid);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean validarIdCsv (String idCsv) {
		return getDelegateService().validarIdCsv(idCsv);
	}

	@Override
	@PermitAll
	public boolean validarFormatCsv(String csv) {
		return getDelegateService().validarFormatCsv(csv);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void actualitzarReferencies() {
		 getDelegateService().actualitzarReferencies();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reenviarNotificacioAmbErrors(Long notificacioId) {
		return getDelegateService().reenviarNotificacioAmbErrors(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reactivarNotificacioAmbErrors(Long notificacioId) {
		return getDelegateService().reactivarNotificacioAmbErrors(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public SignatureInfoDto checkIfSignedAttached(byte[] contingut, String nom, String contentType) {
		return getDelegateService().checkIfSignedAttached(contingut, nom, contentType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsDEHPendentsRefrescarCert() {
		return getDelegateService().getNotificacionsDEHPendentsRefrescarCert();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsCIEPendentsRefrescarCert() {
		return getDelegateService().getNotificacionsCIEPendentsRefrescarCert();
	}

}