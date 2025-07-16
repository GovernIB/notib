/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.DocCieValid;
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
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElement;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class NotificacioService extends AbstractService<es.caib.notib.logic.intf.service.NotificacioService> implements es.caib.notib.logic.intf.service.NotificacioService {

	@Override
	@RolesAllowed("**")
	public NotificacioDtoV2 findAmbId(Long id, boolean isAdministrador) {
		return getDelegateService().findAmbId(id, isAdministrador);
	}

	@Override
	@RolesAllowed("**")
	public NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador) {
		return getDelegateService().findNotificacioInfo(id, isAdministrador);
	}

	@Override
	@RolesAllowed("**")
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long entitatId, Long notificacioId) {
		return getDelegateService().eventFindAmbNotificacio(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public List<NotificacioAuditDto> historicFindAmbNotificacio(Long entitatId, Long notificacioId) {
		return getDelegateService().historicFindAmbNotificacio(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public NotificacioEventDto findUltimEventCallbackByNotificacio(Long notificacioId) {
		return getDelegateService().findUltimEventCallbackByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public List<NotificacioEventDto> eventFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {
		return getDelegateService().eventFindAmbEnviament(entitatId, notificacioId, enviamentId);
	}

	@Override
	@RolesAllowed("**")
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {
		return getDelegateService().historicFindAmbEnviament(entitatId, notificacioId, enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public byte[] getDiagramaMaquinaEstats() throws IOException  {
		return getDelegateService().getDiagramaMaquinaEstats();
	}

	@Override
	@RolesAllowed("**")
	public ArxiuDto getDocumentArxiu(Long notificacioId) {
		return getDelegateService().getDocumentArxiu(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public ArxiuDto getDocumentArxiu(Long notificacioId, Long documentId) {
		return getDelegateService().getDocumentArxiu(notificacioId, documentId);
	}

	@Override
	@RolesAllowed("**")
	public ArxiuDto enviamentGetCertificacioArxiu(Long enviamentId) {
		return getDelegateService().enviamentGetCertificacioArxiu(enviamentId);
	}

    @Override
	@RolesAllowed("**")
    public void refrescarEstatEnviamentASir(Long enviamentId, boolean retry) {
        getDelegateService().refrescarEstatEnviamentASir(enviamentId, retry);
    }

    @Override
	@RolesAllowed("**")
	public boolean enviarNotificacioANotifica(Long notificacioId, boolean retry) {
		return getDelegateService().enviarNotificacioANotifica(notificacioId, retry);
	}

	@Override
	@RolesAllowed("**")
	public RespostaAccio<AccioMassivaElement> resetConsultaEstat(AccioMassivaExecucio accio) {
		return getDelegateService().resetConsultaEstat(accio);
	}


	@Override
	@RolesAllowed("**")
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(Long entitatId, Long enviamentId) {
		return getDelegateService().enviamentRefrescarEstat(entitatId, enviamentId);
	}

	@Override
	@RolesAllowed("**")
	public Notificacio create(Long entitatId, Notificacio notificacio) throws RegistreNotificaException {
		return getDelegateService().create(entitatId, notificacio);
	}

	@Override
	@RolesAllowed("**")
	public List<Notificacio> crearSirDividida(Long entitatId, Notificacio notificacio) throws Exception {
		return getDelegateService().crearSirDividida(entitatId, notificacio);
	}

	@Override
	@RolesAllowed("**")
	public Notificacio update(Long entitatId, Notificacio notificacio, boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {
		return getDelegateService().update(entitatId, notificacio, isAdministradorEntitat);
	}

	@Override
	@RolesAllowed("**")
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {
		getDelegateService().delete(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void restore(Long entitatId, Long notificacioId) throws Exception {
		getDelegateService().restore(entitatId, notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(entitatId, rol, organGestorCodi, usuariCodi, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre) {
		return getDelegateService().findIdsAmbFiltre(entitatId, rol, organGestorCodi, usuariCodi, filtre);
	}

	@Override
	@RolesAllowed("**")
	public String marcarComProcessada(Long enviamentId, String motiu, boolean isAdministrador) throws Exception {
		return getDelegateService().marcarComProcessada(enviamentId, motiu, isAdministrador);
	}

	@Override
	@RolesAllowed("**")
	public RespostaAccio<String> enviarNotificacioARegistre(Long notificacioId, boolean retry) throws RegistreNotificaException {
		return getDelegateService().enviarNotificacioARegistre(notificacioId, retry);
	}

	@Override
	@RolesAllowed("**")
	public RespostaAccio<AccioMassivaElement> resetNotificacioARegistre(Long notificacioId) {
		return getDelegateService().resetNotificacioARegistre(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public List<ProvinciesDto> llistarProvincies() {
		return getDelegateService().llistarProvincies();
	}

	@Override
	@RolesAllowed("**")
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		return getDelegateService().llistarLocalitats(codiProvincia);
	}

	@Override
	@RolesAllowed("**")
	public List<PaisosDto> llistarPaisos() {
		return getDelegateService().llistarPaisos();
	}

	@SuppressWarnings("rawtypes")
	@Override
	@PermitAll
	public List<Long> getNotificacionsPendentsRegistrar() {
		return getDelegateService().getNotificacionsPendentsRegistrar();
	}

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
	@PermitAll
	public Boolean enviamentRefrescarEstatSir(Long enviamentId) {
		return getDelegateService().enviamentRefrescarEstatSir(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<NotificacioDto> findWithCallbackError(NotificacioErrorCallbackFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findWithCallbackError(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public boolean reactivarConsulta(Long notificacioId) {
		return getDelegateService().reactivarConsulta(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public boolean reactivarSir(Long notificacioId) {
		return getDelegateService().reactivarSir(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public boolean enviarEntregaPostal(String uuid, boolean retry) {
		return getDelegateService().enviarEntregaPostal(uuid, retry);
	}

	@Override
	@RolesAllowed("**")
	public boolean cancelarEntregaPostal(Long enviamentId) {
		return getDelegateService().cancelarEntregaPostal(enviamentId);
	}

	@Override
	@RolesAllowed("**")
	public boolean consultarEstatEntregaPostal(Long enviamentId) {
		return getDelegateService().consultarEstatEntregaPostal(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_ADMIN_LECTURA"})
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre, PaginacioParamsDto paginacioDtoFromRequest) {
		return getDelegateService().findNotificacionsAmbErrorRegistre(entitatId, filtre, paginacioDtoFromRequest);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_ADMIN_LECTURA"})
	public List<Long> findNotificacionsIdAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre) {
		return getDelegateService().findNotificacionsIdAmbErrorRegistre(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void reactivarRegistre(Long notificacioId) {
		getDelegateService().reactivarRegistre(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void reenviarNotificaionsMovil(Long notificacioId) {
		getDelegateService().reenviarNotificaionsMovil(notificacioId);
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
	@RolesAllowed("**")
	public List<OrganGestorDto> cercaUnitats(Long entitatId, String codi, String denominacio, Long nivellAdministracio,
											 Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) {
		return getDelegateService().cercaUnitats(entitatId, codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
	}


	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> unitatsPerCodi(String codi) {
		return getDelegateService().unitatsPerCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) {
		return getDelegateService().unitatsPerDenominacio(denominacio);

	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorDto> llistarNivellsAdministracions() {
		return getDelegateService().llistarNivellsAdministracions();
	}

	@Override
	@RolesAllowed("**")
	public List<CodiValorDto> llistarComunitatsAutonomes() {
		return getDelegateService().llistarComunitatsAutonomes();
	}

	@Override
	@RolesAllowed("**")
	public List<ProvinciesDto> llistarProvincies(String codiCA) {
		return getDelegateService().llistarProvincies(codiCA);
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid) {
		return getDelegateService().consultaDocumentIMetadades(identificador, esUuid);
	}

	@Override
	@RolesAllowed("**")
	public int getMaxAccionesMassives() {
		return getDelegateService().getMaxAccionesMassives();
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public boolean reenviarNotificacioAmbErrors(Long notificacioId) {
		return getDelegateService().reenviarNotificacioAmbErrors(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public RespostaAccio<AccioMassivaElement> reactivarNotificacioAmbErrors(Set<Long> notificacioId) {
		return getDelegateService().reactivarNotificacioAmbErrors(notificacioId);
	}

	@Override
	public boolean reactivarNotificacioAmbErrors(Long notificacioId) {
		return getDelegateService().reactivarNotificacioAmbErrors(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public SignatureInfoDto checkIfSignedAttached(byte[] contingut, String nom, String contentType) {
		return getDelegateService().checkIfSignedAttached(contingut, nom, contentType);
	}

	@Override
	@RolesAllowed("**")
	public DocCieValid validateDocCIE(byte[] bytes) throws IOException {
		return getDelegateService().validateDocCIE(bytes);
	}

	@Override
	@RolesAllowed("**")
    public void updateEstatList(Long notificacioId) {
        getDelegateService().updateEstatList(notificacioId);
    }

	@Override
	@RolesAllowed("**")
	public RespuestaAmpliarPlazoOE ampliacionPlazoOE(AmpliacionPlazoDto dto) {
		return getDelegateService().ampliacionPlazoOE(dto);
	}

	@Override
	@RolesAllowed("**")
	public Date getCaducitat(Long notificacioId) {
		return getDelegateService().getCaducitat(notificacioId);
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