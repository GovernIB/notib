package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.consulta.DocumentConsultaV2;
import es.caib.notib.client.domini.consulta.GenericInfo;
import es.caib.notib.client.domini.consulta.PersonaConsultaV2;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.client.domini.consulta.TransmissioV2;
import es.caib.notib.core.api.dto.ApiConsulta;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioTipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.rest.consulta.Estat;
import es.caib.notib.core.api.rest.consulta.Persona;
import es.caib.notib.core.api.rest.consulta.PersonaTipus;
import es.caib.notib.core.api.rest.consulta.Resposta;
import es.caib.notib.core.api.rest.consulta.SubEstat;
import es.caib.notib.core.api.rest.consulta.Transmissio;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.entity.CallbackEntity;
import es.caib.notib.core.entity.ColumnesEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EnviamentTableEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.EnviamentHelper;
import es.caib.notib.core.helper.FiltreHelper;
import es.caib.notib.core.helper.FiltreHelper.FiltreField;
import es.caib.notib.core.helper.FiltreHelper.StringField;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.NotificacioEventHelper;
import es.caib.notib.core.helper.NotificacioHelper;
import es.caib.notib.core.helper.NotificacioTableHelper;
import es.caib.notib.core.helper.OrganGestorHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcSerHelper;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.CallbackRepository;
import es.caib.notib.core.repository.ColumnesRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EnviamentTableRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.UsuariRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class EnviamentServiceImpl implements EnviamentService {

	@Autowired
	PermisosService permisosService;
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
	private NotificacioService notificacioService;
	@Autowired
	private EnviamentHelper enviamentHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ProcSerHelper procedimentHelper;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private NotificacioEventHelper eventHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els destinataris d'una notificació (notificacioId=" + notificacioId + ")");
			entityComprovarHelper.comprovarPermisos(null,true,true,true);
			NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
			List<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findByNotificacio(notificacio);
			return conversioTipusHelper.convertirList(enviaments, NotificacioEnviamentDatatableDto.class);
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
		for (List<Long> idsNotificacionsParcials : Lists.partition(new ArrayList<>(notificacionsIds), 999)){
			idsEnviaments.addAll(
					notificacioEnviamentRepository.findIdByNotificacioIdIn(idsNotificacionsParcials)
			);
		}
		return new HashSet<>(idsEnviaments);
	}

	@Override
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String usuariCodi, String organGestorCodi, NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException  {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consultant els ids d'expedient segons el filtre (entitatId=" + entitatId + ", filtre=" + filtre + ")");
			entityComprovarHelper.comprovarPermisos(entitatId, false, false, false);

			logger.debug("Consulta els enviaments de les notificacións que te una entitat");
			boolean isUsuari = RolEnumDto.tothom.equals(rol);
			boolean isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
			boolean isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
			boolean isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat, false);
			Page<EnviamentTableEntity> pageEnviaments = null;
			logger.info("Consulta ids d'enviament, accions massives");
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("enviamentDataProgramada", new String[] {"enviamentDataProgramada"});
			mapeigPropietatsOrdenacio.put("notificaIdentificador", new String[] {"notificaIdentificador"});
			mapeigPropietatsOrdenacio.put("procedimentCodiNotib", new String[] {"procedimentCodiNotib"});
			mapeigPropietatsOrdenacio.put("grupCodi", new String[] {"grupCodi"});
			mapeigPropietatsOrdenacio.put("emisorDir3Codi", new String[] {"emisorDir3Codi"});
			mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
			mapeigPropietatsOrdenacio.put("concepte", new String[] {"concepte"});
			mapeigPropietatsOrdenacio.put("descripcio", new String[] {"descripcio"});
			mapeigPropietatsOrdenacio.put("titularNif", new String[] {"titularNif"});
			mapeigPropietatsOrdenacio.put("titularNomLlinatge", new String[] {"concat(titularLlinatge1, titularLlinatge2, titularNom)"});
			mapeigPropietatsOrdenacio.put("titularEmail", new String[] {"titularEmail"});
			mapeigPropietatsOrdenacio.put("llibre", new String[] {"registreLlibreNom"});
			mapeigPropietatsOrdenacio.put("registreNumero", new String[] {"registreNumero"});
			mapeigPropietatsOrdenacio.put("notificaDataCaducitat", new String[] {"notificaDataCaducitat"});
			mapeigPropietatsOrdenacio.put("notificaCertificacioNumSeguiment", new String[] {"notificaCertificacioNumSeguiment"});
			mapeigPropietatsOrdenacio.put("csvUuid", new String[] {"csv_uuid"});
			mapeigPropietatsOrdenacio.put("estat", new String[] {"estat"});
			mapeigPropietatsOrdenacio.put("codiNotibEnviament", new String[] {"notificaReferencia"});
			mapeigPropietatsOrdenacio.put("referenciaNotificacio", new String[] {"referenciaNotificacio"});
			NotificacioEnviamentFiltre f = getFiltre(entitatId, filtre);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);

			// Procediments accessibles per qualsevol òrgan gestor
			List<String> codisProcedimentsDisponibles = isUsuari && entitatEntity != null ? procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatEntity, PermisEnum.CONSULTA) : null;

			// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
			List<String> codisOrgansGestorsDisponibles = isUsuari && entitatEntity != null ? organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatEntity, PermisEnum.CONSULTA) : null;

			// Procediments comuns que es poden consultar per a òrgans gestors concrets
			List<String> codisProcedimentsOrgans = isUsuari && entitatEntity != null ? permisosService.getProcedimentsOrgansAmbPermis(entitatEntity.getId(), auth.getName(), PermisEnum.CONSULTA) : null;

			boolean esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
			boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			boolean esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());
			List<String> organs = isAdminOrgan ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatEntity.getDir3Codi(), organGestorCodi) : null;
			if (isAdminOrgan && !Strings.isNullOrEmpty(organGestorCodi)) {
				organs.add(organGestorCodi);
			}
			organs = organs != null && !organs.isEmpty() ? organs : null;
			return enviamentTableRepository.findIdsAmbFiltre(
					f.codiProcediment.isNull(),
					f.codiProcediment.getField(),
					f.grup.isNull(),
					f.grup.getField(),
					f.concepte.isNull(),
					f.concepte.getField(),
					f.descripcio.isNull(),
					f.descripcio.getField(),
					f.dataProgramadaDisposicioInici.isNull(),
					f.dataProgramadaDisposicioInici.getField(),
					f.dataProgramadaDisposicioFi.isNull(),
					f.dataProgramadaDisposicioFi.getField(),
					f.dataCaducitatInici.isNull(),
					f.dataCaducitatInici.getField(),
					f.dataCaducitatFi.isNull(),
					f.dataCaducitatFi.getField(),
					f.enviamentTipus.isNull(),
					f.enviamentTipus.getField(),
					f.csvUuid.isNull(),
					f.csvUuid.getField(),
					f.estat.isNull(),
					f.estat.getField(),
					!f.estat.isNull() ? EnviamentEstat.valueOf(f.estat.getField().toString()) : null,
					entitatEntity,
					f.dataEnviamentInici.isNull(),
					f.dataEnviamentInici.getField(),
					f.dataEnviamentFi.isNull(),
					f.dataEnviamentFi.getField(),
					f.codiNotifica.isNull(),
					f.codiNotifica.getField(),
					f.creadaPer.isNull(),
					f.creadaPer.getField(),
					f.nifTitular.isNull(),
					f.nifTitular.getField(),
					f.nomTitular.isNull(),
					f.nomTitular.getField(),
					f.emailTitular.isNull(),
					f.emailTitular.getField(),
					f.dir3Codi.isNull(),
					f.dir3Codi.getField(),
					f.numeroCertCorreus.isNull(),
					f.numeroCertCorreus.getField(),
					f.usuari.isNull(),
					f.usuari.getField(),
					f.registreNumero.isNull(),
					f.registreNumero.getField(),
					f.codiNotibEnviament.isNull(),
					f.codiNotibEnviament.getField(),
					f.dataRegistreInici.isNull(),
					f.dataRegistreInici.getField(),
					f.dataRegistreFi.isNull(),
					f.dataRegistreFi.getField(),
					f.nomesAmbErrors,
					f.nomesSenseErrors,
					f.hasZeronotificaEnviamentIntent.isNull(),
					f.hasZeronotificaEnviamentIntent.getField(),
					f.referenciaNotificacio.isNull(),
					f.referenciaNotificacio.getField(),
					esProcedimentsCodisNotibNull,
					esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
					esOrgansGestorsCodisNotibNull,
					esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
					esProcedimentOrgansAmbPermisNull,
					esProcedimentOrgansAmbPermisNull ? null : codisProcedimentsOrgans,
					aplicacioService.findRolsUsuariActual(),
					usuariCodi,
					isUsuari,
					isAdminOrgan,
					organs 	);
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
				dataEnviamentInici = FiltreHelper.toIniciDia(dataEnviamentInici);
			}
			if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
				dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
				dataEnviamentFi = FiltreHelper.toIniciDia(dataEnviamentFi);
			}
			if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
				dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
				dataProgramadaDisposicioInici = FiltreHelper.toIniciDia(dataProgramadaDisposicioInici);
			}
			if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
				dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
				dataProgramadaDisposicioFi = FiltreHelper.toIniciDia(dataProgramadaDisposicioFi);
			}
			if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
				dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
				dataRegistreInici = FiltreHelper.toIniciDia(dataRegistreInici);
			}
			if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
				dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
				dataRegistreFi = FiltreHelper.toIniciDia(dataRegistreFi);
			}
			if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
				dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
				dataCaducitatInici = FiltreHelper.toIniciDia(dataCaducitatInici);
			}
			if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
				dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
				dataCaducitatFi = FiltreHelper.toIniciDia(dataCaducitatFi);
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
						filtre.getEstat() != null ? EnviamentEstat.valueOf(filtre.getEstat().toString()) : null,
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
	@Transactional
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de destinatari donat el seu id (destinatariId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
			// #779: Obtenim la certificació de forma automàtica
			if (enviament.getNotificaCertificacioArxiuId() == null &&
					( EnviamentEstat.REBUTJADA.equals(enviament.getNotificaEstat()) ||
					EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat()) )) {
				try {
					notificaHelper.enviamentRefrescarEstat(enviamentId);
					notificacioEnviamentRepository.flush();
					enviament = notificacioEnviamentRepository.findById(enviamentId);
				} catch (Exception ex) {
					log.error("No s'ha pogut actualitzar la certificació de l'enviament amb id: " + enviamentId, ex);
				}
			}
			//NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
			entityComprovarHelper.comprovarPermisos(null, false, false, false);
			return enviamentToDto(enviament);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(
			Long entitatId,
			RolEnumDto rol,
			String organGestorCodi,
			String usuariCodi,
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws ParseException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els enviaments de les notificacións que te una entitat");
			boolean isUsuari = RolEnumDto.tothom.equals(rol);
			boolean isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
			boolean isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
			boolean isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat, false);
			Page<EnviamentTableEntity> pageEnviaments = null;
			logger.info("Consulta de taula d'enviaments ...");
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("enviamentDataProgramada", new String[] {"enviamentDataProgramada"});
			mapeigPropietatsOrdenacio.put("notificaIdentificador", new String[] {"notificaIdentificador"});
			mapeigPropietatsOrdenacio.put("procedimentCodiNotib", new String[] {"procedimentCodiNotib"});
			mapeigPropietatsOrdenacio.put("grupCodi", new String[] {"grupCodi"});
			mapeigPropietatsOrdenacio.put("emisorDir3Codi", new String[] {"emisorDir3Codi"});
			mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
			mapeigPropietatsOrdenacio.put("concepte", new String[] {"concepte"});
			mapeigPropietatsOrdenacio.put("descripcio", new String[] {"descripcio"});
			mapeigPropietatsOrdenacio.put("titularNif", new String[] {"titularNif"});
			mapeigPropietatsOrdenacio.put("titularNomLlinatge", new String[] {"concat(titularLlinatge1, titularLlinatge2, titularNom)"});
			mapeigPropietatsOrdenacio.put("titularEmail", new String[] {"titularEmail"});
			mapeigPropietatsOrdenacio.put("llibre", new String[] {"registreLlibreNom"});
			mapeigPropietatsOrdenacio.put("registreNumero", new String[] {"registreNumero"});
			mapeigPropietatsOrdenacio.put("notificaDataCaducitat", new String[] {"notificaDataCaducitat"});
			mapeigPropietatsOrdenacio.put("notificaCertificacioNumSeguiment", new String[] {"notificaCertificacioNumSeguiment"});
			mapeigPropietatsOrdenacio.put("csvUuid", new String[] {"csv_uuid"});
			mapeigPropietatsOrdenacio.put("estat", new String[] {"estat"});
			mapeigPropietatsOrdenacio.put("codiNotibEnviament", new String[] {"notificaReferencia"});
			mapeigPropietatsOrdenacio.put("referenciaNotificacio", new String[] {"referenciaNotificacio"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			NotificacioEnviamentFiltre filtreFields = getFiltre(entitatId, filtre);
			if (isUsuari) { // && !procedimentsCodisNotib.isEmpty()) {
				List<String> codisProcedimentsDisponibles = new ArrayList<>();
				List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
				List<String> codisProcedimentsOrgans = new ArrayList<>();

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Permission[] permisos = entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);

				// Procediments accessibles per qualsevol òrgan gestor
				codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatEntity, PermisEnum.CONSULTA);

				// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
				codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatEntity, PermisEnum.CONSULTA);

				// Procediments comuns que es poden consultar per a òrgans gestors concrets
				codisProcedimentsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitatEntity.getId(), auth.getName(), PermisEnum.CONSULTA);

				boolean esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
				boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
				boolean esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());

				pageEnviaments = enviamentTableRepository.find4UserRole(
						filtreFields.codiProcediment.isNull(),
						filtreFields.codiProcediment.getField(),
						filtreFields.grup.isNull(),
						filtreFields.grup.getField(),
						filtreFields.concepte.isNull(),
						filtreFields.concepte.getField(),
						filtreFields.descripcio.isNull(),
						filtreFields.descripcio.getField(),
						filtreFields.dataProgramadaDisposicioInici.isNull(),
						filtreFields.dataProgramadaDisposicioInici.getField(),
						filtreFields.dataProgramadaDisposicioFi.isNull(),
						filtreFields.dataProgramadaDisposicioFi.getField(),
						filtreFields.dataCaducitatInici.isNull(),
						filtreFields.dataCaducitatInici.getField(),
						filtreFields.dataCaducitatFi.isNull(),
						filtreFields.dataCaducitatFi.getField(),
						filtreFields.enviamentTipus.isNull(),
						filtreFields.enviamentTipus.getField(),
						filtreFields.csvUuid.isNull(),
						filtreFields.csvUuid.getField(),
						filtreFields.estat.isNull(),
						filtreFields.estat.getField(),
						!filtreFields.estat.isNull() ? EnviamentEstat.valueOf(filtreFields.estat.getField().toString()) : null,
						entitatEntity,
						filtreFields.dataEnviamentInici.isNull(),
						filtreFields.dataEnviamentInici.getField(),
						filtreFields.dataEnviamentFi.isNull(),
						filtreFields.dataEnviamentFi.getField(),
						filtreFields.codiNotifica.isNull(),
						filtreFields.codiNotifica.getField(),
						filtreFields.creadaPer.isNull(),
						filtreFields.creadaPer.getField(),
						filtreFields.nifTitular.isNull(),
						filtreFields.nifTitular.getField(),
						filtreFields.nomTitular.isNull(),
						filtreFields.nomTitular.getField(),
						filtreFields.emailTitular.isNull(),
						filtreFields.emailTitular.getField(),
						filtreFields.dir3Codi.isNull(),
						filtreFields.dir3Codi.getField(),
						filtreFields.numeroCertCorreus.isNull(),
						filtreFields.numeroCertCorreus.getField(),
						filtreFields.usuari.isNull(),
						filtreFields.usuari.getField(),
						filtreFields.registreNumero.isNull(),
						filtreFields.registreNumero.getField(),
						filtreFields.codiNotibEnviament.isNull(),
						filtreFields.codiNotibEnviament.getField(),
						filtreFields.dataRegistreInici.isNull(),
						filtreFields.dataRegistreInici.getField(),
						filtreFields.dataRegistreFi.isNull(),
						filtreFields.dataRegistreFi.getField(),
						esProcedimentsCodisNotibNull,
						esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
						esOrgansGestorsCodisNotibNull,
						esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
						esProcedimentOrgansAmbPermisNull,
						esProcedimentOrgansAmbPermisNull ? null : codisProcedimentsOrgans,
						aplicacioService.findRolsUsuariActual(),
						usuariCodi,
						filtreFields.nomesAmbErrors,
						filtreFields.nomesSenseErrors,
						filtreFields.hasZeronotificaEnviamentIntent.isNull(),
						filtreFields.hasZeronotificaEnviamentIntent.getField(),
						filtreFields.referenciaNotificacio.isNull(),
						filtreFields.referenciaNotificacio.getField(),
						pageable);
			} else if (isAdminOrgan) { // && !procedimentsCodisNotib.isEmpty()) {
				List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatEntity.getDir3Codi(), organGestorCodi);
				pageEnviaments = enviamentTableRepository.find4OrganAdminRole(
						filtreFields.codiProcediment.isNull(),
						filtreFields.codiProcediment.getField(),
						filtreFields.grup.isNull(),
						filtreFields.grup.getField(),
						filtreFields.concepte.isNull(),
						filtreFields.concepte.getField(),
						filtreFields.descripcio.isNull(),
						filtreFields.descripcio.getField(),
						filtreFields.dataProgramadaDisposicioInici.isNull(),
						filtreFields.dataProgramadaDisposicioInici.getField(),
						filtreFields.dataProgramadaDisposicioFi.isNull(),
						filtreFields.dataProgramadaDisposicioFi.getField(),
						filtreFields.dataCaducitatInici.isNull(),
						filtreFields.dataCaducitatInici.getField(),
						filtreFields.dataCaducitatFi.isNull(),
						filtreFields.dataCaducitatFi.getField(),
						filtreFields.enviamentTipus.isNull(),
						filtreFields.enviamentTipus.getField(),
						filtreFields.csvUuid.isNull(),
						filtreFields.csvUuid.getField(),
						filtreFields.estat.isNull(),
						filtreFields.estat.getField(),
						!filtreFields.estat.isNull() ? EnviamentEstat.valueOf(filtreFields.estat.getField().toString()) : null,
						entitatEntity,
						filtreFields.dataEnviamentInici.isNull(),
						filtreFields.dataEnviamentInici.getField(),
						filtreFields.dataEnviamentFi.isNull(),
						filtreFields.dataEnviamentFi.getField(),
						filtreFields.codiNotifica.isNull(),
						filtreFields.codiNotifica.getField(),
						filtreFields.creadaPer.isNull(),
						filtreFields.creadaPer.getField(),
						filtreFields.nifTitular.isNull(),
						filtreFields.nifTitular.getField(),
						filtreFields.nomTitular.isNull(),
						filtreFields.nomTitular.getField(),
						filtreFields.emailTitular.isNull(),
						filtreFields.emailTitular.getField(),
						filtreFields.dir3Codi.isNull(),
						filtreFields.dir3Codi.getField(),
						filtreFields.numeroCertCorreus.isNull(),
						filtreFields.numeroCertCorreus.getField(),
						filtreFields.usuari.isNull(),
						filtreFields.usuari.getField(),
						filtreFields.registreNumero.isNull(),
						filtreFields.registreNumero.getField(),
						filtreFields.codiNotibEnviament.isNull(),
						filtreFields.codiNotibEnviament.getField(),
						filtreFields.dataRegistreInici.isNull(),
						filtreFields.dataRegistreInici.getField(),
						filtreFields.dataRegistreFi.isNull(),
						filtreFields.dataRegistreFi.getField(),
						filtreFields.nomesAmbErrors,
						filtreFields.nomesSenseErrors,
						filtreFields.hasZeronotificaEnviamentIntent.isNull(),
						filtreFields.hasZeronotificaEnviamentIntent.getField(),
						filtreFields.referenciaNotificacio.isNull(),
						filtreFields.referenciaNotificacio.getField(),
						organs,
						pageable);
			} else if (isUsuariEntitat) {
				long ti = System.nanoTime();
				if (filtreFields.isEmpty()) {
					pageEnviaments = enviamentTableRepository.find4EntitatAdminRole(entitatEntity, pageable);
				} else {
					pageEnviaments = enviamentTableRepository.find4EntitatAdminRole(
							filtreFields.codiProcediment.isNull(),
							filtreFields.codiProcediment.getField(),
							filtreFields.grup.isNull(),
							filtreFields.grup.getField(),
							filtreFields.concepte.isNull(),
							filtreFields.concepte.getField(),
							filtreFields.descripcio.isNull(),
							filtreFields.descripcio.getField(),
							filtreFields.dataProgramadaDisposicioInici.isNull(),
							filtreFields.dataProgramadaDisposicioInici.getField(),
							filtreFields.dataProgramadaDisposicioFi.isNull(),
							filtreFields.dataProgramadaDisposicioFi.getField(),
							filtreFields.dataCaducitatInici.isNull(),
							filtreFields.dataCaducitatInici.getField(),
							filtreFields.dataCaducitatFi.isNull(),
							filtreFields.dataCaducitatFi.getField(),
							filtreFields.enviamentTipus.isNull(),
							filtreFields.enviamentTipus.getField(),
							filtreFields.csvUuid.isNull(),
							filtreFields.csvUuid.getField(),
							filtreFields.estat.isNull(),
							filtreFields.estat.getField(),
							!filtreFields.estat.isNull() ? EnviamentEstat.valueOf(filtreFields.estat.getField().toString()) : null,
							entitatEntity,
							filtreFields.dataEnviamentInici.isNull(),
							filtreFields.dataEnviamentInici.getField(),
							filtreFields.dataEnviamentFi.isNull(),
							filtreFields.dataEnviamentFi.getField(),
							filtreFields.codiNotifica.isNull(),
							filtreFields.codiNotifica.getField(),
							filtreFields.creadaPer.isNull(),
							filtreFields.creadaPer.getField(),
							filtreFields.nifTitular.isNull(),
							filtreFields.nifTitular.getField(),
							filtreFields.nomTitular.isNull(),
							filtreFields.nomTitular.getField(),
							filtreFields.emailTitular.isNull(),
							filtreFields.emailTitular.getField(),
							filtreFields.dir3Codi.isNull(),
							filtreFields.dir3Codi.getField(),
							filtreFields.numeroCertCorreus.isNull(),
							filtreFields.numeroCertCorreus.getField(),
							filtreFields.usuari.isNull(),
							filtreFields.usuari.getField(),
							filtreFields.registreNumero.isNull(),
							filtreFields.registreNumero.getField(),
							filtreFields.codiNotibEnviament.isNull(),
							filtreFields.codiNotibEnviament.getField(),
							filtreFields.dataRegistreInici.isNull(),
							filtreFields.dataRegistreInici.getField(),
							filtreFields.dataRegistreFi.isNull(),
							filtreFields.dataRegistreFi.getField(),
							filtreFields.nomesAmbErrors,
							filtreFields.nomesSenseErrors,
							filtreFields.hasZeronotificaEnviamentIntent.isNull(),
							filtreFields.hasZeronotificaEnviamentIntent.getField(),
							filtreFields.referenciaNotificacio.isNull(),
							filtreFields.referenciaNotificacio.getField(),
							pageable);
				}
				logger.info(String.format("Consulta enviaments: %f ms", (System.nanoTime() - ti) / 1e6));
			}
			if(pageEnviaments == null || !pageEnviaments.hasContent()) {
				pageEnviaments = new PageImpl<>(new ArrayList<EnviamentTableEntity>());
			}
			PaginaDto<NotEnviamentTableItemDto> paginaDto = paginacioHelper.toPaginaDto(pageEnviaments, NotEnviamentTableItemDto.class);
			return paginaDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	public NotificacioEnviamentFiltre getFiltre(Long entitatId, NotificacioEnviamentFiltreDto filtreDto) throws ParseException {

		Date dataEnviamentInici = null,
				dataEnviamentFi = null,
				dataProgramadaDisposicioInici = null,
				dataProgramadaDisposicioFi = null,
				dataRegistreInici = null,
				dataRegistreFi = null,
				dataCaducitatInici = null,
				dataCaducitatFi = null;

		if (filtreDto.getDataEnviamentInici() != null && filtreDto.getDataEnviamentInici() != "") {
			dataEnviamentInici = FiltreHelper.toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataEnviamentInici()));
		}
		if (filtreDto.getDataEnviamentFi() != null && filtreDto.getDataEnviamentFi() != "") {
			dataEnviamentFi = FiltreHelper.toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataEnviamentFi()));
		}
		if (filtreDto.getDataProgramadaDisposicioInici() != null && filtreDto.getDataProgramadaDisposicioInici() != "") {
			dataProgramadaDisposicioInici = FiltreHelper.toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataProgramadaDisposicioInici()));
		}
		if (filtreDto.getDataProgramadaDisposicioFi() != null && filtreDto.getDataProgramadaDisposicioFi() != "") {
			dataProgramadaDisposicioFi = FiltreHelper.toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataProgramadaDisposicioFi()));
		}
		if (filtreDto.getDataRegistreInici() != null && filtreDto.getDataRegistreInici() != "") {
			dataRegistreInici = FiltreHelper.toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataRegistreInici()));
		}
		if (filtreDto.getDataRegistreFi() != null && filtreDto.getDataRegistreFi() != "") {
			dataRegistreFi = FiltreHelper.toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataRegistreFi()));
		}
		if (filtreDto.getDataCaducitatInici() != null && filtreDto.getDataCaducitatInici() != "") {
			dataCaducitatInici = FiltreHelper.toIniciDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataCaducitatInici()));
		}
		if (filtreDto.getDataCaducitatFi() != null && filtreDto.getDataCaducitatFi() != "") {
			dataCaducitatFi = FiltreHelper.toFiDia(new SimpleDateFormat("dd/MM/yyyy").parse(filtreDto.getDataCaducitatFi()));
		}

		NotificacioEstatEnumDto estat = filtreDto.getEstat();
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
		NotificaEnviamentTipusEnumDto tipusEnviament = null;
		if(filtreDto.getEnviamentTipus()!=null) {
			if (NotificacioTipusEnviamentEnumDto.notificacio.equals(filtreDto.getEnviamentTipus())) {
				tipusEnviament = NotificaEnviamentTipusEnumDto.NOTIFICACIO;
			}else {
				tipusEnviament = NotificaEnviamentTipusEnumDto.COMUNICACIO;
			}
		}

		return NotificacioEnviamentFiltre.builder()
				.entitatId(new FiltreField<>(entitatId))
				.dataEnviamentInici(new FiltreField<>(dataEnviamentInici))
				.dataEnviamentFi(new FiltreField<>(dataEnviamentFi))
				.dataProgramadaDisposicioInici(new FiltreField<>(dataProgramadaDisposicioInici))
				.dataProgramadaDisposicioFi(new FiltreField<>(dataProgramadaDisposicioFi))
				.codiNotifica(new StringField(filtreDto.getCodiNotifica()))
				.codiProcediment(new StringField(filtreDto.getCodiProcediment()))
				.grup(new StringField(filtreDto.getGrup()))
				.usuari(new StringField(filtreDto.getUsuari()))
				.enviamentTipus(new FiltreField<>(tipusEnviament))
				.concepte(new StringField(filtreDto.getConcepte()))
				.descripcio(new StringField(filtreDto.getDescripcio()))
				.nifTitular(new StringField(filtreDto.getNifTitular()))
				.nomTitular(new StringField(filtreDto.getNomTitular()))
				.nomTitular(new StringField(filtreDto.getTitularNomLlinatge()))
				.emailTitular(new StringField(filtreDto.getEmailTitular()))
//				.destinataris(new FiltreField<>(dataProgramadaDisposicioFi))
//				.registreLlibre(new FiltreField<>(dataProgramadaDisposicioFi))
				.registreNumero(new StringField(filtreDto.getRegistreNumero()))
				.dataProgramadaDisposicioFi(new FiltreField<>(dataProgramadaDisposicioFi))
				.dataRegistreInici(new FiltreField<>(dataRegistreInici))
				.dataRegistreFi(new FiltreField<>(dataRegistreFi))
				.dataCaducitatInici(new FiltreField<>(dataCaducitatInici))
				.dataCaducitatFi(new FiltreField<>(dataCaducitatFi))
				.codiNotibEnviament(new StringField(filtreDto.getCodiNotibEnviament()))
				.numeroCertCorreus(new StringField(filtreDto.getNumeroCertCorreus()))
				.csvUuid(new StringField(filtreDto.getCsvUuid()))
				.estat(new FiltreField<>(estat))
				.dir3Codi(new StringField(filtreDto.getDir3Codi()))
//				.titularNomLlinatge(new FiltreField<>(dataProgramadaDisposicioFi))
				.creadaPer(new StringField(filtreDto.getCreatedBy() != null ? filtreDto.getCreatedBy().getCodi() : null))
				.nomesSenseErrors(nomesSenseErrors)
				.nomesAmbErrors(nomesAmbErrors)
				.hasZeronotificaEnviamentIntent(new FiltreField<Boolean>(hasZeronotificaEnviamentIntent))
				.referenciaNotificacio(new StringField(filtreDto.getReferenciaNotificacio()))
				.build();
	}
	@Builder
	@Getter
	@Setter
	public static class NotificacioEnviamentFiltre implements Serializable {

		private FiltreField<Long> entitatId;

		private FiltreField<Date> dataEnviamentInici;
		private FiltreField<Date> dataEnviamentFi;
		private FiltreField<Date> dataProgramadaDisposicioInici;
		private FiltreField<Date> dataProgramadaDisposicioFi;
		private StringField codiNotifica;
		private StringField codiProcediment;
		private StringField grup;
		private StringField usuari;
		private FiltreField<NotificaEnviamentTipusEnumDto> enviamentTipus;
		private StringField concepte;
		private StringField descripcio;
		private StringField nifTitular;
		private StringField nomTitular;
		private StringField emailTitular;
//		private String destinataris;
//		private String registreLlibre;
		private StringField registreNumero;
		private FiltreField<Date> dataRegistreInici;
		private FiltreField<Date> dataRegistreFi;
		private FiltreField<Date> dataCaducitatInici;
		private FiltreField<Date> dataCaducitatFi;
		private StringField codiNotibEnviament;
		private StringField numeroCertCorreus;
		private StringField csvUuid;
		private FiltreField<NotificacioEstatEnumDto> estat;
		private StringField dir3Codi;
//		private String titularNomLlinatge;
//		private String uuid;
		private StringField creadaPer;

		private boolean nomesSenseErrors;
		private boolean nomesAmbErrors;
		private FiltreField<Boolean> hasZeronotificaEnviamentIntent;
		private StringField referenciaNotificacio;

//		private FiltreField<NotificacioComunicacioTipusEnumDto> comunicacioTipus;
//		private FiltreField<Date> dataInici;
//		private FiltreField<Date> dataFi;
//		private StringField titular;
//		private FiltreField<OrganGestorEntity> organGestor;
//		private FiltreField<ProcedimentEntity> procediment;
//		private FiltreField<TipusUsuariEnumDto> tipusUsuari;
//		private StringField numExpedient;
//		private StringField identificador;
//		private FiltreField<Boolean> nomesAmbErrors;
//		private FiltreField<Boolean> nomesSenseErrors;
//		private FiltreField<Boolean> hasZeronotificaEnviamentIntent;

		public boolean isEmpty() {
			if (!entitatId.isNull()) return false;
			if (!dataEnviamentInici.isNull()) return false;
			if (!dataEnviamentFi.isNull()) return false;
			if (!dataProgramadaDisposicioInici.isNull()) return false;
			if (!dataProgramadaDisposicioFi.isNull()) return false;
			if (!codiNotifica.isNull()) return false;
			if (!codiProcediment.isNull()) return false;
			if (!grup.isNull()) return false;
			if (!usuari.isNull()) return false;
			if (!enviamentTipus.isNull()) return false;
			if (!concepte.isNull()) return false;
			if (!descripcio.isNull()) return false;
			if (!nifTitular.isNull()) return false;
			if (!nomTitular.isNull()) return false;
			if (!emailTitular.isNull()) return false;
			if (!registreNumero.isNull()) return false;
			if (!dataRegistreInici.isNull()) return false;
			if (!dataRegistreFi.isNull()) return false;
			if (!dataCaducitatInici.isNull()) return false;
			if (!dataCaducitatFi.isNull()) return false;
			if (!codiNotibEnviament.isNull()) return false;
			if (!numeroCertCorreus.isNull()) return false;
			if (!csvUuid.isNull()) return false;
			if (!estat.isNull()) return false;
			if (!dir3Codi.isNull()) return false;
			if (!creadaPer.isNull()) return false;
			if (!hasZeronotificaEnviamentIntent.isNull()) return false;
			if (!referenciaNotificacio.isNull()) return false;
			return true;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> enviamentIds,
			String format) throws IOException, ParseException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Exportant informació dels enviaments (" +
					"entitatId=" + entitatId + ", " +
					"enviamentsIds=" + enviamentIds + ", " +
					"format=" + format + ")");

			EntitatEntity entitatEntity = entitatRepository.findOne(entitatId);
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);


			List<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findByIdIn(enviamentIds);

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

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			List<String[]> files = new ArrayList<String[]>();
			boolean llibreOrgan = !entitatEntity.isLlibreEntitat();
			
			for (NotificacioEnviamentEntity enviament : enviaments) {
				String[] fila = new String[numColumnes];
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
				fila[22] = (enviament.getNotificacio().getEstatDate() != null ? enviament.getNotificacio().getEstatDate() + "" : "");
				fila[23] = (enviament.getNotificacio().getEstatProcessatDate() != null ? enviament.getNotificacio().getEstatProcessatDate() + "" : "");
				files.add(fila);
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
	public void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId);
			UsuariEntity usuariEntity = usuariRepository.findByCodi(codiUsuari);
			
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
	public void columnesUpdate(Long entitatId, ColumnesDto columnes) {

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
					columnes.isEstat(),
					columnes.isReferenciaNotificacio());
	
			columnesRepository.saveAndFlush(columnesEntity);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
		
	@Transactional(readOnly = true)	
	@Override
	public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			UsuariEntity usuari = usuariRepository.findByCodi(codiUsuari);
			ColumnesEntity columnes = columnesRepository.findByEntitatAndUser(entitat, usuari);
			return conversioTipusHelper.convertir(columnes, ColumnesDto.class);
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
	public void reactivaConsultes(Set<Long> enviaments) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			for (Long enviamentId: enviaments) {
				NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
				enviament.refreshNotificaConsulta();
				List<NotificacioEventEntity> events = notificacioEventRepository.findByEnviamentAndTipusAndError(enviament, NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, true);
				for (NotificacioEventEntity event : events) {
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			for (Long enviamentId: enviaments) {
				NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
				enviament.refreshSirConsulta();
				enviamentHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "EnviamentServiceImpl.reactivaSir");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioEnviamentDto enviamentToDto(NotificacioEnviamentEntity enviament) {

		//		enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacioId()));
		NotificacioEnviamentDto enviamentDto = conversioTipusHelper.convertir(enviament, NotificacioEnviamentDto.class);
		enviamentDto.setRegistreNumeroFormatat(enviament.getRegistreNumeroFormatat());
		enviamentDto.setRegistreData(enviament.getRegistreData());
		enviamentDto.setNotificaCertificacioArxiuNom(calcularNomArxiuCertificacio(enviament));
		CallbackEntity callback = callbackRepository.findByEnviamentIdAndEstat(enviament.getId(), CallbackEstatEnumDto.PENDENT);
		if (callback != null) {
			enviamentDto.setCallbackPendent(callback != null);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			enviamentDto.setCallbackData(callback.getData() != null ? df.format(callback.getData()) : null);
		}
		NotificacioEventEntity event = enviament.getNotificacioErrorEvent();
		if (event != null && event.getFiReintents()) {
			enviamentDto.setFiReintents(event.getFiReintents());
			String msg = messageHelper.getMessage("notificacio.event.fi.reintents");
			String tipus = messageHelper.getMessage("es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto." + event.getTipus());
			enviamentDto.setFiReintentsDesc(msg + " -> " + tipus);
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertir(notificacioEnviamentRepository.findOne(enviamentId), NotificacioEnviamentDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	public byte[] getDocumentJustificant(Long enviamentId) {
		
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
		if (enviament.getRegistreEstat() != null && enviament.getRegistreEstat().equals(NotificacioRegistreEstatEnumDto.OFICI_EXTERN)) {
			return pluginHelper.obtenirOficiExtern(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();
		}
		return pluginHelper.obtenirJustificant(enviament.getNotificacio().getEmisorDir3Codi(), enviament.getRegistreNumeroFormatat()).getJustificant();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Resposta findEnviaments(ApiConsulta consulta) {

		consulta.setVisibleCarpeta(null);
		PaginaEnviaments paginaEnviaments = findEnviamentsByConsulta(consulta);
		Resposta resposta = new Resposta();
		resposta.setNumeroElementsTotals(paginaEnviaments.getNumEnviaments());
		resposta.setNumeroElementsRetornats(paginaEnviaments.getNumeroEnviamentsRetornats());
		List<NotificacioEnviamentDto> dtos = conversioTipusHelper.convertirList(paginaEnviaments.getEnviaments(), NotificacioEnviamentDto.class);
		resposta.setResultat(dtosToTransmissions(dtos, consulta.getBasePath()));
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaV2 findEnviamentsV2(ApiConsulta consulta) {
		PaginaEnviaments paginaEnviaments = findEnviamentsByConsulta(consulta);
		RespostaConsultaV2 resposta = RespostaConsultaV2.builder()
				.numeroElementsTotals(paginaEnviaments.getNumEnviaments())
				.numeroElementsRetornats(paginaEnviaments.getNumeroEnviamentsRetornats())
				.resultat(paginaEnviaments.getTransmissionsV2(consulta.getBasePath(), consulta.getIdioma().name().toLowerCase()))
				.build();
		return resposta;
	}

	private PaginaEnviaments findEnviamentsByConsulta(ApiConsulta consulta) {
		Date dataInicial = consulta.getDataInicial() != null ? FiltreHelper.toIniciDia(consulta.getDataInicial()) : null;
		Date dataFinal = consulta.getDataFinal() != null ? FiltreHelper.toFiDia(consulta.getDataFinal()) : null;
		Integer numEnviaments = notificacioEnviamentRepository.countEnviaments(
				consulta.getDniTitular(),
				dataInicial == null,
				dataInicial,
				dataFinal == null,
				dataFinal,
				consulta.getTipus(),
				consulta.getEstatFinal() == null,
				consulta.getEstatFinal(),
				consulta.getVisibleCarpeta() == null,
				consulta.getVisibleCarpeta());
		Page<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findEnviaments(
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

		return PaginaEnviaments.builder().messageHelper(messageHelper).numEnviaments(numEnviaments).enviaments(enviaments.getContent()).locale(new Locale(consulta.getIdioma().name())).build();
	}

	private Pageable getPageable(Integer pagina, Integer mida) {
		Pageable pageable = new PageRequest(0, 999999999);
		if (pagina != null && mida != null) {
			pageable = new PageRequest(pagina, mida);
		}
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
		NotificacioDto not = enviament.getNotificacio();
		transmissio.setEmisor(not.getEntitat().getCodi());
		transmissio.setOrganGestor(not.getOrganGestor());
		if (not.getProcediment() != null) {
			transmissio.setProcediment(not.getProcediment().getCodi());
		}
		transmissio.setNumExpedient(not.getNumExpedient());
		transmissio.setConcepte(not.getConcepte());
		transmissio.setDescripcio(not.getDescripcio());
		Date dataProgramada = not.getEnviamentDataProgramada();
		if (dataProgramada == null) {
			transmissio.setDataEnviament(not.getNotificaEnviamentData());
		} else {
			transmissio.setDataEnviament(dataProgramada);
		}

		transmissio.setEstat(getEstat(enviament));
		Date data = not.getEstatDate() != null ? not.getEstatDate() :
				(NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat()) ? not.getRegistreData()
						: NotificacioEstatEnumDto.PENDENT.equals(not.getEstat()) ? not.getCreatedDate()
						: (NotificacioEstatEnumDto.ENVIADA.equals(not.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(not.getEstat())) ? not.getNotificaEnviamentData()
						: not.getCreatedDate());

		transmissio.setDataEstat(data);
		if (not.getDocument() != null) {
			es.caib.notib.core.api.rest.consulta.Document document = es.caib.notib.core.api.rest.consulta.Document.builder()
					.nom(not.getDocument().getArxiuNom())
					.mediaType(not.getDocument().getMediaType())
					.mida(not.getDocument().getMida())
					.url(basePath + "/document/" + not.getId()).build();
			transmissio.setDocument(document);
		}
		transmissio.setTitular(toPersona(enviament.getTitular()));
		List<Persona> destinataris = new ArrayList<Persona>();
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
		if (NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat()) &&
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

	// Recuperar l'estat a partir de l'enviament, i no de la notificació.
	//  A més, no s'han d'eliminar els estat enviada_amb_errors i finalitzada_amb_errors.
	private Estat getEstat(NotificacioEnviamentDto enviament) {
		NotificacioDto notificacio = enviament.getNotificacio();

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
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA :
						enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA : Estat.REGISTRADA;
			case ENVIADA_AMB_ERRORS:
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA :
						enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA :
						enviament.isEnviamentEnviat() ? Estat.ENVIADA : Estat.REGISTRADA;
			default:
				return enviament.isEnviamentProcessat() ? Estat.PROCESSADA :
						enviament.isEnviamentFinalitzat() ? Estat.FINALITZADA :
						enviament.isEnviamentEnviat() ? Estat.ENVIADA :
								EnviamentEstat.REGISTRADA.equals(enviament.getNotificaEstat()) ? Estat.REGISTRADA : Estat.PENDENT;
		}
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

	@Transactional
	@Override
	public void actualitzarEstat(Long enviamentId) {

		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		enviament.refreshNotificaConsulta();
		enviament.refreshSirConsulta();

		if (enviament.getNotificacio().isComunicacioSir()) {
			// si l'enviament esta pendent de refrescar l'estat enviat SIR
			if (enviament.isPendentRefrescarEstatRegistre()) {
				notificacioService.enviamentRefrescarEstatRegistre(enviamentId);
			}
		} else {
			// si l'enviament esta pendent de refrescar estat a notifica
			if (enviament.isPendentRefrescarEstatNotifica()) {
				notificacioService.enviamentRefrescarEstat(enviamentId);
			}
		}

	}

	@Transactional
	@Override
	public void activarCallback(Long enviamentId) {

		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		if (enviament.getNotificacio().isTipusUsuariAplicacio()) {
			logger.info(String.format("[callback] Reactivam callback de l'enviment [id=%d]", enviamentId));
			callbackHelper.reactivarCallback(enviament);
			return;
		}
		logger.info(String.format("[callback] No es pot reactivar el callback de l'enviment [id=%d] (Tipus usuari = %s, callbacks pendents = %d)",
					enviamentId, enviament.getNotificacio().getTipusUsuari().toString()));
	}

//	@Transactional
	@Override
	public List<Long> enviarCallback(Set<Long> notificacions) throws Exception {

		List<CallbackEntity> callbacks = callbackRepository.findByNotificacioIdIn(notificacions);
		List<Long> enviamentsAmbError = new ArrayList<>();
		for (CallbackEntity callback : callbacks) {
			logger.info(String.format("[callback] Enviar callback de l'enviment [id=%d]", callback.getEnviamentId()));
			boolean isError = callbackHelper.notifica(callback.getEnviamentId());
			if (isError)
				enviamentsAmbError.add(callback.getEnviamentId());
			else
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

		public List<TransmissioV2> getTransmissionsV2(String basePath, String lang) {
			List<TransmissioV2> transmissions = new ArrayList<>();
			if (enviaments != null) {
				for (NotificacioEnviamentEntity enviament: enviaments) {
					transmissions.add(toTransmissio(enviament, basePath, new Locale(lang)));
				}
			}
			return transmissions;
		}

		private TransmissioV2 toTransmissio(NotificacioEnviamentEntity enviament, String basePath, Locale locale) {

			NotificacioEntity not = enviament.getNotificacio();
			// Organ
			GenericInfo organGestor = GenericInfo.builder().codi(not.getOrganGestor().getCodi()).nom(not.getOrganGestor().getNom()).build();
			// Procediment
			GenericInfo procediment = null;
			if (not.getProcediment() != null)
				procediment = GenericInfo.builder().codi(not.getProcediment().getCodi()).nom(not.getProcediment().getNom()).build();
			// Estat
			GenericInfo estat = getEstat(enviament);
			Date dataEstat = getEstatDate(enviament);
			// Document
			DocumentConsultaV2 document = null;
			if (not.getDocument() != null) {
				document = DocumentConsultaV2.builder()
						.nom(not.getDocument().getArxiuNom())
						.mediaType(not.getDocument().getMediaType())
						.mida(not.getDocument().getMida())
						.url(basePath + "/document/" + not.getId()).build();
			}
			// Titular
			PersonaConsultaV2 titular = toPersona(enviament.getTitular());
			// Destinataris
			List<PersonaConsultaV2> destinataris = new ArrayList<>();
			if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
				for (PersonaEntity destinatari: enviament.getDestinataris()) {
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
				NotificacioEventEntity event = enviament.getNotificacioErrorEvent();
				if (event != null) {
					errorData = event.getData();
					errorDescripcio = event.getErrorDescripcio();
				}
			}

			TransmissioV2 transmissio = TransmissioV2.builder()
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

			return transmissio;
		}

		// Recuperar l'estat a partir de l'enviament, i no de la notificació.
		//  A més, no s'han d'eliminar els estat enviada_amb_errors i finalitzada_amb_errors.
		private GenericInfo getEstat(NotificacioEnviamentEntity enviament) {
			switch (enviament.getNotificaEstat()) {
				case NOTIB_PENDENT:
				case REGISTRADA:
				case NOTIB_ENVIADA:
				case ENVIAMENT_PROGRAMAT:
					return GenericInfo.builder().codi("EN TRAMITACIO").nom(getNom("EN_TRAMITACIO")).descripcio(getDesc("EN_TRAMITACIO")).build();
				case ABSENT:
					return GenericInfo.builder().codi("ABSENT").nom(getNom("ABSENT")).descripcio(getDesc("ABSENT")).build();
				case ADRESA_INCORRECTA:
					return GenericInfo.builder().codi("ADREÇA INCORRECTA").nom(getNom("ADRESA_INCORRECTA")).descripcio(getDesc("ADRESA_INCORRECTA")).build();
				case DESCONEGUT:
					return GenericInfo.builder().codi("DESCONEGUT").nom(getNom("DESCONEGUT")).descripcio(getDesc("DESCONEGUT")).build();
				case ENVIADA:
				case ENVIADA_CI:
				case ENVIADA_DEH:
				case ENTREGADA_OP:
				case PENDENT:
				case PENDENT_ENVIAMENT:
				case PENDENT_SEU:
				case PENDENT_CIE:
				case PENDENT_DEH:
					return GenericInfo.builder().codi("PENDENT COMPAREIXENÇA").nom(getNom("PENDENT")).descripcio(getDesc("PENDENT")).build();
				case ERROR_ENTREGA:
					return GenericInfo.builder().codi("ERROR").nom(getNom("ERROR")).descripcio(getDesc("ERROR")).build();
				case EXPIRADA:
					return GenericInfo.builder().codi("EXPIRADA").nom(getNom("EXPIRADA")).descripcio(getDesc("EXPIRADA")).build();
				case EXTRAVIADA:
					return GenericInfo.builder().codi("EXTRAVIADA").nom(getNom("EXTRAVIADA")).descripcio(getDesc("EXTRAVIADA")).build();
				case MORT:
					return GenericInfo.builder().codi("DIFUNT").nom(getNom("DIFUNT")).descripcio(getDesc("DIFUNT")).build();
				case LLEGIDA:
					return GenericInfo.builder().codi("LLEGIDA").nom(getNom("LLEGIDA")).descripcio(getDesc("LLEGIDA")).build();
				case NOTIFICADA:
					return GenericInfo.builder().codi("ACCEPTADA").nom(getNom("ACCEPTADA")).descripcio(getDesc("ACCEPTADA")).build();
				case REBUTJADA:
					return GenericInfo.builder().codi("REBUTJADA").nom(getNom("REBUTJADA")).descripcio(getDesc("REBUTJADA")).build();
				case SENSE_INFORMACIO:
					return GenericInfo.builder().codi("SENSE INFORMACIO").nom(getNom("SENSE_INFORMACIO")).descripcio(getDesc("SENSE_INFORMACIO")).build();
				case ANULADA:
					return GenericInfo.builder().codi("ANULADA").nom(getNom("ANULADA")).descripcio(getDesc("ANULADA")).build();
				case ENVIAT_SIR:
					return GenericInfo.builder().codi("ENVIAT SIR").nom(getNom("ENVIADA_SIR")).descripcio(getDesc("ENVIADA_SIR")).build();

				case FINALITZADA:
					if (enviament.isPerEmail()) {
						return GenericInfo.builder().codi("ENVIAT EMAIL").nom(getNom("ENVIADA_EMAIL")).descripcio(getDesc("ENVIADA_EMAIL")).build();
					}
				case PROCESSADA:
				case ENVIADA_AMB_ERRORS:
				case FINALITZADA_AMB_ERRORS:
				default:
					return GenericInfo.builder().codi("ERROR").nom(getNom("ERROR")).descripcio(messageHelper.getMessage("enviament.estat.ERROR", null, locale)).build();
			}
		}

		private String getNom(String codi) {
			return messageHelper.getMessage("enviament.estat." + codi + ".nom", null, locale);
		}
		private String getDesc(String codi) {
			return messageHelper.getMessage("enviament.estat." + codi + ".desc", null, locale);
		}

		private Date getEstatDate(NotificacioEnviamentEntity enviament) {

			if (enviament.getNotificaEstatData() != null)
				return enviament.getNotificaEstatData();

			NotificacioEntity not = enviament.getNotificacio();
			if (not.getEstatDate() != null)
				return not.getEstatDate();
			if (NotificacioEstatEnumDto.REGISTRADA.equals(not.getEstat())) {
				return not.getRegistreData();
			} else if (NotificacioEstatEnumDto.ENVIADA.equals(not.getEstat()) || NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(not.getEstat())) {
				return not.getNotificaEnviamentData();
			}
			return not.getCreatedDate().toDate();
		}

		private PersonaConsultaV2 toPersona(PersonaEntity personaEntity) {
			InteressatTipusEnumDto tipus = personaEntity.getInteressatTipus();
			String nom = personaEntity.getNom();
			if (!InteressatTipusEnumDto.FISICA.equals(tipus)) {
				if (personaEntity.getRaoSocial() != null && !personaEntity.getRaoSocial().isEmpty()) {
					nom = personaEntity.getRaoSocial();
				}
			}
			PersonaConsultaV2 persona= PersonaConsultaV2.builder()
					.tipus(GenericInfo.builder().codi(tipus.name()).nom(messageHelper.getMessage("interessatTipusEnumDto." + tipus.name())).build())
					.nom(nom)
					.llinatge1(personaEntity.getLlinatge1())
					.llinatge2(personaEntity.getLlinatge2())
					.nif(personaEntity.getNif())
					.email(personaEntity.getEmail())
					.build();
			return persona;
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(EnviamentServiceImpl.class);

}
