package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.consulta.DocumentConsultaV2;
import es.caib.notib.client.domini.consulta.GenericInfo;
import es.caib.notib.client.domini.consulta.PersonaConsultaV2;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.client.domini.consulta.TransmissioV2;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.FiltreHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.OrganGestorHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.ProcSerHelper;
import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioTipusEnviamentEnumDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.exception.WriteCsvException;
import es.caib.notib.logic.intf.rest.consulta.Estat;
import es.caib.notib.logic.intf.rest.consulta.Persona;
import es.caib.notib.logic.intf.rest.consulta.PersonaTipus;
import es.caib.notib.logic.intf.rest.consulta.Resposta;
import es.caib.notib.logic.intf.rest.consulta.SubEstat;
import es.caib.notib.logic.intf.rest.consulta.Transmissio;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.filtres.FiltreEnviament;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.ColumnesRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class EnviamentServiceImpl implements EnviamentService {

	@Autowired
	private PermisosService permisosService;
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
	private EnviamentTableRepository enviamentTableRepository;
	@Autowired
	private NotificacioTableViewRepository notificacioTableRepository;
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
	private AuditHelper auditHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ProcSerHelper procedimentHelper;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificaHelper notificaHelper;

	@Autowired
	private EnviamentSmService enviamentSmService;

	private static final String CONSULTA_ENV_LOG = "Consulta els enviaments de les notificacións que te una entitat";
	private static final String FORMAT_DATA = "dd/MM/yyyy";
	private static final String FORMAT_DATA_HORA = "dd/MM/yyyy HH:mm:ss";

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els destinataris d'una notificació (notificacioId=" + notificacioId + ")");
			entityComprovarHelper.comprovarPermisos(null,true,true,true);
			var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
			var enviaments = notificacioEnviamentRepository.findByNotificacio(notificacio);
			var envs = conversioTipusHelper.convertirList(enviaments, NotificacioEnviamentDatatableDto.class);
			NotificacioEventEntity event;
			for (var env : envs) {
				event = notificacioEventRepository.findLastApiCarpetaByEnviamentId(env.getId());
				if (event != null && event.isError()) {
					env.setNotificacioMovilErrorDesc(event.getErrorDescripcio());
				}
			}
			return envs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Set<Long> findIdsByNotificacioIds(Collection<Long> notificacionsIds) {

		if (notificacionsIds == null || notificacionsIds.isEmpty()) {
			return Collections.emptySet();
		}
		// Oracle no permet fer consultes de més de 1000 elements en un .. in (..)
		List<Long> idsEnviaments = new ArrayList<>();
		var list = Lists.partition(new ArrayList<>(notificacionsIds), 999);
		for (var idsNotificacionsParcials : list){
			idsEnviaments.addAll(notificacioEnviamentRepository.findIdByNotificacioIdIn(idsNotificacionsParcials));
		}
		return new HashSet<>(idsEnviaments);
	}

	@Override
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String usuariCodi, String organGestorCodi, NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException  {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant els ids d'expedient segons el filtre (entitatId=" + entitatId + ", filtre=" + filtre + ")");
			entityComprovarHelper.comprovarPermisos(entitatId, false, false, false);
			log.debug(CONSULTA_ENV_LOG);
			log.info("Consulta ids d'enviament, accions massives");
			entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
			var f = getFiltre(entitatId, filtre, usuariCodi, rol, organGestorCodi);
			f.setDataEnviamentFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataEnviamentInici(), f.getDataEnviamentFi()));
			f.setDataProgramadaDisposicioFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataProgramadaDisposicioInici(), f.getDataProgramadaDisposicioFi()));
			f.setDataCaducitatFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataCaducitatInici(), f.getDataCaducitatFi()));
			return enviamentTableRepository.findIdsAmbFiltre(f);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, false, false, false);
			log.debug("Consulta de destinatari donat el seu id (destinatariId=" + enviamentId + ")");
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			// #779: Obtenim la certificació de forma automàtica
			if (enviament.getNotificaCertificacioArxiuId() == null &&
					( EnviamentEstat.REBUTJADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat()) )) {

				try {
					notificaHelper.enviamentRefrescarEstat(enviamentId);
					notificacioEnviamentRepository.flush();
					enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
				} catch (Exception ex) {
					log.error("No s'ha pogut actualitzar la certificació de l'enviament amb id: " + enviamentId, ex);
				}
			}
			return enviamentToDto(enviament);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioEnviamentFiltreDto filtre, PaginacioParamsDto paginacioParams) throws ParseException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug(CONSULTA_ENV_LOG);
			entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
			log.info("Consulta de taula d'enviaments ...");
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
			mapeigPropietatsOrdenacio.put("enviamentDataProgramada", new String[] {"enviamentDataProgramada"});
			mapeigPropietatsOrdenacio.put("notificaIdentificador", new String[] {"notificaIdentificador"});
			mapeigPropietatsOrdenacio.put("procedimentCodiNotib", new String[] {"procedimentCodiNotib"});
			mapeigPropietatsOrdenacio.put("grupCodi", new String[] {"grupCodi"});
			mapeigPropietatsOrdenacio.put("emisorDir3Codi", new String[] {"emisorDir3Codi"});
			mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
			mapeigPropietatsOrdenacio.put("concepte", new String[] {"concepte"});
			mapeigPropietatsOrdenacio.put("descripcio", new String[] {"descripcio"});
			mapeigPropietatsOrdenacio.put("titularNif", new String[] {"titularNif"});
			mapeigPropietatsOrdenacio.put("titularNomLlinatge", new String[] {"titularNomLlinatge"});
			mapeigPropietatsOrdenacio.put("titularEmail", new String[] {"titularEmail"});
			mapeigPropietatsOrdenacio.put("llibre", new String[] {"registreLlibreNom"});
			mapeigPropietatsOrdenacio.put("registreNumero", new String[] {"registreNumero"});
			mapeigPropietatsOrdenacio.put("notificaDataCaducitat", new String[] {"notificaDataCaducitat"});
			mapeigPropietatsOrdenacio.put("notificaCertificacioNumSeguiment", new String[] {"notificaCertificacioNumSeguiment"});
			mapeigPropietatsOrdenacio.put("csvUuid", new String[] {"csv_uuid"});
			mapeigPropietatsOrdenacio.put("estat", new String[] {"estat"});
			mapeigPropietatsOrdenacio.put("codiNotibEnviament", new String[] {"notificaReferencia"});
			mapeigPropietatsOrdenacio.put("referenciaNotificacio", new String[] {"referenciaNotificacio"});

			var f = getFiltre(entitatId, filtre, usuariCodi, rol, organGestorCodi);
			f.setDataEnviamentFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataEnviamentInici(), f.getDataEnviamentFi()));
			f.setDataCaducitatFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataCaducitatInici(), f.getDataCaducitatFi()));
			f.setDataProgramadaDisposicioFi(DatesUtils.incrementarDataFiSiMateixDia(f.getDataProgramadaDisposicioInici(), f.getDataProgramadaDisposicioFi()));
			setOrdresCampsCompostos(paginacioParams);
			var pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
 			var pageEnviaments = enviamentTableRepository.findAmbFiltre(f, pageable);
			if(pageEnviaments == null || !pageEnviaments.hasContent()) {
				pageEnviaments = new PageImpl<>(new ArrayList<>());
			}
			return paginacioHelper.toPaginaDto(pageEnviaments, NotEnviamentTableItemDto.class);
		} finally {
				metricsHelper.fiMetrica(timer);
		}
	}

	private void setOrdresCampsCompostos(PaginacioParamsDto paginacioParams) {

		var addOrdres = false;
		var direccio = PaginacioParamsDto.OrdreDireccioDto.DESCENDENT;
		for (var ordre : paginacioParams.getOrdres()) {

			if ("procedimentCodiNom".equals(ordre.getCamp())) {
				ordre.setCamp("procedimentCodiNotib");
				continue;
			}
			if ("organCodiNom".equals(ordre.getCamp())) {
				ordre.setCamp("emisorDir3Codi");
				continue;
			}
			if ("enviadaDate".equals(ordre.getCamp())) {
				ordre.setCamp("registreData");
				continue;
			}
			if ("titularNomLlinatge".equals(ordre.getCamp())) {
				ordre.setCamp("titularNom");
				addOrdres = true;
				direccio = ordre.getDireccio();
			}
		}
		if (!addOrdres) {
			return;
		}
		var ordre = new PaginacioParamsDto.OrdreDto("titularLlinatge1", direccio);
		paginacioParams.getOrdres().add(ordre);
		ordre = new PaginacioParamsDto.OrdreDto("titularLlinatge2", direccio);
		paginacioParams.getOrdres().add(ordre);
	}

	public FiltreEnviament getFiltre(Long entitatId, NotificacioEnviamentFiltreDto filtreDto, String usuariCodi, RolEnumDto rol, String organGestorCodi) throws ParseException {

		var isUsuari = RolEnumDto.tothom.equals(rol);
		var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
		var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
		var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
		var entitatsActives = isSuperAdmin ? entitatRepository.findByActiva(true) : null;
		var entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat, false);
		var auth = SecurityContextHolder.getContext().getAuthentication();
		entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
		// Procediments accessibles per qualsevol òrgan gestor
		var codisProcedimentsDisponibles = isUsuari && entitatEntity != null ? procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatEntity, PermisEnum.CONSULTA) : null;
		// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
		var codisOrgansGestorsDisponibles = isUsuari && entitatEntity != null ? organGestorHelper.findCodiOrgansGestorsWithPermisPerConsulta(auth, entitatEntity, PermisEnum.CONSULTA) : null;
		var codisOrgansGestorsComunsDisponibles = isUsuari && entitatEntity != null ? organGestorHelper.findCodiOrgansGestorsWithPermisPerConsulta(auth, entitatEntity, PermisEnum.COMUNS) : null;
		// Procediments comuns que es poden consultar per a òrgans gestors concrets
		var codisProcedimentsOrgans = isUsuari && entitatEntity != null ? permisosService.getProcedimentsOrgansAmbPermis(entitatEntity.getId(), auth.getName(), PermisEnum.CONSULTA) : null;
		var esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
		var esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
		boolean esOrgansGestorsComunsCodisNotibNull = (codisOrgansGestorsComunsDisponibles == null || codisOrgansGestorsComunsDisponibles.isEmpty());
		var esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());


		var organs = isAdminOrgan ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatEntity.getDir3Codi(), organGestorCodi) : null;
		if (isAdminOrgan && !Strings.isNullOrEmpty(organGestorCodi)) {
			organs.add(organGestorCodi);
		}
		organs = organs != null && !organs.isEmpty() ? organs : null;

		Date dataCreacioInici = null;
		Date dataCreacioFi = null;
		Date dataEnviamentInici = null;
		Date dataEnviamentFi = null;
		Date dataProgramadaDisposicioInici = null;
		Date dataProgramadaDisposicioFi = null;
		Date dataRegistreInici = null;
		Date dataRegistreFi = null;
		Date dataCaducitatInici = null;
		Date dataCaducitatFi = null;

		if (!Strings.isNullOrEmpty(filtreDto.getDataCreacioInici())) {
			dataCreacioInici = FiltreHelper.toIniciDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataCreacioInici()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataCreacioFi())) {
			dataCreacioFi = FiltreHelper.toFiDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataCreacioFi()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataEnviamentInici())) {
			dataEnviamentInici = FiltreHelper.toIniciDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataEnviamentInici()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataEnviamentFi())) {
			dataEnviamentFi = FiltreHelper.toFiDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataEnviamentFi()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataProgramadaDisposicioInici())) {
			dataProgramadaDisposicioInici = FiltreHelper.toIniciDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataProgramadaDisposicioInici()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataProgramadaDisposicioFi())) {
			dataProgramadaDisposicioFi = FiltreHelper.toFiDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataProgramadaDisposicioFi()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataRegistreInici())) {
			dataRegistreInici = FiltreHelper.toIniciDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataRegistreInici()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataRegistreFi())) {
			dataRegistreFi = FiltreHelper.toFiDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataRegistreFi()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataCaducitatInici())) {
			dataCaducitatInici = FiltreHelper.toIniciDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataCaducitatInici()));
		}
		if (!Strings.isNullOrEmpty(filtreDto.getDataCaducitatFi())) {
			dataCaducitatFi = FiltreHelper.toFiDia(new SimpleDateFormat(FORMAT_DATA).parse(filtreDto.getDataCaducitatFi()));
		}
		var estat = filtreDto.getEstat();
		Boolean hasZeronotificaEnviamentIntent = null;
		var isEstatNull = estat == null;
		var nomesSenseErrors = false;
		var nomesAmbErrors = false;
		if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.ENVIANT)) {
			estat = NotificacioEstatEnumDto.PENDENT;
			hasZeronotificaEnviamentIntent = true;
			nomesSenseErrors = true;
		} else if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.PENDENT)) {
			hasZeronotificaEnviamentIntent = false;
		}
		//Filtres camps procediment
		EnviamentTipus tipusEnviament = null;
		if (filtreDto.getEnviamentTipus() != null) {
			tipusEnviament = NotificacioTipusEnviamentEnumDto.notificacio.equals(filtreDto.getEnviamentTipus()) ? EnviamentTipus.NOTIFICACIO :  EnviamentTipus.COMUNICACIO;
		}
		var creadaPer = filtreDto.getCreatedBy() != null ? filtreDto.getCreatedBy().getCodi() : null;
		return FiltreEnviament.builder()
				.entitatIdNull(isSuperAdmin)
				.entitatId(entitatId)
				.dataCreacioIniciNull(dataCreacioInici == null)
				.dataCreacioInici(dataCreacioInici)
				.dataCreacioFiNull(dataCreacioFi == null)
				.dataCreacioFi(dataCreacioFi)
				.dataEnviamentIniciNull(dataEnviamentInici == null)
				.dataEnviamentInici(dataEnviamentInici)
				.dataEnviamentFiNull(dataEnviamentFi == null)
				.dataEnviamentFi(dataEnviamentFi)
				.dataProgramadaDisposicioIniciNull(dataProgramadaDisposicioInici == null)
				.dataProgramadaDisposicioInici(dataProgramadaDisposicioInici)
				.dataProgramadaDisposicioFiNull(dataProgramadaDisposicioFi == null)
				.dataProgramadaDisposicioFi(dataProgramadaDisposicioFi)
				.codiNotificaNull(Strings.isNullOrEmpty(filtreDto.getCodiNotifica()))
				.codiNotifica(filtreDto.getCodiNotifica())
				.codiProcediment(filtreDto.getCodiProcediment())
				.codiProcedimentNull(Strings.isNullOrEmpty(filtreDto.getCodiProcediment()))
				.grupNull(Strings.isNullOrEmpty(filtreDto.getGrup()))
				.grup(filtreDto.getGrup())
				.usuariNull(Strings.isNullOrEmpty(filtreDto.getUsuari()))
				.usuari(filtreDto.getUsuari())
				.enviamentTipusNull(tipusEnviament == null)
				.enviamentTipus(tipusEnviament)
				.concepteNull(Strings.isNullOrEmpty(filtreDto.getConcepte()))
				.concepte(filtreDto.getConcepte())
				.descripcioNull(Strings.isNullOrEmpty(filtreDto.getDescripcio()))
				.descripcio(filtreDto.getDescripcio())
				.nifTitularNull(Strings.isNullOrEmpty(filtreDto.getNifTitular()))
				.nifTitular(filtreDto.getNifTitular())
				.nomTitularNull(Strings.isNullOrEmpty(filtreDto.getTitularNomLlinatge()))
				.nomTitular(filtreDto.getTitularNomLlinatge())
				.emailTitularNull(Strings.isNullOrEmpty(filtreDto.getEmailTitular()))
				.emailTitular(filtreDto.getEmailTitular())
//				.destinataris(new FiltreField<>(dataProgramadaDisposicioFi))
//				.registreLlibre(new FiltreField<>(dataProgramadaDisposicioFi))
				.codiNotibEnviament(filtreDto.getCodiNotibEnviament())
				.registreNumeroNull(Strings.isNullOrEmpty(filtreDto.getRegistreNumero()))
				.registreNumero(filtreDto.getRegistreNumero())
				.dataProgramadaDisposicioFiNull(dataProgramadaDisposicioFi == null)
				.dataProgramadaDisposicioFi(dataProgramadaDisposicioFi)
				.dataRegistreIniciNull(dataRegistreInici == null)
				.dataRegistreInici(dataRegistreInici)
				.dataRegistreFiNull(dataRegistreFi == null)
				.dataRegistreFi(dataRegistreFi)
				.dataCaducitatIniciNull(dataCaducitatInici == null)
				.dataCaducitatInici(dataCaducitatInici)
				.dataCaducitatFiNull(dataCaducitatFi == null)
				.dataCaducitatFi(dataCaducitatFi)
				.codiNotibEnviamentNull(Strings.isNullOrEmpty(filtreDto.getCodiNotibEnviament()))
				.codiNotibEnviament(filtreDto.getCodiNotibEnviament())
				.numeroCertCorreusNull(Strings.isNullOrEmpty(filtreDto.getNumeroCertCorreus()))
				.numeroCertCorreus(filtreDto.getNumeroCertCorreus())
				.csvUuidNull(Strings.isNullOrEmpty(filtreDto.getCsvUuid()))
				.csvUuid(filtreDto.getCsvUuid())
				.estatNull(estat == null)
				.estat(estat)
				.dir3CodiNull(Strings.isNullOrEmpty(filtreDto.getDir3Codi()))
				.dir3Codi(filtreDto.getDir3Codi())
				.creadaPerNull(Strings.isNullOrEmpty(creadaPer))
				.creadaPerCodi(creadaPer)
				.nomesSenseErrors(nomesSenseErrors)
				.nomesAmbErrors(nomesAmbErrors)
				.hasZeronotificaEnviamentIntentNull(hasZeronotificaEnviamentIntent == null)
				.hasZeronotificaEnviamentIntent(hasZeronotificaEnviamentIntent)
				.referenciaNotificacioNull(Strings.isNullOrEmpty(filtreDto.getReferenciaNotificacio()))
				.referenciaNotificacio(filtreDto.getReferenciaNotificacio())
				.procedimentsCodisNotibNull(esProcedimentsCodisNotibNull)
				.procedimentsCodisNotib(!esProcedimentsCodisNotibNull ? codisProcedimentsOrgans : null)
				.organsGestorsCodisNotibNull(esOrgansGestorsCodisNotibNull)
				.organsGestorsCodisNotib(!esOrgansGestorsCodisNotibNull ? codisOrgansGestorsDisponibles : null)
				.organsGestorsComunsCodisNotibNull(esOrgansGestorsComunsCodisNotibNull)
				.organsGestorsComunsCodisNotib(codisOrgansGestorsComunsDisponibles)
				.procedimentOrgansAmbPermisNull(esProcedimentOrgansAmbPermisNull)
				.procedimentOrgansAmbPermis(!esProcedimentOrgansAmbPermisNull ? codisProcedimentsOrgans : null)
				.organs(organs)
				.entitat(entitatEntity)
				.rols(aplicacioService.findRolsUsuariActual())
				.usuariCodi(usuariCodi)
				.isUsuari(isUsuari)
				.entitatsActives(entitatsActives)
				.isSuperAdmin(isSuperAdmin)
				.isAdminOrgan(isAdminOrgan)
				.build();
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(Long entitatId, Collection<Long> enviamentIds, String format) throws IOException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Exportant informació dels enviaments (entitatId=" + entitatId + ", enviamentsIds=" + enviamentIds + ", format=" + format + ")");
			var entitatEntity = entitatRepository.findById(entitatId).orElseThrow();
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			var enviaments = notificacioEnviamentRepository.findByIdIn(enviamentIds);
			//Genera les columnes
			int numColumnes = 24;
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
			columnes[22] = messageHelper.getMessage("enviament.service.exportacio.estat.finalitzat.date");
			columnes[23] = messageHelper.getMessage("enviament.service.exportacio.estat.processat.date");

			var sdf = new SimpleDateFormat(FORMAT_DATA_HORA);
			var dtf = DateTimeFormatter.ofPattern(FORMAT_DATA_HORA);
			List<String[]> files = new ArrayList<>();
			var llibreOrgan = !entitatEntity.isLlibreEntitat();
			String[] fila;
			String csvUuid;
			for (var enviament : enviaments) {
				fila = new String[numColumnes];
				csvUuid = "";
				if(enviament.getNotificacio().getDocument().getCsv() != null) {
					csvUuid = enviament.getNotificacio().getDocument().getCsv();
				}
				if(enviament.getNotificacio().getDocument().getUuid() != null) {
					csvUuid = enviament.getNotificacio().getDocument().getUuid();
				}
				fila[0] = enviament.getCreatedDate().isPresent() ? dtf.format(enviament.getCreatedDate().orElseThrow()) : "";
				fila[1] = enviament.getNotificacio().getEnviamentDataProgramada() != null ? sdf.format(enviament.getNotificacio().getEnviamentDataProgramada()) : "";
				fila[2] = enviament.getNotificaIdentificador();
				fila[3] = enviament.getNotificacio().getProcedimentCodiNotib();
				fila[4] = enviament.getNotificacio().getGrupCodi();
				fila[5] = enviament.getNotificacio().getEmisorDir3Codi();
				fila[6] = enviament.getCreatedBy().orElseThrow().getCodi();
				fila[7] = enviament.getNotificacio().getEnviamentTipus().name();
				fila[8] = enviament.getNotificacio().getConcepte();
				fila[9] = enviament.getNotificacio().getDescripcio();
				fila[10] = enviament.getTitular().getNif();
				fila[11] = enviament.getTitular().getNom();
				fila[12] = enviament.getTitular().getEmail();
				fila[13] = !enviament.getDestinataris().isEmpty() ? enviament.getDestinataris().get(0).getNif() : null;
				if (llibreOrgan) {
					if (enviament.getNotificacio().getProcediment() != null) {
						fila[14] = enviament.getNotificacio().getProcediment().getOrganGestor().getLlibre();
					}
				} else {
					fila[14] = entitatEntity.getLlibre();
				}
				fila[15] = String.valueOf(enviament.getNotificacio().getRegistreNumero());
				fila[16] = (enviament.getNotificacio().getRegistreData() != null)? enviament.getNotificacio().getRegistreData().toString() : "";
				fila[17] = enviament.getNotificacio().getCaducitat() != null ? sdf.format(enviament.getNotificacio().getCaducitat()) : "";
				fila[19] = enviament.getNotificaCertificacioNumSeguiment();
				fila[20] = csvUuid;
				fila[21] = enviament.getNotificacio().getEstat().name();
				fila[22] = (enviament.getNotificacio().getEstatDate() != null ? enviament.getNotificacio().getEstatDate() + "" : "");
				fila[23] = (enviament.getNotificacio().getEstatProcessatDate() != null ? enviament.getNotificacio().getEstatProcessatDate() + "" : "");
				files.add(fila);
			}

			var fitxer = new FitxerDto();
			// TODO: Substutuit la llibreria jopendocument
			if (!"ODS".equalsIgnoreCase(format)) {
				throw new ValidationException("Format de fitxer no suportat: " + format);
			}
			var writerList = new StringWriter();
			var listWriter = initCsvWritter(writerList);
			writeCsvHeader(listWriter, columnes);
			files.forEach(f -> writeCsvLinia(listWriter, f));
			listWriter.flush();
			var contingut = writerList.toString().getBytes();
			writeCsvClose(listWriter);
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			fitxer.setContingut(contingut);
			return fitxer;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private ICsvListWriter initCsvWritter(Writer writer) {
		return new CsvListWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
	}

	private void writeCsvHeader(ICsvListWriter listWriter, String[] csvHeader) {

		try {
			listWriter.writeHeader(csvHeader);
		} catch (IOException e) {
			log.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
			throw new WriteCsvException(messageHelper.getMessage("error.escriure.capcalera.fitxer.csv"));
		}
	}

	private void writeCsvLinia(ICsvListWriter listWriter, String... linia) {

		try {
			listWriter.write(linia);
		} catch (IOException e) {
			log.error("S'ha produït un error a l'escriure la línia en el fitxer CSV.", e);
			throw new WriteCsvException(messageHelper.getMessage("error.escriure.linia.fitxer.csv"));
		}
	}
	private void writeCsvClose(ICsvListWriter listWriter) {

		try {
			if( listWriter != null ) {
				listWriter.close();
			}
		} catch (IOException e) {
			log.error("S'ha produït un error a l'tancar el fitxer CSV.", e);
			throw new WriteCsvException(messageHelper.getMessage("error.tancar.fitxer.csv"));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events de la notificació (notificacioId=" + notificacioId + ")");
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			return conversioTipusHelper.convertirList(notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId), NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void reactivaConsultes(Set<Long> enviaments) {

		var timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEnviamentEntity enviament;
			List<NotificacioEventEntity> events;
			for (var enviamentId: enviaments) {
				enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
				enviament.refreshNotificaConsulta();
				events = notificacioEventRepository.findByEnviamentAndTipusAndError(enviament, NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, true);
				for (var event : events) {
					event.setFiReintents(false);
				}
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void reactivaSir(Set<Long> enviaments) {

		var timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEnviamentEntity enviament;
			NotificacioEventEntity event;
			for (Long enviamentId: enviaments) {
				enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
				enviament.refreshSirConsulta();
				event = enviament.getNotificacioErrorEvent();
				if (event != null) {
					event.setFiReintents(false);
				}
				auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "EnviamentServiceImpl.reactivaSir");
				enviamentSmService.sirReset(enviament.getUuid());
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioEnviamentDto enviamentToDto(NotificacioEnviamentEntity enviament) {

		var enviamentDto = conversioTipusHelper.convertir(enviament, NotificacioEnviamentDto.class);
		enviamentDto.setRegistreNumeroFormatat(enviament.getRegistreNumeroFormatat());
		enviamentDto.setRegistreData(enviament.getRegistreData());
		enviamentDto.setNotificaCertificacioArxiuNom(calcularNomArxiuCertificacio(enviament));
		var callback = callbackRepository.findByEnviamentIdAndEstat(enviament.getId(), CallbackEstatEnumDto.PENDENT);
		if (callback != null) {
			enviamentDto.setCallbackPendent(callback != null);
			var df = new SimpleDateFormat(FORMAT_DATA_HORA);
			enviamentDto.setCallbackData(callback.getData() != null ? df.format(callback.getData()) : null);
		}
		var event = enviament.getNotificacioErrorEvent();
		if (event != null && event.getFiReintents()) {
			enviamentDto.setFiReintents(true);
			var msg = messageHelper.getMessage("notificacio.event.fi.reintents");
			var tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
			enviamentDto.setFiReintentsDesc(msg + " -> " + tipus);
		}
		var e = notificacioEventRepository.findLastApiCarpetaByEnviamentId(enviament.getId());
		if (e != null && e.isError()) {
			enviamentDto.setNotificacioMovilErrorDesc(e.getErrorDescripcio());
		}
		callback = callbackRepository.findByEnviamentIdAndEstat(enviament.getId(), CallbackEstatEnumDto.ERROR);
		if (callback == null) {
			return enviamentDto;
		}
		enviamentDto.setCallbackFiReintents(true);
		enviamentDto.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
		return enviamentDto;
	}

	private String calcularNomArxiuCertificacio(NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}
	
	@Transactional
	@Override
	public NotificacioEnviamentDtoV2 getOne(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertir(notificacioEnviamentRepository.findById(enviamentId).orElseThrow(), NotificacioEnviamentDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	public byte[] getDocumentJustificant(Long enviamentId) {
		
		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		if (enviament.getRegistreEstat() != null && enviament.getRegistreEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_EXTERN)) {
			return pluginHelper.obtenirOficiExtern(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();
		}
		return pluginHelper.obtenirJustificant(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Resposta findEnviaments(ApiConsulta consulta) {

		consulta.setVisibleCarpeta(null);
		var paginaEnviaments = findEnviamentsByConsulta(consulta);
		var resposta = new Resposta();
		resposta.setNumeroElementsTotals(paginaEnviaments.getNumEnviaments());
		resposta.setNumeroElementsRetornats(paginaEnviaments.getNumeroEnviamentsRetornats());
		var dtos = conversioTipusHelper.convertirList(paginaEnviaments.getEnviaments(), NotificacioEnviamentDto.class);
		resposta.setResultat(dtosToTransmissions(dtos, consulta.getBasePath()));
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaV2 findEnviamentsV2(ApiConsulta consulta) {

		var paginaEnviaments = findEnviamentsByConsulta(consulta);
		return RespostaConsultaV2.builder()
				.numeroElementsTotals(paginaEnviaments.getNumEnviaments())
				.numeroElementsRetornats(paginaEnviaments.getNumeroEnviamentsRetornats())
				.resultat(paginaEnviaments.getTransmissionsV2(consulta.getBasePath()))
				.build();
	}

	private PaginaEnviaments findEnviamentsByConsulta(ApiConsulta consulta) {

		var dataInicial = consulta.getDataInicial() != null ? FiltreHelper.toIniciDia(consulta.getDataInicial()) : null;
		var dataFinal = consulta.getDataFinal() != null ? FiltreHelper.toFiDia(consulta.getDataFinal()) : null;
		var enviaments = notificacioEnviamentRepository.findEnviaments(
				consulta.getDniTitular(),
				dataInicial == null,
				dataInicial,
				dataFinal == null,
				dataFinal,
				consulta.getTipus(),
				consulta.getEstatFinal() == null,
				consulta.getEstatFinal(),
				consulta.getVisibleCarpeta() == null,
				consulta.getVisibleCarpeta(),
				getPageable(consulta.getPagina(), consulta.getMida()));

		var numEnviaments = (int) enviaments.getTotalElements();
		return PaginaEnviaments.builder().messageHelper(messageHelper).numEnviaments(numEnviaments).enviaments(enviaments.getContent()).locale(new Locale(consulta.getIdioma().name())).build();
	}

	private Pageable getPageable(Integer pagina, Integer mida) {

		var pageable = PageRequest.of(0, 999999999);
		if (pagina != null && mida != null) {
			pageable = PageRequest.of(pagina, mida);
		}
		return pageable;
	}
	
	
	private List<Transmissio> dtosToTransmissions(List<NotificacioEnviamentDto> enviaments, String basePath) {

		if (enviaments == null) {
			return new ArrayList<>();
		}
		List<Transmissio> transmissions = new ArrayList<>();
		for (var enviament : enviaments) {
			transmissions.add(toTransmissio(enviament, basePath));
		}
		return transmissions;
	}

	private Transmissio toTransmissio(NotificacioEnviamentDto enviament, String basePath) {

		var transmissio = new Transmissio();
		transmissio.setId(enviament.getId());
		var not = enviament.getNotificacio();
		transmissio.setEmisor(not.getEntitat().getCodi());
		transmissio.setOrganGestor(not.getOrganGestor());
		if (not.getProcediment() != null) {
			transmissio.setProcediment(not.getProcediment().getCodi());
		}
		transmissio.setNumExpedient(not.getNumExpedient());
		transmissio.setConcepte(not.getConcepte());
		transmissio.setDescripcio(not.getDescripcio());
		var dataProgramada = not.getEnviamentDataProgramada();
		transmissio.setDataEnviament(dataProgramada == null ? not.getNotificaEnviamentData() : dataProgramada);
		transmissio.setEstat(getEstat(enviament));
		var data = not.getEstatDate() != null ? not.getEstatDate() :
				(NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat()) ? not.getRegistreData()
						: NotificacioEstatEnumDto.PENDENT.equals(not.getEstat()) ? not.getCreatedDate()
						: (NotificacioEstatEnumDto.ENVIADA.equals(not.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(not.getEstat())) ? not.getNotificaEnviamentData()
						: not.getCreatedDate());

		transmissio.setDataEstat(data);
		if (not.getDocument() != null) {
			var document = es.caib.notib.logic.intf.rest.consulta.Document.builder().nom(not.getDocument().getArxiuNom()).mediaType(not.getDocument().getMediaType())
							.mida(not.getDocument().getMida()).url(basePath + "/document/" + not.getId()).build();
			transmissio.setDocument(document);
		}
		transmissio.setTitular(toPersona(enviament.getTitular()));
		List<Persona> destinataris = new ArrayList<>();
		if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
			for (PersonaDto destinatari: enviament.getDestinataris()) {
				destinataris.add(toPersona(destinatari));
			}
		}
		transmissio.setDestinataris(destinataris);
		transmissio.setSubestat(SubEstat.valueOf(enviament.getNotificaEstat().name()));
		transmissio.setDataSubestat(enviament.getNotificaEstatData() != null ? enviament.getNotificaEstatData() : data);
		transmissio.setError(enviament.isNotificaError());
		transmissio.setErrorData(enviament.getNotificaErrorData());
		transmissio.setErrorDescripcio(enviament.getNotificaErrorDescripcio());
		
		// Justificant de registre
		if (NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat()) && (enviament.getRegistreEstat() != null &&
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

	// Recuperar l'estat a partir de l'enviament, i no de la notificació.
	//  A més, no s'han d'eliminar els estat enviada_amb_errors i finalitzada_amb_errors.
	private Estat getEstat(NotificacioEnviamentDto enviament) {

		var notificacio = enviament.getNotificacio();
		switch (notificacio.getEstat()) {
			case PENDENT:
				return Estat.PENDENT;
			case REGISTRADA:
				return Estat.REGISTRADA;
			case ENVIADA:
				return enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA : Estat.ENVIADA;
			case FINALITZADA:
				return Estat.FINALITZADA;
			case PROCESSADA:
				return Estat.PROCESSADA;
			case FINALITZADA_AMB_ERRORS:
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA : enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA : Estat.REGISTRADA;
			case ENVIADA_AMB_ERRORS:
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA :
						enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA : enviament.isEnviamentEnviat() ? Estat.ENVIADA : Estat.REGISTRADA;
			default:
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA :
						enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA :
						enviament.isEnviamentEnviat() ? Estat.ENVIADA : EnviamentEstat.REGISTRADA.equals(enviament.getNotificaEstat()) ? Estat.REGISTRADA : Estat.PENDENT;
		}
	}
	
	private Persona toPersona(PersonaDto dto) {

		var persona = new Persona();
		persona.setNom(dto.getNom());
		if (dto.getInteressatTipus() != null) {
			persona.setTipus(PersonaTipus.valueOf(dto.getInteressatTipus().name()));
			if (!InteressatTipus.FISICA.equals(dto.getInteressatTipus())) {
				persona.setNom(!Strings.isNullOrEmpty(dto.getRaoSocial()) ? dto.getRaoSocial() : dto.getNom());
			}
		}
		persona.setLlinatge1(dto.getLlinatge1());
		persona.setLlinatge2(dto.getLlinatge2());
		persona.setNif(dto.getNif());
		persona.setEmail(dto.getEmail());
		return persona;
	}

	@Transactional
	@Override
	public void actualitzarEstat(Long enviamentId) {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		enviament.refreshNotificaConsulta();
		enviament.refreshSirConsulta();
		if (enviament.getNotificacio().isComunicacioSir()) {
			// si l'enviament esta pendent de refrescar l'estat enviat SIR
			if (enviament.isPendentRefrescarEstatRegistre()) {
				enviamentSmService.sirRetry(enviament.getUuid());
//				notificacioService.enviamentRefrescarEstatRegistre(enviamentId);
//				enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
//				if (enviament.isRegistreEstatFinal()) {
//					enviamentSmService.sirForward(enviament.getUuid());
//				}
			}
			return;
		}
		// si l'enviament esta pendent de refrescar estat a notifica
		if (enviament.isPendentRefrescarEstatNotifica()) {
			enviamentSmService.consultaRetry(enviament.getUuid());
//			notificacioService.enviamentRefrescarEstat(enviamentId);
//			enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
//			if (enviament.isNotificaEstatFinal()) {
//				enviamentSmService.consultaForward(enviament.getUuid());
//			}
		}
	}

	@Transactional
	@Override
	public void activarCallback(Long enviamentId) {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		if (!enviament.getNotificacio().isTipusUsuariAplicacio()) {
			var text = String.format("[callback] No es pot reactivar el callback de l'enviment [id=%d] (Tipus usuari = %s)", enviamentId, enviament.getNotificacio().getTipusUsuari().toString());
			log.info(text);
			return;
		}
		var event = notificacioEventRepository.findEventCallbackAmbFiReintentsByEnviamentId(enviamentId);
		if (event == null) {
			return;
		}
		log.info(String.format("[callback] Reactivam callback de l'enviment [id=%d]", enviamentId));

		callbackHelper.reactivarCallback(enviament);
		event.setFiReintents(false);
		var not = notificacioTableRepository.findById(enviament.getNotificacio().getId());
		not.orElseThrow().setPerActualitzar(true);
	}

	@Override
	public List<Long> enviarCallback(Set<Long> notificacions) throws Exception {

		var callbacks = callbackRepository.findByNotificacioIdIn(notificacions);
		List<Long> enviamentsAmbError = new ArrayList<>();
		boolean isError;
		for (var callback : callbacks) {
			log.info(String.format("[callback] Enviar callback de l'enviment [id=%d]", callback.getEnviamentId()));
			isError = callbackHelper.notifica(callback.getEnviamentId());
			if (isError) {
				enviamentsAmbError.add(callback.getEnviamentId());
				continue;
			}
			log.info(String.format("[Callback] Enviament del callback [Id: %d] de l''enviament [Id: %d] exitós", callback.getId(), callback.getEnviamentId()));
		}
		return enviamentsAmbError;
	}

	@Builder @Getter
	private static class PaginaEnviaments {

		private Integer numEnviaments;
		private List<NotificacioEnviamentEntity> enviaments;
		private Locale locale;
		private MessageHelper messageHelper;

		public Integer getNumeroEnviamentsRetornats() {
			return enviaments != null ? enviaments.size() : 0;
		}

		public List<TransmissioV2> getTransmissionsV2(String basePath) {

			if (enviaments == null) {
				return new ArrayList<>();
			}
			List<TransmissioV2> transmissions = new ArrayList<>();
			for (NotificacioEnviamentEntity enviament: enviaments) {
				transmissions.add(toTransmissio(enviament, basePath));
			}
			return transmissions;
		}

		private TransmissioV2 toTransmissio(NotificacioEnviamentEntity enviament, String basePath) {

			var not = enviament.getNotificacio();
			// Organ
			var organGestor = GenericInfo.builder().codi(not.getOrganGestor().getCodi()).nom(not.getOrganGestor().getNom()).build();
			// Procediment
			GenericInfo procediment = null;
			if (not.getProcediment() != null) {
				procediment = GenericInfo.builder().codi(not.getProcediment().getCodi()).nom(not.getProcediment().getNom()).build();
			}
			// Estat
			var estat = getEstat(enviament);
			var dataEstat = getEstatDate(enviament);
			// Document
			DocumentConsultaV2 document = null;
			if (not.getDocument() != null) {
				document = DocumentConsultaV2.builder().nom(not.getDocument().getArxiuNom()).mediaType(not.getDocument().getMediaType())
							.mida(not.getDocument().getMida()).url(basePath + "/document/" + not.getId()).build();
			}
			// Titular
			var titular = toPersona(enviament.getTitular());
			// Destinataris
			List<PersonaConsultaV2> destinataris = new ArrayList<>();
			if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
				for (var destinatari: enviament.getDestinataris()) {
					destinataris.add(toPersona(destinatari));
				}
			}
			// Justificant de registre
			String justificantUrl = null;
			if (NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat()) &&
					(enviament.getRegistreEstat() != null &&
							(NotificacioRegistreEstatEnumDto.DISTRIBUIT.equals(enviament.getRegistreEstat()) ||
									NotificacioRegistreEstatEnumDto.OFICI_EXTERN.equals(enviament.getRegistreEstat()) ||
									NotificacioRegistreEstatEnumDto.OFICI_SIR.equals(enviament.getRegistreEstat()) ) ||
							(enviament.getRegistreData() != null && enviament.getRegistreNumeroFormatat() != null && !enviament.getRegistreNumeroFormatat().isEmpty()))) {

				justificantUrl = basePath + "/justificant/" + enviament.getId();
			}
			// Certificació
			String certificacioUrl = null;
			if (enviament.getNotificaCertificacioData() != null) {
				certificacioUrl = basePath + "/certificacio/" + enviament.getId();
			}
			// Errors
			Date errorData = null;
			String errorDescripcio = null;
			if (enviament.isNotificaError()) {
				var event = enviament.getNotificacioErrorEvent();
				if (event != null) {
					errorData = event.getData();
					errorDescripcio = event.getErrorDescripcio();
				}
			}

			return TransmissioV2.builder()
					.id(enviament.getId())
					.emisor(not.getEntitat().getCodi())
					.organGestor(organGestor)
					.procediment(procediment)
					.numExpedient(not.getNumExpedient())
					.concepte(not.getConcepte())
					.descripcio(not.getDescripcio())
					.dataEnviament(not.getEnviamentDataProgramada() != null ? not.getEnviamentDataProgramada() : not.getNotificaEnviamentData())
					.estat(estat)
					.dataEstat(dataEstat)
					.document(document)
					.titular(titular)
					.destinataris(destinataris)
					.error(enviament.isNotificaError())
					.errorData(errorData)
					.errorDescripcio(errorDescripcio)
					.justificant(justificantUrl)
					.certificacio(certificacioUrl)
					.build();
		}

		// Recuperar l'estat a partir de l'enviament, i no de la notificació.
		//  A més, no s'han d'eliminar els estat enviada_amb_errors i finalitzada_amb_errors.
		private GenericInfo getEstat(NotificacioEnviamentEntity enviament) {

			var error = "ERROR";
			switch (enviament.getNotificaEstat()) {
				case NOTIB_PENDENT:
				case REGISTRADA:
				case NOTIB_ENVIADA:
				case ENVIAMENT_PROGRAMAT:
					var enTramit = "EN_TRAMITACIO";
					return GenericInfo.builder().codi("EN TRAMITACIO").nom(getNom(enTramit)).descripcio(getDesc(enTramit)).build();
				case ABSENT:
					var absent = "ABSENT";
					return GenericInfo.builder().codi(absent).nom(getNom(absent)).descripcio(getDesc(absent)).build();
				case ADRESA_INCORRECTA:
					var adrIncorrecta = "ADRESA_INCORRECTA";
					return GenericInfo.builder().codi("ADREÇA INCORRECTA").nom(getNom(adrIncorrecta)).descripcio(getDesc(adrIncorrecta)).build();
				case DESCONEGUT:
					var desconegut = "DESCONEGUT";
					return GenericInfo.builder().codi("DESCONEGUT").nom(getNom(desconegut)).descripcio(getDesc(desconegut)).build();
				case ENVIADA:
				case ENVIADA_CI:
				case ENVIADA_DEH:
				case ENTREGADA_OP:
				case PENDENT:
				case PENDENT_ENVIAMENT:
				case PENDENT_SEU:
				case PENDENT_CIE:
				case PENDENT_DEH:
					var pendent = "PENDENT";
					return GenericInfo.builder().codi("PENDENT COMPAREIXENÇA").nom(getNom(pendent)).descripcio(getDesc(pendent)).build();
				case ERROR_ENTREGA:
					return GenericInfo.builder().codi(error).nom(getNom(error)).descripcio(getDesc(error)).build();
				case EXPIRADA:
					var expirada = "EXPIRADA";
					return GenericInfo.builder().codi(expirada).nom(getNom(expirada)).descripcio(getDesc(expirada)).build();
				case EXTRAVIADA:
					var extraviada = "EXTRAVIADA";
					return GenericInfo.builder().codi(extraviada).nom(getNom(extraviada)).descripcio(getDesc(extraviada)).build();
				case MORT:
					var difunt = "DIFUNT";
					return GenericInfo.builder().codi(difunt).nom(getNom(difunt)).descripcio(getDesc(difunt)).build();
				case LLEGIDA:
					var llegida = "LLEGIDA";
					return GenericInfo.builder().codi(llegida).nom(getNom(llegida)).descripcio(getDesc(llegida)).build();
				case NOTIFICADA:
					var acceptada = "ACCEPTADA";
					return GenericInfo.builder().codi(acceptada).nom(getNom(acceptada)).descripcio(getDesc(acceptada)).build();
				case REBUTJADA:
					var rebutjada = "REBUTJADA";
					return GenericInfo.builder().codi(rebutjada).nom(getNom(rebutjada)).descripcio(getDesc(rebutjada)).build();
				case SENSE_INFORMACIO:
					var noInfo = "SENSE_INFORMACIO";
					return GenericInfo.builder().codi("SENSE INFORMACIO").nom(getNom(noInfo)).descripcio(getDesc(noInfo)).build();
				case ANULADA:
					var anulada = "ANULADA";
					return GenericInfo.builder().codi(anulada).nom(getNom(anulada)).descripcio(getDesc(anulada)).build();
				case ENVIAT_SIR:
					var enviadaSir = "ENVIADA_SIR";
					return GenericInfo.builder().codi("ENVIAT SIR").nom(getNom(enviadaSir)).descripcio(getDesc(enviadaSir)).build();
				case FINALITZADA:
					if (enviament.isPerEmail()) {
						var enviadaMail = "ENVIADA_EMAIL";
						return GenericInfo.builder().codi("ENVIAT EMAIL").nom(getNom(enviadaMail)).descripcio(getDesc(enviadaMail)).build();
					}
				case PROCESSADA:
				case ENVIADA_AMB_ERRORS:
				case FINALITZADA_AMB_ERRORS:
				default:
					return GenericInfo.builder().codi(error).nom(getNom(error)).descripcio(messageHelper.getMessage("enviament.estat.ERROR", null, locale)).build();
			}
		}

		private String getNom(String codi) {
			return messageHelper.getMessage("enviament.estat." + codi + ".nom", null, locale);
		}
		private String getDesc(String codi) {
			return messageHelper.getMessage("enviament.estat." + codi + ".desc", null, locale);
		}

		private Date getEstatDate(NotificacioEnviamentEntity enviament) {

			if (enviament.getNotificaEstatData() != null) {
				return enviament.getNotificaEstatData();
			}
			var not = enviament.getNotificacio();
			if (not.getEstatDate() != null) {
				return not.getEstatDate();
			}
			if (NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat())) {
				return not.getRegistreData();
			} else if (NotificacioEstatEnumDto.ENVIADA.equals(not.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(not.getEstat())) {
				return not.getNotificaEnviamentData();
			}
			AtomicReference<Date> data = null;
			not.getCreatedDate().ifPresentOrElse(value -> data.set(Date.from(value.atZone(ZoneId.systemDefault()).toInstant())), () -> data.set(null));
			return data.get();
		}

		private PersonaConsultaV2 toPersona(PersonaEntity personaEntity) {

			var tipus = personaEntity.getInteressatTipus();
			var nom = personaEntity.getNom();
			if (!InteressatTipus.FISICA.equals(tipus)&& personaEntity.getRaoSocial() != null && !personaEntity.getRaoSocial().isEmpty()) {
				nom = personaEntity.getRaoSocial();
			}
			return PersonaConsultaV2.builder()
					.tipus(GenericInfo.builder().codi(tipus.name()).nom(messageHelper.getMessage("InteressatTipus." + tipus.name())).build())
					.nom(nom)
					.llinatge1(personaEntity.getLlinatge1())
					.llinatge2(personaEntity.getLlinatge2())
					.nif(personaEntity.getNif())
					.email(personaEntity.getEmail())
					.build();
		}

	}

}
