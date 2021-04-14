/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.rest.consulta.*;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EnviamentServiceImpl implements EnviamentService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ColumnesRepository columnesRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private CallbackHelper callbackHelper;
	@Autowired
	private OrganigramaHelper organigramaHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private NotificacioService notificacioService;

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els destinataris d'una notificació (" +
					"notificacioId=" + notificacioId + ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
			NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
			List<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findByNotificacio(notificacio);
			return conversioTipusHelper.convertirList(enviaments, NotificacioEnviamentDatatableDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Override
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException  {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant els ids d'expedient segons el filtre ("
					+ "entitatId=" + entitatId + ", "
					+ "filtre=" + filtre + ")");
			entityComprovarHelper.comprovarPermisos(
					entitatId,
					false,
					false,
					false);
			return findIdsAmbFiltrePaginat(
					entitatId,
					filtre);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			NotificacioEnviamentFiltreDto filtre) throws ParseException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els enviaments de les notificacións que te una entitat");
			Date dataEnviamentInici = null,
				 dataEnviamentFi = null,
				 dataProgramadaDisposicioInici = null,
				 dataProgramadaDisposicioFi = null,
				 dataRegistreInici = null,
				 dataRegistreFi = null,
				 dataCaducitatInici = null,
				 dataCaducitatFi = null;
			
			EntitatEntity entitatEntity = entitatRepository.findOne(entitatId);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
			
			if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
				dataEnviamentInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici());
				if (dataEnviamentInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataEnviamentInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataEnviamentInici = cal.getTime();
				}
			}
			if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
				dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
				if (dataEnviamentFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataEnviamentFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataEnviamentFi = cal.getTime();
				}
			}
			if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
				dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
				if (dataProgramadaDisposicioInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataProgramadaDisposicioInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataProgramadaDisposicioInici = cal.getTime();
				}
			}
			if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
				dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
				if (dataProgramadaDisposicioFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataProgramadaDisposicioFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataProgramadaDisposicioFi = cal.getTime();
				}
			}
			if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
				dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
				if (dataRegistreInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataRegistreInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataRegistreInici = cal.getTime();
				}
			}
			if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
				dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
				if (dataRegistreFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataRegistreFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataRegistreFi = cal.getTime();
				}
			}
			if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
				dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
				if (dataCaducitatInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataCaducitatInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataCaducitatInici = cal.getTime();
				}
			}
			if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
				dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
				if (dataCaducitatFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataCaducitatFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataCaducitatFi = cal.getTime();
				}
			}
			//Filtres camps procediment
			Integer estat = null;
			Integer tipusEnviament = null;
			if(filtre.getEstat()!=null){
				estat = filtre.getEstat().getNumVal();
			}else{
				estat = 0;
			}
			if(filtre.getEnviamentTipus()!=null){
				tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
			}else{
				tipusEnviament = 0;
			}
			List<NotificacioEnviamentEntity> enviament = null;
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
				
				enviament = notificacioEnviamentRepository.findByNotificacio(
						filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
						filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
						filtre.getGrup() == null || filtre.getGrup().isEmpty(),
						filtre.getGrup() == null ? "" : filtre.getGrup(),
						filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
						filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
						(dataProgramadaDisposicioInici == null),
						dataProgramadaDisposicioInici,
						(dataProgramadaDisposicioFi == null),
						dataProgramadaDisposicioFi,
						(dataCaducitatInici == null),
						dataCaducitatInici,
						(dataCaducitatFi == null),
						dataCaducitatFi,
						(filtre.getEnviamentTipus() == null),
						(tipusEnviament),
						(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
						filtre.getCsvUuid(),
						(filtre.getEstat() == null),
						(estat),
						filtre.getEstat() != null ? NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()) : null,
						entitatEntity,
						(dataEnviamentInici == null),
						dataEnviamentInici,
						(dataEnviamentFi == null),
						dataEnviamentFi,
						(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
						filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
						(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
						conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
						(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
						filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
						(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
						filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
						(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
						filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
						(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
						filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
						(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
						filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
						(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
						filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
						(dataRegistreInici == null),
						dataRegistreInici,
						(dataRegistreFi == null),
						dataRegistreFi);
				
			List<Long> enviamentIds = new ArrayList<Long>();
			for(NotificacioEnviamentEntity nee: enviament) {
				enviamentIds.add(nee.getId());
//				nee.setNotificacio(notificacioRepository.findById(nee.getNotificacioId()));
			}
			return enviamentIds;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de destinatari donat el seu id (" +
					"destinatariId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
			//NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
			entityComprovarHelper.comprovarPermisos(
					null,
					false,
					false,
					false);
			return enviamentToDto(enviament);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(
			EntitatDto entitat,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdminOrgan,
			List<String> procedimentsCodisNotib,
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
			String organGestorCodi,
			String usuariCodi,
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws ParseException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els enviaments de les notificacións que te una entitat");
			
			boolean esProcedimentsCodisNotibNull = (procedimentsCodisNotib == null || procedimentsCodisNotib.isEmpty());
			boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			boolean esProcedimentsOrgansCodisNotibNull = (codisProcedimentOrgansDisponibles == null || codisProcedimentOrgansDisponibles.isEmpty());

			Date dataEnviamentInici = null,
				 dataEnviamentFi = null,
				 dataProgramadaDisposicioInici = null,
				 dataProgramadaDisposicioFi = null,
				 dataRegistreInici = null,
				 dataRegistreFi = null,
				 dataCaducitatInici = null,
				 dataCaducitatFi = null;
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitat.getId());
			
			if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
				dataEnviamentInici = toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici()));
			}
			if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
				dataEnviamentFi = toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi()));
			}
			if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
				dataProgramadaDisposicioInici = toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici()));
			}
			if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
				dataProgramadaDisposicioFi = toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi()));
			}
			if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
				dataRegistreInici = toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici()));
			}
			if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
				dataRegistreFi = toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi()));
			}
			if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
				dataCaducitatInici = toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici()));
			}
			if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
				dataCaducitatFi = toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi()));
			}

			NotificacioEstatEnumDto estat = filtre.getEstat();
			Boolean hasZeronotificaEnviamentIntent = null;
			boolean isEstatNull = estat == null;
			boolean nomesSenseErrors = false;
			boolean nomesAmbErrors = false;
			if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.ENVIANT)) {
				estat = NotificacioEstatEnumDto.PENDENT;
				hasZeronotificaEnviamentIntent = true;
				nomesSenseErrors = true;

			} else if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.PENDENT)) {
				hasZeronotificaEnviamentIntent = false;
			}

			//Filtres camps procediment
			int tipusEnviament;
			if(filtre.getEnviamentTipus()!=null){
				tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
			}else{
				tipusEnviament = 0;
			}
			Page<NotEnviamentTableItemDto> pageEnviaments = null;
			
//			campsOrdre(paginacioParams);
			logger.info("Consulta de taula d'enviaments ...");

			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("enviamentDataProgramada", new String[] {"n.enviamentDataProgramada"});
			mapeigPropietatsOrdenacio.put("notificaIdentificador", new String[] {"nenv.notificaIdentificador"});
			mapeigPropietatsOrdenacio.put("procedimentCodiNotib", new String[] {"n.procedimentCodiNotib"});
			mapeigPropietatsOrdenacio.put("grupCodi", new String[] {"n.grupCodi"});
			mapeigPropietatsOrdenacio.put("emisorDir3Codi", new String[] {"n.emisorDir3Codi"});
			mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"n.usuariCodi"});
			mapeigPropietatsOrdenacio.put("concepte", new String[] {"n.concepte"});
			mapeigPropietatsOrdenacio.put("descripcio", new String[] {"n.descripcio"});
			mapeigPropietatsOrdenacio.put("titularNif", new String[] {"t.nif"});
			mapeigPropietatsOrdenacio.put("titularNomLlinatge", new String[] {"concat(t.llinatge1, t.llinatge2, t.nom)"});
			mapeigPropietatsOrdenacio.put("titularEmail", new String[] {"t.email"});
//			mapeigPropietatsOrdenacio.put("destinatarisNomLlinatges", new String[] {"createdBy"});
			mapeigPropietatsOrdenacio.put("llibre", new String[] {"n.registreLlibreNom"});
			mapeigPropietatsOrdenacio.put("registreNumero", new String[] {"n.registreNumero"});
			mapeigPropietatsOrdenacio.put("notificaDataCaducitat", new String[] {"nenv.notificaDataCaducitat"});
			mapeigPropietatsOrdenacio.put("notificaCertificacioNumSeguiment", new String[] {"nenv.notificaCertificacioNumSeguiment"});
			mapeigPropietatsOrdenacio.put("csvUuid", new String[] {"concat(d.uuid, d.csv)"});
			mapeigPropietatsOrdenacio.put("estat", new String[] {"n.estat"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
//			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);

			if (isUsuari) { // && !procedimentsCodisNotib.isEmpty()) {
				pageEnviaments = notificacioEnviamentRepository.findByNotificacio(
						filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
						filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
						filtre.getGrup() == null || filtre.getGrup().isEmpty(),
						filtre.getGrup() == null ? "" : filtre.getGrup(),
						filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
						filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
						(dataProgramadaDisposicioInici == null),
						dataProgramadaDisposicioInici,
						(dataProgramadaDisposicioFi == null),
						dataProgramadaDisposicioFi,
						(dataCaducitatInici == null),
						dataCaducitatInici,
						(dataCaducitatFi == null),
						dataCaducitatFi,
						(filtre.getEnviamentTipus() == null),
						(tipusEnviament),
						(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
						filtre.getCsvUuid(),
						estat == null,
						estat == null ? 0 : estat.getNumVal(),
						estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
						entitatEntity,
						(dataEnviamentInici == null),
						dataEnviamentInici,
						(dataEnviamentFi == null),
						dataEnviamentFi,
						(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
						filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
						(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
						conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
						(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
						filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
						(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
						filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
						(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
						filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
						(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
						filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
						(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
						filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
						(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
						filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
						(dataRegistreInici == null),
						dataRegistreInici,
						(dataRegistreFi == null),
						dataRegistreFi,
						esProcedimentsCodisNotibNull,
						esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
						esOrgansGestorsCodisNotibNull,
						esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
						esProcedimentsOrgansCodisNotibNull,
						esProcedimentsOrgansCodisNotibNull ? null : codisProcedimentOrgansDisponibles,
						aplicacioService.findRolsUsuariActual(),
						usuariCodi,
						nomesAmbErrors,
						nomesSenseErrors,
						hasZeronotificaEnviamentIntent == null,
						hasZeronotificaEnviamentIntent,
						pageable);
			} else if (isAdminOrgan) { // && !procedimentsCodisNotib.isEmpty()) {
				List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatEntity.getDir3Codi(), organGestorCodi);
				pageEnviaments = notificacioEnviamentRepository.findByNotificacio(
						filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
						filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
						filtre.getGrup() == null || filtre.getGrup().isEmpty(),
						filtre.getGrup() == null ? "" : filtre.getGrup(),
						filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
						filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
						(dataProgramadaDisposicioInici == null),
						dataProgramadaDisposicioInici,
						(dataProgramadaDisposicioFi == null),
						dataProgramadaDisposicioFi,
						(dataCaducitatInici == null),
						dataCaducitatInici,
						(dataCaducitatFi == null),
						dataCaducitatFi,
						(filtre.getEnviamentTipus() == null),
						(tipusEnviament),
						(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
						filtre.getCsvUuid(),
						(estat == null),
						estat == null ? 0 : estat.getNumVal(),
						estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
						entitatEntity,
						(dataEnviamentInici == null),
						dataEnviamentInici,
						(dataEnviamentFi == null),
						dataEnviamentFi,
						(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
						filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
						(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
						conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
						(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
						filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
						(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
						filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
						(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
						filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
						(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
						filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
						(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
						filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
						(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
						filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
						(dataRegistreInici == null),
						dataRegistreInici,
						(dataRegistreFi == null),
						dataRegistreFi,
						esProcedimentsCodisNotibNull,
						esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
						organs,
						nomesAmbErrors,
						nomesSenseErrors,
						hasZeronotificaEnviamentIntent == null,
						hasZeronotificaEnviamentIntent,
						pageable);
			} else if (isUsuariEntitat) {
				boolean isNullProcediment = filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty();
				boolean isNullGrup = filtre.getGrup() == null || filtre.getGrup().isEmpty();
				boolean isNullConcepte = filtre.getConcepte() == null || filtre.getConcepte().isEmpty();
				boolean isNullDescripcio = filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty();
				boolean isNullCsvUuid = filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty();
				boolean isNullCodiNotifica = filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty();
				boolean isNullCreatedBy = filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty();
				boolean isNullTitularNif = filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty();
				boolean isNullTitularNom = filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty();
				boolean isNullTitularEmail = filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty();
				boolean isNullDir3Codi = filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty();
				boolean isNullNumeroCertCorreus = filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty();
				boolean isNullUsuari = filtre.getUsuari() == null || filtre.getUsuari().isEmpty();
				boolean isNullNumeroRegistre = filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty();
				logger.info("--------------");
				logger.info("--------------");
				logger.info("--------------");
				logger.info("Inici Consulta");
				logger.info("--------------");
				logger.info("--------------");
				logger.info("--------------");
				long ti = System.nanoTime();
				pageEnviaments = notificacioEnviamentRepository.findByNotificacio(
						isNullProcediment,
						filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
						isNullGrup,
						filtre.getGrup() == null ? "" : filtre.getGrup(),
						isNullConcepte,
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						isNullDescripcio,
						filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
						dataProgramadaDisposicioInici == null,
						dataProgramadaDisposicioInici,
						dataProgramadaDisposicioFi == null,
						dataProgramadaDisposicioFi,
						dataCaducitatInici == null,
						dataCaducitatInici,
						dataCaducitatFi == null,
						dataCaducitatFi,
						filtre.getEnviamentTipus() == null,
						(tipusEnviament),
						isNullCsvUuid,
						filtre.getCsvUuid(),
						estat == null,
						estat == null ? 0 : estat.getNumVal(),
						estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
						entitatEntity,
						dataEnviamentInici == null,
						dataEnviamentInici,
						dataEnviamentFi == null,
						dataEnviamentFi,
						isNullCodiNotifica,
						filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
						isNullCreatedBy,
						isNullCreatedBy ? "" : filtre.getCreatedBy().getCodi(),
						isNullTitularNif,
						filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
						isNullTitularNom,
						filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
						isNullTitularEmail,
						filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
						isNullDir3Codi,
						filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
						isNullNumeroCertCorreus,
						filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
						isNullUsuari,
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						isNullNumeroRegistre,
						filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
						dataRegistreInici == null,
						dataRegistreInici,
						dataRegistreFi == null,
						dataRegistreFi,
						nomesAmbErrors,
						nomesSenseErrors,
						hasZeronotificaEnviamentIntent == null,
						hasZeronotificaEnviamentIntent,
						pageable);
				logger.info("--------------");
				logger.info("--------------");
				logger.info("Fi Consulta");
				logger.info(String.format("Time spent: %f ms", (System.nanoTime() - ti) / 1e6));
			}
			if(pageEnviaments == null || !pageEnviaments.hasContent()) {
				pageEnviaments = new PageImpl<>(new ArrayList<NotEnviamentTableItemDto>());
			}

			PaginaDto<NotEnviamentTableItemDto> paginaDto = paginacioHelper.toPaginaDto(pageEnviaments);

			// TODO: això ho podriem afefir a la consulta amb un CASE isLlibreEntitat THEN llibreEntitat ELSE llibreCamp
			if (entitat.isLlibreEntitat()) {
				for (NotEnviamentTableItemDto tableItem : paginaDto.getContingut()) {
						tableItem.setLlibre(entitatEntity.getLlibre());
				}
			}

			for (NotEnviamentTableItemDto tableItem : paginaDto.getContingut()) {
				List<PersonaEntity> destinataris = personaRepository.findByEnviamentId(tableItem.getId());
				List<NotEnviamentTableItemDto.DestinatariDto> destinatarisDto = new ArrayList<>();
				for (PersonaEntity destinatari : destinataris) {
					destinatarisDto.add(new NotEnviamentTableItemDto.DestinatariDto(
							destinatari.getNif(),
							destinatari.getNom(),
							destinatari.getEmail(),
							destinatari.getLlinatge1(),
							destinatari.getLlinatge2(),
							destinatari.getRaoSocial()
					));
				}
				tableItem.setDestinataris(destinatarisDto);
			}

			return paginaDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private Date toIniciDia(Date data) {
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			data = cal.getTime();
		}
		return data;
	}
	
	private Date toFiDia(Date data) {
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			data = cal.getTime();
		}
		return data;
	}
	
//	private void campsOrdre(PaginacioParamsDto paginacioParams) {
//		PaginacioParamsDto paginacioParamsNou = paginacioParams;
//
//		OrdreDto ordreAntic = paginacioParams.getOrdres().get(0);
//		OrdreDto ordrenNou = null;
//
//		switch (ordreAntic.getCamp()) {
//		case "procedimentCodiNotib":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.procedimentCodiNotib", ordreAntic.getDireccio());
//			break;
//		case "enviamentDataProgramada":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.enviamentDataProgramada", ordreAntic.getDireccio());
//			break;
//		case "grupCodi":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.grupCodi", ordreAntic.getDireccio());
//			break;
//		case "emisorDir3Codi":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.emisorDir3Codi", ordreAntic.getDireccio());
//			break;
//		case "usuariCodi":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.usuariCodi", ordreAntic.getDireccio());
//			break;
//		case "enviamentTipus":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.enviamentTipus", ordreAntic.getDireccio());
//			break;
//		case "concepte":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.concepte", ordreAntic.getDireccio());
//			break;
//		case "descripcio":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.descripcio", ordreAntic.getDireccio());
//			break;
//		case "llibre":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.llibre", ordreAntic.getDireccio());
//			break;
//		case "registreNumero":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.registreNumero", ordreAntic.getDireccio());
//			break;
//		case "estat":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.estat", ordreAntic.getDireccio());
//			break;
//		case "comunicacioTipus":
//			ordrenNou = paginacioParamsNou.new OrdreDto("notificacio.comunicacioTipus", ordreAntic.getDireccio());
//			break;
//		case "titularNomLlinatge":
//			ordrenNou = paginacioParamsNou.new OrdreDto("titular.nom", ordreAntic.getDireccio());
//			break;
//		case "destinatarisNomLlinatges":
//			ordrenNou = paginacioParamsNou.new OrdreDto("destinataris.get(0).llinatge1", ordreAntic.getDireccio());
//			break;
//		default:
//			ordrenNou = paginacioParamsNou.new OrdreDto(ordreAntic.getCamp(), ordreAntic.getDireccio());;
//			break;
//		}
//		paginacioParams.getOrdres().set(0, ordrenNou);
//	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> enviamentIds,
			String format,
			NotificacioEnviamentFiltreDto filtre) throws IOException, ParseException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Exportant informació dels enviaments (" +
					"entitatId=" + entitatId + ", " +
					"enviamentsIds=" + enviamentIds + ", " +
					"format=" + format + ")");
				Date dataEnviamentInici = null,
				 dataEnviamentFi = null,
				 dataProgramadaDisposicioInici = null,
				 dataProgramadaDisposicioFi = null,
				 dataRegistreInici = null,
				 dataRegistreFi = null,
				 dataCaducitatInici = null,
				 dataCaducitatFi = null;
			
			EntitatEntity entitatEntity = entitatRepository.findOne(entitatId);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
			
			if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
				dataEnviamentInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici());
				if (dataEnviamentInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataEnviamentInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataEnviamentInici = cal.getTime();
				}
			}
			if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
				dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
				if (dataEnviamentFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataEnviamentFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataEnviamentFi = cal.getTime();
				}
			}
			if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
				dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
				if (dataProgramadaDisposicioInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataProgramadaDisposicioInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataProgramadaDisposicioInici = cal.getTime();
				}
			}
			if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
				dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
				if (dataProgramadaDisposicioFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataProgramadaDisposicioFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataProgramadaDisposicioFi = cal.getTime();
				}
			}
			if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
				dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
				if (dataRegistreInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataRegistreInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataRegistreInici = cal.getTime();
				}
			}
			if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
				dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
				if (dataRegistreFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataRegistreFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataRegistreFi = cal.getTime();
				}
			}
			if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
				dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
				if (dataCaducitatInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataCaducitatInici);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataCaducitatInici = cal.getTime();
				}
			}
			if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
				dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
				if (dataCaducitatFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataCaducitatFi);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataCaducitatFi = cal.getTime();
				}
			}
			//Filtres camps procediment
			Integer estat = null;
			Integer tipusEnviament = null;
			if(filtre.getEstat()!=null){
				estat = filtre.getEstat().getNumVal();
			}else{
				estat = 0;
			}
			if(filtre.getEnviamentTipus()!=null){
				tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
			}else{
				tipusEnviament = 0;
			}
			List<NotificacioEnviamentEntity> enviaments = null;
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
				
			enviaments = notificacioEnviamentRepository.findByNotificacio(
					filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
					filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
					filtre.getGrup() == null || filtre.getGrup().isEmpty(),
					filtre.getGrup() == null ? "" : filtre.getGrup(),
					filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
					filtre.getConcepte() == null ? "" : filtre.getConcepte(),
					filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
					filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
					(dataProgramadaDisposicioInici == null),
					dataProgramadaDisposicioInici,
					(dataProgramadaDisposicioFi == null),
					dataProgramadaDisposicioFi,
					(dataCaducitatInici == null),
					dataCaducitatInici,
					(dataCaducitatFi == null),
					dataCaducitatFi,
					(filtre.getEnviamentTipus() == null),
					(tipusEnviament),
					(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
					filtre.getCsvUuid(),
					(filtre.getEstat() == null),
					(estat),
					filtre.getEstat() != null ? NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()) : null,
					entitatEntity,
					(dataEnviamentInici == null),
					dataEnviamentInici,
					(dataEnviamentFi == null),
					dataEnviamentFi,
					(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
					filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
					(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
					conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
					(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
					filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
					(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
					filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
					(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
					filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
					(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
					filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
					(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
					filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
					(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
					filtre.getUsuari() == null ? "" : filtre.getUsuari(),
					(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
					filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
					(dataRegistreInici == null),
					dataRegistreInici,
					(dataRegistreFi == null),
					dataRegistreFi);
			
//			for(NotificacioEnviamentEntity nee: enviaments) {
//				nee.setNotificacio(notificacioRepository.findById(nee.getNotificacioId()));
//			}
			
			//Genera les columnes
			int numColumnes = 22;
			String[] columnes = new String[numColumnes];
			columnes[0] = messageHelper.getMessage("enviament.service.exportacio.dataenviament");
			columnes[1] = messageHelper.getMessage("enviament.service.exportacio.dataprogramada");
			columnes[2] = messageHelper.getMessage("enviament.service.exportacio.codinotifica");
			columnes[3] = messageHelper.getMessage("enviament.service.exportacio.codiprocediment");
			columnes[4] = messageHelper.getMessage("enviament.service.exportacio.codigrup");
			columnes[5] = messageHelper.getMessage("enviament.service.exportacio.dir3codi");
			columnes[6] = messageHelper.getMessage("enviament.service.exportacio.usuari");
			columnes[7] = messageHelper.getMessage("enviament.service.exportacio.tipusenviament");
			columnes[8] = messageHelper.getMessage("enviament.service.exportacio.concepte");
			columnes[9] = messageHelper.getMessage("enviament.service.exportacio.descripcio");
			columnes[10] = messageHelper.getMessage("enviament.service.exportacio.niftitular");
			columnes[11] = messageHelper.getMessage("enviament.service.exportacio.nomLlinatgetitular");
			columnes[12] = messageHelper.getMessage("enviament.service.exportacio.emailtitular");
			columnes[13] = messageHelper.getMessage("enviament.service.exportacio.destinataris");
			columnes[14] = messageHelper.getMessage("enviament.service.exportacio.llibreregistre");
			columnes[15] = messageHelper.getMessage("enviament.service.exportacio.numeroregistre");
			columnes[16] = messageHelper.getMessage("enviament.service.exportacio.dataregistre");
			columnes[17] = messageHelper.getMessage("enviament.service.exportacio.datacaducitat");
			columnes[18] = messageHelper.getMessage("enviament.service.exportacio.codinotibenviament");
			columnes[19] = messageHelper.getMessage("enviament.service.exportacio.numerocertificatcorreus");
			columnes[20] = messageHelper.getMessage("enviament.service.exportacio.codicsvuuid");
			columnes[21] = messageHelper.getMessage("enviament.service.exportacio.estat");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			List<String[]> files = new ArrayList<String[]>();
			boolean llibreOrgan = !entitatEntity.isLlibreEntitat();
			
			for (NotificacioEnviamentEntity enviament : enviaments) {
				String[] fila = new String[numColumnes];
				if(enviamentIds.contains(enviament.getId())) {
					String csvUuid = "";
					if(enviament.getNotificacio().getDocument().getCsv() != null) {
						csvUuid = enviament.getNotificacio().getDocument().getCsv();
					}
					if(enviament.getNotificacio().getDocument().getUuid() != null) {
						csvUuid = enviament.getNotificacio().getDocument().getUuid();
					}
					
					
					fila[0] = enviament.getCreatedDate().toDate() != null ? sdf.format(enviament.getCreatedDate().toDate()) : "";
					fila[1] = enviament.getNotificacio().getEnviamentDataProgramada() != null ? sdf.format(enviament.getNotificacio().getEnviamentDataProgramada()) : "";
					fila[2] = enviament.getNotificaIdentificador();
					fila[3] = enviament.getNotificacio().getProcedimentCodiNotib();
					fila[4] = enviament.getNotificacio().getGrupCodi();
					fila[5] = enviament.getNotificacio().getEmisorDir3Codi();
					fila[6] = enviament.getCreatedBy().getCodi();
					fila[7] = enviament.getNotificacio().getEnviamentTipus().getText();
					fila[8] = enviament.getNotificacio().getConcepte();
					fila[9] = enviament.getNotificacio().getDescripcio();
					fila[10] = enviament.getTitular().getNif();
					fila[11] = enviament.getTitular().getNom();
					fila[12] = enviament.getTitular().getEmail();
					fila[13] = (enviament.getDestinataris().size() > 0) ? enviament.getDestinataris().get(0).getNif() : null;
					if (llibreOrgan) {
						if (enviament.getNotificacio().getProcediment() != null)
							fila[14] = enviament.getNotificacio().getProcediment().getOrganGestor().getLlibre();
					} else {
						fila[14] = entitatEntity.getLlibre();
					}
					fila[15] = String.valueOf(enviament.getNotificacio().getRegistreNumero());
					fila[16] = (enviament.getNotificacio().getRegistreData() != null)? enviament.getNotificacio().getRegistreData().toString() : "";
					fila[17] = enviament.getNotificacio().getCaducitat() != null ? sdf.format(enviament.getNotificacio().getCaducitat()) : "";
					fila[19] = enviament.getNotificaCertificacioNumSeguiment();
					fila[20] = csvUuid;
					fila[21] = enviament.getNotificacio().getEstat().name();	
					
					files.add(fila);	
				}
			}
				
			FitxerDto fitxer = new FitxerDto();
			if ("ODS".equalsIgnoreCase(format)) {
				Object[][] filesArray = files.toArray(new Object[files.size()][numColumnes]);
				TableModel model = new DefaultTableModel(filesArray, columnes);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				SpreadSheet.createEmpty(model).getPackage().save(baos);
				fitxer.setNom("exportacio.ods");
				fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
				fitxer.setContingut(baos.toByteArray());
			} else {
				throw new ValidationException("Format de fitxer no suportat: " + format);
			}
			return fitxer;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public void columnesCreate(
			UsuariDto usuari,
			Long entitatId, 
			ColumnesDto columnes) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId);
			UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
			
			if (columnes == null) {
				columnes = new ColumnesDto();
				
				columnes.setDataEnviament(true);
				columnes.setCodiNotibEnviament(true);
				columnes.setProCodi(true);
				columnes.setGrupCodi(true);
				columnes.setEnviamentTipus(true);
				columnes.setConcepte(true);
				columnes.setTitularNif(true);
				columnes.setTitularNomLlinatge(true);
			}
			// Dades generals de la notificació
			ColumnesEntity.Builder columnesBuilder = ColumnesEntity.getBuilder(
					columnes.isDataEnviament(),
					columnes.isDataProgramada(), 
					columnes.isNotIdentificador(), 
					columnes.isProCodi(),
					columnes.isGrupCodi(),
					columnes.isDir3Codi(), 
					columnes.isUsuari(), 
					columnes.isEnviamentTipus(), 
					columnes.isConcepte(),
					columnes.isDescripcio(), 
					columnes.isTitularNif(), 
					columnes.isTitularNomLlinatge(),
					columnes.isTitularEmail(),
					columnes.isDestinataris(),
					columnes.isLlibreRegistre(),
					columnes.isNumeroRegistre(), 
					columnes.isDataRegistre(), 
					columnes.isDataCaducitat(),
					columnes.isCodiNotibEnviament(), 
					columnes.isNumCertificacio(), 
					columnes.isCsvUuid(), 
					columnes.isEstat(),
					entitatEntity,
					usuariEntity);
	
			ColumnesEntity columnesEntity = columnesBuilder.build();
			columnesRepository.saveAndFlush(columnesEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void columnesUpdate(
			Long entitatId, 
			ColumnesDto columnes) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			ColumnesEntity columnesEntity = columnesRepository.findOne(columnes.getId());
	
			columnesEntity.update(
					columnes.isDataEnviament(), 
					columnes.isDataProgramada(), 
					columnes.isNotIdentificador(),
					columnes.isProCodi(), 
					columnes.isGrupCodi(), 
					columnes.isDir3Codi(), 
					columnes.isUsuari(),
					columnes.isEnviamentTipus(), 
					columnes.isConcepte(), 
					columnes.isDescripcio(), 
					columnes.isTitularNif(),
					columnes.isTitularNomLlinatge(), 
					columnes.isTitularEmail(), 
					columnes.isDestinataris(),
					columnes.isLlibreRegistre(), 
					columnes.isNumeroRegistre(), 
					columnes.isDataRegistre(),
					columnes.isDataCaducitat(), 
					columnes.isCodiNotibEnviament(), 
					columnes.isNumCertificacio(),
					columnes.isCsvUuid(), 
					columnes.isEstat());
	
			columnesRepository.saveAndFlush(columnesEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
		
	@Transactional(readOnly = true)	
	@Override
	public ColumnesDto getColumnesUsuari(
			Long entitatId,
			UsuariDto usuariDto) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			UsuariEntity usuari = usuariRepository.findByCodi(usuariDto.getCodi());
			
			ColumnesEntity columnes = columnesRepository.findByEntitatAndUser(
					entitat, 
					usuari);
			
			return conversioTipusHelper.convertir(
					columnes, 
					ColumnesDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels events de la notificació (" +
					"notificacioId=" + notificacioId + ")");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
			return conversioTipusHelper.convertirList(
					notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
					NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public boolean reintentarCallback(Long eventId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.info("Notificant canvi al client...");
			NotificacioEntity notificacio = callbackHelper.notifica(eventId); 
			return (notificacio != null && !notificacio.isErrorLastCallback());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void reactivaConsultes(Set<Long> enviaments) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			for (Long enviamentId: enviaments) {
				auditEnviamentHelper.reiniciaConsultaNotifica(notificacioEnviamentRepository.findById(enviamentId));
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void reactivaSir(Set<Long> enviaments) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			for (Long enviamentId: enviaments) {
				auditEnviamentHelper.reiniciaConsultaSir(notificacioEnviamentRepository.findById(enviamentId));
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
//	private List<NotificacioEnviamentDatatableDto> enviamentsToDto(
//			List<NotificacioEnviamentEntity> enviaments) {
//		List<NotificacioEnviamentDatatableDto> enviamentsDto = new ArrayList<>();
//		for(NotificacioEnviamentEntity enviament : enviaments) {
//			NotificacioEnviamentDatatableDto enviamentDto = conversioTipusHelper.convertir(
//					enviament,
//					NotificacioEnviamentDatatableDto.class);
//			if (enviament.isNotificaError()) {
//				NotificacioEventEntity event = enviament.getNotificacioErrorEvent();
//				if (event != null) {
//					enviamentDto.setNotificacioErrorData(event.getData());
//					enviamentDto.setNotificacioErrorDescripcio(event.getErrorDescripcio());
//				}
//			}
//			enviamentsDto.add(enviamentDto);
//		}
//		return enviamentsDto;
//	}

	private NotificacioEnviamentDto enviamentToDto(
			NotificacioEnviamentEntity enviament) {
//		enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacioId()));
		NotificacioEnviamentDto enviamentDto = conversioTipusHelper.convertir(
				enviament,
				NotificacioEnviamentDto.class);
		enviamentDto.setRegistreNumeroFormatat(enviament.getRegistreNumeroFormatat());
		enviamentDto.setRegistreData(enviament.getRegistreData());
		destinatariCalcularCampsAddicionals(
				enviament,
				enviamentDto);
		return enviamentDto;
	}

	private void destinatariCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamentDto enviamentDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificacioErrorEvent();
			if (event != null) {
				enviamentDto.setNotificaErrorData(event.getData());
				enviamentDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
			}
		}
		enviamentDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}
	
	@Transactional
	@Override
	public NotificacioEnviamentDtoV2 getOne(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertir(notificacioEnviamentRepository.findOne(entitatId), NotificacioEnviamentDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	public byte[] getDocumentJustificant(Long enviamentId) {
		
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
		
		if (enviament.getRegistreEstat() != null && enviament.getRegistreEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_EXTERN))
			return pluginHelper.obtenirOficiExtern(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();	
		else
			return pluginHelper.obtenirJustificant(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Resposta findEnviamentsByNif(
			String dniTitular,
			NotificaEnviamentTipusEnumDto tipus,
			Boolean estatFinal,
			String basePath, 
			Integer pagina, 
			Integer mida) {
		Integer numEnviaments = notificacioEnviamentRepository.countEnviamentsByNif(
				dniTitular.toUpperCase(),
				tipus,
				estatFinal == null,
				estatFinal);
		Page<NotificacioEnviamentEntity> comunicacions = notificacioEnviamentRepository.findEnviamentsByNif(
				dniTitular.toUpperCase(),
				tipus,
				estatFinal == null,
				estatFinal,
				getPageable(pagina, mida));
		Resposta resposta = new Resposta();
		resposta.setNumeroElementsTotals(numEnviaments);
		resposta.setNumeroElementsRetornats(comunicacions.getContent() != null ? comunicacions.getContent().size() : 0);
		
		List<NotificacioEnviamentDto> dtos = conversioTipusHelper.convertirList(comunicacions.getContent(), NotificacioEnviamentDto.class);
		resposta.setResultat(dtosToTransmissions(dtos, basePath));
		return resposta;
	}
	
	private Pageable getPageable(Integer pagina, Integer mida) {
		Pageable pageable = new PageRequest(0, 999999999);
		if (pagina != null && mida != null)
			pageable = new PageRequest(
					pagina,
					mida);
		return pageable;
	}
	
	
	private List<Transmissio> dtosToTransmissions(List<NotificacioEnviamentDto> enviaments, String basePath) {
		List<Transmissio> transmissions = new ArrayList<Transmissio>();
		if (enviaments != null) {
			for (NotificacioEnviamentDto enviament: enviaments) {
				transmissions.add(toTransmissio(enviament, basePath));
			}
		}
		return transmissions;
	}
	
	private Transmissio toTransmissio(NotificacioEnviamentDto enviament, String basePath) {
		Transmissio transmissio = new Transmissio();
		transmissio.setId(enviament.getId());
		transmissio.setEmisor(enviament.getNotificacio().getEntitat().getCodi());
		transmissio.setOrganGestor(enviament.getNotificacio().getOrganGestor());
		if (enviament.getNotificacio().getProcediment() != null)
			transmissio.setProcediment(enviament.getNotificacio().getProcediment().getCodi());
		transmissio.setNumExpedient(enviament.getNotificacio().getNumExpedient());
		transmissio.setConcepte(enviament.getNotificacio().getConcepte());
		transmissio.setDescripcio(enviament.getNotificacio().getDescripcio());
		transmissio.setDataEnviament(enviament.getNotificacio().getEnviamentDataProgramada());
		transmissio.setEstat(Estat.valueOf(enviament.getNotificacio().getEstat().name()));
		transmissio.setDataEstat(enviament.getNotificacio().getEstatDate());
		Document document = Document.builder()
				.nom(enviament.getNotificacio().getDocument().getArxiuNom())
				.mediaType(enviament.getNotificacio().getDocument().getMediaType())
				.mida(enviament.getNotificacio().getDocument().getMida())
				.url(basePath + "/document/" + enviament.getNotificacio().getId()).build();
		transmissio.setDocument(document);
		transmissio.setTitular(toPersona(enviament.getTitular()));
		List<Persona> destinataris = new ArrayList<Persona>();
		if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
			for (PersonaDto destinatari: enviament.getDestinataris()) {
				destinataris.add(toPersona(destinatari));
			}
		}
		transmissio.setDestinataris(destinataris);
		transmissio.setSubestat(SubEstat.valueOf(enviament.getNotificaEstat().name()));
		transmissio.setDataSubestat(enviament.getNotificaEstatData());

		transmissio.setError(enviament.isNotificaError());
		transmissio.setErrorData(enviament.getNotificaErrorData());
		transmissio.setErrorDescripcio(enviament.getNotificaErrorDescripcio());
		
		// Justificant de registre
		if (NotificacioEstatEnumDto.REGISTRADA.equals(enviament.getNotificacio().getEstat()) &&
			(enviament.getRegistreEstat() != null && 
				(NotificacioRegistreEstatEnumDto.DISTRIBUIT.equals(enviament.getRegistreEstat()) || 
				 NotificacioRegistreEstatEnumDto.OFICI_EXTERN.equals(enviament.getRegistreEstat()) ||
				 NotificacioRegistreEstatEnumDto.OFICI_SIR.equals(enviament.getRegistreEstat()) ) ||
				(enviament.getRegistreData() != null && enviament.getRegistreNumeroFormatat() != null && !enviament.getRegistreNumeroFormatat().isEmpty()))) {
			transmissio.setJustificant(basePath + "/justificant/" + enviament.getId());
		}
		
		// Certificació
		if (enviament.getNotificaCertificacioData() != null) {
			transmissio.setCertificacio(basePath + "/certificacio/" + enviament.getId());
		}
		
		return transmissio;
	}
	
	private Persona toPersona(PersonaDto dto) {
		Persona persona= new Persona();
		persona.setNom(dto.getNom());
		if (dto.getInteressatTipus() != null) {
			persona.setTipus(PersonaTipus.valueOf(dto.getInteressatTipus().name()));
			if (!InteressatTipusEnumDto.FISICA.equals(dto.getInteressatTipus())) {
				if (dto.getRaoSocial() != null && !dto.getRaoSocial().isEmpty()) {
					persona.setNom(dto.getRaoSocial());
				} else {
					persona.setNom(dto.getNom());
				}
			}
		}
		persona.setLlinatge1(dto.getLlinatge1());
		persona.setLlinatge2(dto.getLlinatge2());
		persona.setNif(dto.getNif());
		persona.setEmail(dto.getEmail());
		return persona;
	}

	@Override
	public void actualitzarEstat(Long enviamentId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		auditEnviamentHelper.reiniciaConsultaNotifica(enviament);
		auditEnviamentHelper.reiniciaConsultaSir(enviament);

		// si l'enviament esta pendent de refrescar estat a notifica
		if (enviament.isPendentRefrescarEstatNotifica())
			notificacioService.enviamentRefrescarEstat(enviamentId);

		// si l'enviament esta pendent de refrescar l'estat enviat SIR
		if (enviament.isPendentRefrescarEstatRegistre())
			notificacioService.enviamentRefrescarEstatRegistre(enviamentId);
	}

	private static final Logger logger = LoggerFactory.getLogger(EnviamentServiceImpl.class);

}
