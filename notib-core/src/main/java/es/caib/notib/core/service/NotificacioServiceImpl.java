/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.EntregaDehDto;
import es.caib.notib.core.api.dto.EntregaPostalDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ParametresRegistreDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentReferencia;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.ProcedimentRepository;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class NotificacioServiceImpl implements NotificacioService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PropertiesHelper propertiesHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	
	@Transactional
	@Override
	public List<NotificacioDto> create(
			Long entitatId, 
			NotificacioDtoV2 notificacio) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		
		NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(
				entitat, 
				notificacio.getId());
		
		entityComprovarHelper.comprovarNotificacio(
				entitat, 
				notificacio.getId(), 
				false, 
				false, 
				true, 
				false);
		
		
		String documentGesdocId = null;
		GrupEntity grup = null;
		
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitat,
				 	notificacio.getProcediment().getId(),
				 	true,
				 	true,
				 	true,
				 	true);

		if (notificacio.getGrup().getId() != null) {
			grup = entityComprovarHelper.comprovarGrup(
						notificacio.getGrup().getId());
		}
		 
		if (notificacio.getDocument().getContingutBase64() != null) {
			documentGesdocId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					new ByteArrayInputStream(notificacio.getDocument().getContingutBase64()));
		} 
		// Dades generals de la notificació
		NotificacioEntity.Builder notificacioBuilder = NotificacioEntity.
				getBuilder(
						entitat,
						notificacio.getEmisorDir3Codi(), 
						notificacio.getComunicacioTipus(),
						notificacio.getEnviamentTipus(), 
						notificacio.getConcepte(), 
						notificacio.getDocument().getArxiuNom(), 
						documentGesdocId,
						notificacio.getDocument().getCsv() != null ? notificacio.getDocument().getCsv() : notificacio.getDocument().getUuid(),
						notificacio.getDocument().getHash(), 
						notificacio.getDocument().isNormalitzat(),
						notificacio.getDocument().isGenerarCsv()).
						descripcio(notificacio.getDescripcio()).
						caducitat(notificacio.getCaducitat()).
						descripcio(notificacio.getDescripcio()).
						procedimentCodiNotib(procediment.getCodi()).
						grupCodi(grup != null ? grup.getCodi() : null);

		/*
		 * Falta afegir paràmetres registre S'han llevat els paràmetres de la seu
		 */
		ParametresRegistreDto parametresRegistre = notificacio.getParametresRegistre();
		if (parametresRegistre != null) {
			notificacioBuilder.
			registreOficina(parametresRegistre.getOficina()).
			registreLlibre(parametresRegistre.getLlibre());
		}

		notificacioEntity = notificacioBuilder.build();
		notificacioRepository.saveAndFlush(notificacioEntity);

		List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
		PersonaDto titular = notificacio.getTitular();
		NotificacioEnviamentEntity.Builder enviamentBuilder = null;
		
		// Comprovar si hi ha titular
		if (titular != null) {

			NotificaServeiTipusEnumDto serveiTipus = null;
			if (notificacio.getServeiTipus() != null) {
				switch (notificacio.getServeiTipus()) {
				case NORMAL:
					serveiTipus = NotificaServeiTipusEnumDto.NORMAL;
					break;
				case URGENT:
					serveiTipus = NotificaServeiTipusEnumDto.URGENT;
					break;
				}
			}
			// Rellenar dades enviament titular
			enviamentBuilder = NotificacioEnviamentEntity.
					getBuilder(titular.getNif().toUpperCase(), serveiTipus, notificacioEntity).
					titularNom(titular.getNom()).
					titularLlinatge1(titular.getLlinatge1()).
					titularLlinatge2(titular.getLlinatge2()).
					titularTelefon(titular.getTelefon()).
					titularEmail(titular.getEmail()).
					titularRaoSocial(titular.getRaoSocial()).
					titularCodiDesti(titular.getCodiAdministracio());

			// Comprovar si hi ha destinataris
			if (notificacio.getDestinataris() != null) {
				// Afegir un enviament per cada destinatari
				for (PersonaDto destinatari : notificacio.getDestinataris()) {

					// Rellenar dades enviament titular
					enviamentBuilder = NotificacioEnviamentEntity.
							getBuilder(
									titular.getNif().toUpperCase(), 
									serveiTipus, 
									notificacioEntity).
							titularNom(titular.getNom()).
							titularLlinatge1(titular.getLlinatge1()).
							titularLlinatge2(titular.getLlinatge2()).
							titularTelefon(titular.getTelefon()).
							titularEmail(titular.getEmail()).
							titularRaoSocial(titular.getRaoSocial()).
							titularCodiDesti(titular.getCodiAdministracio());

					
					// Rellenar dades enviament destinatari
					enviamentBuilder.destinatariNif(destinatari.getNif().toUpperCase()).
					destinatariNom(destinatari.getNom()).
					destinatariLlinatge1(destinatari.getLlinatge1()).
					destinatariLlinatge2(destinatari.getLlinatge2()).
					destinatariTelefon(destinatari.getTelefon()).
					destinatariEmail(destinatari.getEmail()).
					destinatariRaoSocial(destinatari.getRaoSocial()).
					destinatariCodiDesti(destinatari.getCodiAdministracio());

					//Registra enviament per cada destinatari
					RellenarInformacioAdicional(
							notificacio, 
							notificacioEntity, 
							referencies, 
							enviamentBuilder, 
							titular);
				}
			}
			//Registra enviament titular
			RellenarInformacioAdicional(
					notificacio, 
					notificacioEntity, 
					referencies, 
					enviamentBuilder, 
					titular);
		}
		notificacioRepository.saveAndFlush(notificacioEntity);
		// Comprovar on s'ha d'enviar
		if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(notificacioEntity.getComunicacioTipus())) {
			notificaHelper.notificacioEnviar(notificacioEntity.getId());
			notificacioEntity = notificacioRepository.findOne(notificacioEntity.getId());
		}

		List<NotificacioEntity> notificacions = notificacioRepository.findByEntitatId(entitatId);

		return conversioTipusHelper.convertirList(
				notificacions, 
				NotificacioDto.class);
	}

	@Override
	public NotificacioDtoV2 update(
			Long entitatId,
			NotificacioDtoV2 procediment) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDto findAmbId(Long id) {
		logger.debug("Consulta de la notificacio amb id (id=" + id + ")");
		NotificacioEntity dto = notificacioRepository.findOne(id);
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		return  conversioTipusHelper.convertir(
				dto,
				NotificacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				true,
				false);
	
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
		List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
		PaginaDto<NotificacioDto> resultatPagina = null;
		Page<NotificacioEntity> notificacions = null;
		List<String> procedimentsCodisNotib = new ArrayList<String>();
		List<ProcedimentDto> procedimentsAmbPermis = new ArrayList<ProcedimentDto>();
		
		if (isUsuari) {
			procedimentsAmbPermis = entityComprovarHelper.findPermisConsultaProcedimentsUsuariActual();
		
			if (!procedimentsAmbPermis.isEmpty()) {
					for (ProcedimentDto procedimentDto : procedimentsAmbPermis) {
						procedimentsCodisNotib.add(procedimentDto.getCodi());
					}
			}
		}
		
		if (filtre == null) {
			//Consulta les notificacions sobre les quals té permis l'usuari actual
			if (isUsuari) {
				if (!procedimentsCodisNotib.isEmpty()) {
					notificacions = notificacioRepository.findByEntitatActualAndProcedimentCodiNotib(
							entitatActual,
							procedimentsCodisNotib,
							paginacioHelper.toSpringDataPageable(paginacioParams));
					}
			//Consulta els notificacions de l'entitat acutal
			} else if (isUsuariEntitat) {
				notificacions = notificacioRepository.findByEntitatActual(
						entitatActual,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			//Consulta totes les notificacions de les entitats actives
			} else if (isAdministrador) {
				notificacions = notificacioRepository.findByEntitatActiva(
						entitatsActiva,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
		} else {
				Date dataInici = filtre.getDataInici();
				if (dataInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataInici);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataInici = cal.getTime();
				}
				Date dataFi = filtre.getDataFi();
				if (dataFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataInici);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataFi = cal.getTime();
				}
				Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
				if (isUsuari) {
					if (!procedimentsCodisNotib.isEmpty()) {
						notificacions = notificacioRepository.findAmbFiltreAndProcedimentCodiNotib(
								filtre.getEntitatId() == null,
								filtre.getEntitatId(),
								//filtre.getComunicacioTipus() == null,
								//filtre.getComunicacioTipus(),
								false,
								NotificacioComunicacioTipusEnumDto.SINCRON,
								filtre.getEnviamentTipus() == null,
								filtre.getEnviamentTipus(),
								filtre.getConcepte() == null,
								filtre.getConcepte() == null ? "" : filtre.getConcepte(),
								filtre.getEstat() == null,
								filtre.getEstat(),
								dataInici == null && dataFi == null,
								dataInici,
								dataFi,
								filtre.getTitular() == null || filtre.getTitular().isEmpty(), 
								filtre.getTitular() == null ? "" : filtre.getTitular(),
								procedimentsCodisNotib,
								pageable);
					}
				} else if (isUsuariEntitat) {
					notificacions = notificacioRepository.findAmbFiltre(
							entitatId == null,
							entitatId,
							false,
							NotificacioComunicacioTipusEnumDto.SINCRON,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte(),
							filtre.getEstat() == null,
							filtre.getEstat(),
							dataInici == null && dataFi == null,
							dataInici,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(), 
							filtre.getTitular(),
							pageable);
				} else if (isAdministrador) {
					notificacions = notificacioRepository.findAmbFiltre(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							//filtre.getComunicacioTipus() == null,
							//filtre.getComunicacioTipus(),
							false,
							NotificacioComunicacioTipusEnumDto.SINCRON,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte(),
							filtre.getEstat() == null,
							filtre.getEstat(),
							dataInici == null && dataFi == null,
							dataInici,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(), 
							filtre.getTitular(),
							pageable);
				}
			}
		
		if (notificacions == null)
			resultatPagina = paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		 
		else
			resultatPagina = paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioDto.class);
		
		return resultatPagina;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId, 
			Long notificacioId) {
		logger.debug("Consulta dels events de la notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId, 
			Long notificacioId,
			Long enviamentId) {
		logger.debug("Consulta dels events associats a un destinatari (" +
				"notificacioId=" + notificacioId + ", " + 
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity destinatari = notificacioEnviamentRepository.findOne(enviamentId);
		entityComprovarHelper.comprovarPermisos(
				destinatari.getNotificacio().getId(),
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrEnviamentIdOrderByDataAsc(
						notificacioId,
						enviamentId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		NotificacioEntity entity = notificacioRepository.findOne(notificacioId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				entity.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				output);
		return new ArxiuDto(
				entity.getDocumentArxiuNom(),
				"PDF",
				output.toByteArray(),
				output.size());
	}
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
		NotificacioEnviamentEntity enviament =
				notificacioEnviamentRepository.findOne(enviamentId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				enviament.getNotificaCertificacioArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				output);
		return new ArxiuDto(
				calcularNomArxiuCertificacio(enviament),
				enviament.getNotificaCertificacioMime(),
				output.toByteArray(),
				output.size());
	}

	@Override
	public boolean enviar(
			Long entitatId, 
			Long notificacioId) {
		logger.debug("Intentant enviament de la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		return notificaHelper.notificacioEnviar(notificacioId);
	}

	@Override
	@Transactional
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId, 
			Long enviamentId) {
		logger.debug("Refrescant l'estat de la notificació de Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		notificaHelper.enviamentRefrescarEstat(enviament.getId());
		NotificacioEnviamenEstatDto estatDto = conversioTipusHelper.convertir(
				enviament,
				NotificacioEnviamenEstatDto.class);
		estatCalcularCampsAddicionals(
				enviament,
				estatDto);
		return estatDto;
	}
/*
	@Override
	@Transactional
	public boolean enviamentComunicacioSeu(
			Long enviamentId) {
		logger.debug("Enviant canvi d'estat de la seu electrònica a Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return notificaHelper.enviamentComunicacioSeu(
				enviament.getId(),
				new Date());
	}

	@Override
	@Transactional
	public boolean enviamentCertificacioSeu(
			Long enviamentId,
			ArxiuDto certificacioArxiu) {
		logger.debug("Enviant certificació de la seu electrònica a Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return notificaHelper.enviamentCertificacioSeu(
				enviament.getId(),
				certificacioArxiu,
				new Date());
	}
*/
	// 1. Enviament de notificacions pendents a Notific@
	////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
	public void notificaEnviamentsPendents() {
		if (isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			logger.debug("Cercant notificacions pendents d'enviar a Notifica");
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(
					pluginHelper.getNotificaReintentsMaxProperty(), 
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEntity pendent: pendents) {
					logger.debug(">>> Realitzant enviament a Notifica de la notificació amb identificador: " + pendent.getId());
					notificaHelper.notificacioEnviar(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.debug("L'enviament de notificacions a Notific@ està deshabilitada");
		}
	}

	// 2. Enviament de notificacions pendents a la seu electrònica
	//////////////////////////////////////////////////////////////
	/*@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.seu.enviaments.retard.inicial}")
	public void seuEnviamentsPendents() {
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && isSeuEnviamentsActiu() && pluginHelper.isSeuPluginDisponible()) {
			logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
			int maxPendents = getSeuEnviamentsProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatPendent(
					pluginHelper.getSeuReintentsMaxProperty(),
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.debug(">>> Realitzant enviament a la Seu de la notificació amb identificador: " + pendent.getId());
					seuHelper.enviament(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.debug("L'enviament de notificacions a la seu electrònica està deshabilitada");
		}
	}*/

	// 3. Actualització de l'estat dels enviaments amb l'estat de la seu electrònica
	////////////////////////////////////////////////////////////////////////////////
	/*@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.consulta.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.seu.consulta.retard.inicial}")
	public void seuConsultaEstatNotificacions() {
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && isSeuConsultaActiu() && pluginHelper.isSeuPluginDisponible()) {
			logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
			int maxPendents = getSeuConsultaProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatEnviat(new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant consulta d'estat a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.debug(">>> Consultant l'estat de la notificació a la Seu amb identificador: " + pendent.getId());
					boolean estatActualitzat = seuHelper.consultaEstat(pendent.getId());
					if (estatActualitzat) {
						logger.debug(">>>>>> La notificació amb identificador " + pendent.getId() + " ha canviat d'estat a la Seu. Enviant el canvi a Notific@");
						notificaHelper.enviamentSeu(pendent.getId());
					}
				}
			} else {
				logger.debug("No hi ha notificacions pendents de consultar estat a la seu electrònica");
			}
		} else {
			logger.debug("L'actualització de l'estat dels enviaments amb l'estat de la seu electrònica està deshabilitada");
		}
	}*/

	// 4. Actualització dels estats dels enviaments a Notifica@ amb l'estat de la seu electrònica
	/////////////////////////////////////////////////////////////////////////////////////////////
	/*@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviament.estat.seu.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviament.estat.seu.retard.inicial}")
	public void notificaInformaCanviEstatSeu() {
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && isNotificaCanviEstatSeuActiu() && pluginHelper.isSeuPluginDisponible()) {
			logger.debug("Cercant notificacions de la seu pendents d'actualitzar l'estat a Notifica");
			int maxPendents = getNotificaCanviEstatSeuProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatModificat(new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant actualització d'estat a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.debug(">>> Enviant el canvi d'estat de la Seu cap a Notific@ de la notificació amb identificador: " + pendent.getId());
					notificaHelper.enviamentSeu(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'actualització d'estat a Notifica");
			}
		} else {
			logger.debug("L'actualització de l'estat dels enviaments a Notifica@ amb l'estat de la seu electrònica està deshabilitat");
		}
	}*/

	// 5. Actualització de l'estat dels enviaments amb l'estat de Notific@
	//////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
	public void enviamentRefrescarEstatPendents() {
		if (isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
			logger.debug("Cercant enviaments pendents de refrescar l'estat de Notifica");
			int maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByNotificaRefresc(
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					logger.debug(">>> Consultat l'estat a Notific@ de la notificació amb identificador " + pendent.getId() + ", i actualitzant les dades a Notib.");
					notificaHelper.enviamentRefrescarEstat(pendent.getId());
				}
			} else {
				logger.debug("No hi ha enviaments pendents de refrescar l'estat de Notifica");
			}
		} else {
			logger.debug("L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
		}
	}

	private void estatCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamenEstatDto estatDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificaErrorEvent();
			if (event != null) {
				estatDto.setNotificaErrorData(event.getData());
				estatDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
			}
		}
		/*if (enviament.isSeuError()) {
			NotificacioEventEntity event = enviament.getSeuErrorEvent();
			if (event != null) {
				estatDto.setSeuErrorData(event.getData());
				estatDto.setSeuErrorDescripcio(event.getErrorDescripcio());
			}
		}*/
		estatDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}

	private boolean isNotificaEnviamentsActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.notifica.enviaments.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getNotificaEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.notifica.enviaments.processar.max",
				10);
	}

	private boolean isSeuEnviamentsActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.seu.enviaments.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getSeuEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.enviaments.processar.max",
				10);
	}

	private boolean isSeuConsultaActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.seu.consulta.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getSeuConsultaProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.consulta.processar.max",
				10);
	}

	private boolean isNotificaCanviEstatSeuActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.notifica.enviament.estat.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getNotificaCanviEstatSeuProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.notifica.enviament.estat.seu.processar.max",
				10);
	}

	private boolean isEnviamentActualitzacioEstatActiu() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max",
				10);
	}

	private boolean isTasquesActivesProperty() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasques.actives");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}

	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
	}
	
	
	private void RellenarInformacioAdicional(
			NotificacioDtoV2 notificacio,
			NotificacioEntity notificacioEntity,
			List<EnviamentReferencia> referencies,
			NotificacioEnviamentEntity.Builder enviamentBuilder, 
			PersonaDto titular) {
		
		// Definir entrega postal si hi ha
		boolean entregaPostalActiva = notificacio.isEntregaPostalActiva();
		if (entregaPostalActiva) {
			EntregaPostalDto entregaPostal = notificacio.getEntregaPostal();
			NotificaDomiciliTipusEnumDto tipus = null;
			NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
			if (entregaPostal.getTipus() != null) {
				switch (entregaPostal.getTipus()) {
				case APARTAT_CORREUS:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
					break;
				case ESTRANGER:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
					break;
				case NACIONAL:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
					break;
				case SENSE_NORMALITZAR:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
					break;
				}
				tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
			} else {
				throw new ValidationException("ENTREGA_POSTAL", "L'entrega postal te el camp tipus buit");
			}
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
			if (entregaPostal.getNumeroCasa() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
			} else if (entregaPostal.getApartatCorreus() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
			} else if (entregaPostal.getPuntKm() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
			} else {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
			}
			enviamentBuilder.domiciliTipus(tipus).domiciliConcretTipus(tipusConcret).
			domiciliViaTipus(toEnviamentViaTipusEnum(entregaPostal.getTipusVia())).
			domiciliViaNom(entregaPostal.getViaNom()).
			domiciliNumeracioTipus(numeracioTipus).
			domiciliNumeracioNumero(entregaPostal.getNumeroCasa()).
			domiciliNumeracioPuntKm(entregaPostal.getPuntKm()).
			domiciliApartatCorreus(entregaPostal.getApartatCorreus()).
			domiciliBloc(entregaPostal.getBloc()).
			domiciliPortal(entregaPostal.getPortal()).
			domiciliEscala(entregaPostal.getEscala()).
			domiciliPlanta(entregaPostal.getPlanta()).
			domiciliPorta(entregaPostal.getPorta()).
			domiciliComplement(entregaPostal.getComplement()).
			domiciliCodiPostal(entregaPostal.getCodiPostal()).
			domiciliPoblacio(entregaPostal.getPoblacio()).
			domiciliMunicipiCodiIne(entregaPostal.getMunicipiCodi()).
			domiciliProvinciaCodi(entregaPostal.getProvinciaCodi()).
			domiciliPaisCodiIso(entregaPostal.getPaisCodi()).
			domiciliLinea1(entregaPostal.getLinea1()).
			domiciliLinea2(entregaPostal.getLinea2());
		}
		EntregaDehDto entregaDeh = notificacio.getEntregaDeh();
		if (entregaDeh != null) {
			enviamentBuilder.dehObligat(entregaDeh.isObligat()).
			dehNif(titular.getNif().toUpperCase()).
			dehProcedimentCodi(entregaDeh.getProcedimentCodi());
		}
		
		NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
				enviamentBuilder.build());
		String referencia;
		try {
			referencia = notificaHelper.xifrarId(enviamentSaved.getId());
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(
					"No s'ha pogut crear la referencia per al destinatari",
					ex);
		}
		enviamentSaved.updateNotificaReferencia(referencia);
		EnviamentReferencia enviamentReferencia = new EnviamentReferencia();
		enviamentReferencia.setTitularNif(titular.getNif().toUpperCase());
		enviamentReferencia.setReferencia(referencia);
		referencies.add(enviamentReferencia);
		notificacioEntity.addEnviament(enviamentSaved);
	}
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);


	


}
