package es.caib.notib.logic.service;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.*;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.helper.FiltreHelper.FiltreField;
import es.caib.notib.logic.helper.FiltreHelper.StringField;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.logic.test.NotificacioMassivaTests;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.OutputStream;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioMassivaServiceTest {
	@Mock
	private EntityComprovarHelper entityComprovarHelper;
	@Mock
	private ConversioTipusHelper conversioTipusHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private RegistreNotificaHelper registreNotificaHelper;
	@Mock
	private MetricsHelper metricsHelper;
	@Mock
	private NotificacioHelper notificacioHelper;
	@Mock
	private PaginacioHelper paginacioHelper;
	@Mock
	private NotificacioValidatorHelper notificacioValidatorHelper;
	@Mock
	private NotificacioMassivaHelper notificacioMassivaHelper;

	@Mock
	private ProcSerRepository procSerRepository;

	@Mock
	private NotificacioMassivaRepository notificacioMassivaRepository;
	@Mock
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Mock
	private NotificacioListHelper notificacioListHelper;
	@Mock
	private MessageHelper messageHelper;
//	@Mock
//	private EmailNotificacioMassivaHelper emailNotificacioMassivaHelper;
//	@Mock
//	private Authentication auth;

	@InjectMocks
	NotificacioMassivaService notificacioMassivaService = new NotificacioMassivaServiceImpl();

	Long entitatId = 2L;
	EntitatEntity entitatMock;
	ProcSerEntity procSerMock;

	Long notMassivaId = 2L;
	NotificacioMassivaEntity notificacioMassivaMock;

	String entitatCodiDir3 = "A000000";

	private static String csvNom = "csv_test.csv";
	private static String zipNom = "zip_test.zip";
	private static String email = "test@limit.com";
	private static String codiUsuari = "CODI_USER";

	@Before
	public void setUp() {

		entitatMock = Mockito.mock(EntitatEntity.class);
		procSerMock = Mockito.mock(ProcSerEntity.class);
		Mockito.when(entitatMock.getDir3Codi()).thenReturn(entitatCodiDir3);
		Mockito.when(metricsHelper.iniciMetrica()).thenReturn(null);
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.eq(entitatId))).thenReturn(entitatMock);
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.eq(entitatId), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(entitatMock);
		Mockito.when(registreNotificaHelper.isSendDocumentsActive()).thenReturn(false);
		Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.anyString(), Mockito.any(byte[].class))).thenReturn("rnd_gesid");
		Mockito.when(notificacioHelper.saveNotificacio(Mockito.any(EntitatEntity.class), Mockito.any(NotificacioDatabaseDto.class), Mockito.anyBoolean(), Mockito.any(NotificacioMassivaEntity.class), Mockito.<Map<String, Long>>any()))
				.thenReturn(NotificacioEntity.builder().build());
		Mockito.when(procSerRepository.findByCodiAndEntitat(Mockito.anyString(), Mockito.<EntitatEntity>any())).thenReturn(procSerMock);
		setUpNotificacioMassiva();
//		setUpAuthentication();
	}

//	private void setUpAuthentication() {
//		Authentication authentication = Mockito.mock(Authentication.class);
//		ºº
//
//		// Mockito.whens() for your authorization object
//		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//		SecurityContextHolder.setContext(securityContext);
//	}

	private void setUpNotificacioMassiva() {
		notificacioMassivaMock =  Mockito.mock(NotificacioMassivaEntity.class);
//		Mockito.when(notificacioMassivaMock.getEntitat()).thenReturn(entitatMock);
//		Mockito.when(notificacioMassivaMock.getId()).thenReturn(notMassivaId);
		Mockito.when(notificacioMassivaMock.getCsvFilename()).thenReturn("massius.csv");
		Mockito.when(notificacioMassivaMock.getZipFilename()).thenReturn("massius.zip");
		Mockito.when(notificacioMassivaMock.getCsvGesdocId()).thenReturn("CSV_GESDOC_ID");
		Mockito.when(notificacioMassivaMock.getZipGesdocId()).thenReturn("ZIP_GESDOC_ID");
		Mockito.when(notificacioMassivaMock.getErrorsGesdocId()).thenReturn("ERRORS_GESDOC_ID");
		Mockito.when(notificacioMassivaMock.getResumGesdocId()).thenReturn("RESUM_GESDOC_ID");

		Mockito.when(notificacioMassivaRepository.findById(
				Mockito.eq(notMassivaId)))
				.thenReturn(Optional.of(notificacioMassivaMock));
	}

	@Test
	public void whenCreate_interessat_sense_nif_ok() throws Exception {

		// Given
		Mockito.when(notificacioValidatorHelper.validarNotificacioMassiu(Mockito.any(NotificacioDatabaseDto.class), Mockito.any(EntitatEntity.class), Mockito.<Map<String, Long>>any()))
				.thenReturn(new ArrayList<String>());

		NotificacioMassivaTests.TestMassiusFiles test = NotificacioMassivaTests.getTestInteressatSenseNif();
		NotificacioMassivaDto not = NotificacioMassivaDto.builder().build();
		NotificacioMassivaDto notificacioMassiu = NotificacioMassivaDto.builder().ficheroCsvNom(csvNom).ficheroZipNom(zipNom).ficheroCsvBytes(test.getCsvContent())
													.ficheroZipBytes(test.getZipContent()).caducitat(new Date()).email(email).build();

		// When
		notificacioMassivaService.create(entitatId, codiUsuari, notificacioMassiu);

		// Then
		Mockito.verify(notificacioHelper, Mockito.times(1)).
				altaEnviamentsWeb(Mockito.any(EntitatEntity.class), Mockito.any(NotificacioEntity.class), Mockito.<List<NotEnviamentDatabaseDto>>any());
	}

	@Test
	public void whenCreate_GivenNoErrors_ThenCallAltaNotificacioWeb() throws Exception {

		// Given
		Mockito.when(notificacioValidatorHelper.validarNotificacioMassiu(
				Mockito.any(NotificacioDatabaseDto.class),
				Mockito.any(EntitatEntity.class),
				Mockito.<Map<String, Long>>any()
		)).thenReturn(new ArrayList<String>());
		String usuariCodi = "CODI_USER";
		NotificacioMassivaTests.TestMassiusFiles test1Data = NotificacioMassivaTests.getTest1Files();
		NotificacioMassivaDto notificacioMassiu = NotificacioMassivaDto.builder()
				.ficheroCsvNom("csv_test.csv")
				.ficheroZipNom("zip_test.zip")
				.ficheroCsvBytes(test1Data.getCsvContent())
				.ficheroZipBytes(test1Data.getZipContent())
				.caducitat(new Date())
				.email("test@email.com")
				.build();

		// When
		notificacioMassivaService.create(entitatId, usuariCodi, notificacioMassiu);

		// Then
		Mockito.verify(notificacioHelper, Mockito.times(4)).altaEnviamentsWeb(
				Mockito.any(EntitatEntity.class),
				Mockito.any(NotificacioEntity.class),
				Mockito.<List<NotEnviamentDatabaseDto>>any()
		);
	}

	@Test
	public void whenCreate_GivenSomeErrors_ThenNoCallAltaNotificacioWeb() throws Exception {
		// Given
		Mockito.when(notificacioValidatorHelper.validarNotificacioMassiu(
				Mockito.any(NotificacioDatabaseDto.class),
				Mockito.any(EntitatEntity.class),
				Mockito.<Map<String, Long>>any()
		)).thenReturn(Arrays.asList("Error 1", "Error 2"));
		String usuariCodi = "CODI_USER";
		NotificacioMassivaTests.TestMassiusFiles test1Data = NotificacioMassivaTests.getTest1Files();
		NotificacioMassivaDto notificacioMassiu = NotificacioMassivaDto.builder()
				.ficheroCsvNom("csv_test.csv")
				.ficheroZipNom("zip_test.zip")
				.ficheroCsvBytes(test1Data.getCsvContent())
				.ficheroZipBytes(test1Data.getZipContent())
				.caducitat(new Date())
				.email("test@email.com")
				.build();

		// When
		notificacioMassivaService.create(entitatId, usuariCodi, notificacioMassiu);

		// Then
		Mockito.verify(notificacioHelper, Mockito.times(0)).altaEnviamentsWeb(
				Mockito.any(EntitatEntity.class),
				Mockito.any(NotificacioEntity.class),
				Mockito.<List<NotEnviamentDatabaseDto>>any()
		);
	}

	@Test
	public void whenFindById_ThenCallFindOne() throws Exception {
		// When
		notificacioMassivaService.findById(entitatId, notMassivaId);

		// Then
		Mockito.verify(notificacioMassivaRepository, Mockito.times(1)).findById(
				Mockito.eq(notMassivaId));
	}

	@Test
	public void whenGetNotificacioMassivaInfo_ThenCallFindOneAndGestioDocumentalGet() throws Exception {
		// Given
		Mockito.when(conversioTipusHelper.convertir(
				Mockito.any(NotificacioMassivaEntity.class), Mockito.any(Class.class)))
				.thenReturn(new NotificacioMassivaInfoDto()); // ho ignorarem per a la prova

		// When
		notificacioMassivaService.getNotificacioMassivaInfo(entitatId, notMassivaId);

		// Then
		Mockito.verify(notificacioMassivaRepository, Mockito.times(1)).findById(
				Mockito.eq(notMassivaId));

		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getResumGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
				Mockito.<OutputStream>any());
	}

	@Test
	public void whenFindNotificacions_ThenCallFindAmbFiltreByNotificacioMassiva() throws Exception {
		// Given
		Mockito.when(notificacioListHelper.getMappeigPropietats(
				Mockito.any(PaginacioParamsDto.class)))
				.thenReturn(null); // ho ignorarem per a la prova
		Mockito.when(notificacioListHelper.getFiltre(
				Mockito.any(NotificacioFiltreDto.class)))
				.thenReturn( NotificacioListHelper.NotificacioFiltre.builder()
						.entitatId(new FiltreField<Long>(entitatId))
						.comunicacioTipus(new FiltreField<NotificacioComunicacioTipusEnumDto>(null))
						.enviamentTipus(new FiltreField<NotificaEnviamentTipusEnumDto>(null))
						.estat(new FiltreField<NotificacioEstatEnumDto>(null))
						.concepte(new StringField(null))
						.dataInici(new FiltreField<Date>(null))
						.dataFi(new FiltreField<Date>(null))
						.titular(new StringField(null))
						.organGestor(new FiltreField<OrganGestorEntity>(null))
						.procediment(new FiltreField<ProcSerEntity>(null))
						.tipusUsuari(new FiltreField<TipusUsuariEnumDto>(null))
						.numExpedient(new StringField(null))
						.creadaPer(new StringField(null))
						.identificador(new StringField(null))
						.nomesAmbErrors(new FiltreField<Boolean>(false))
						.nomesSenseErrors(new FiltreField<Boolean>(false))
						.hasZeronotificaEnviamentIntent(new FiltreField<Boolean>(false))
						.build()); // ho ignorarem per a la prova
			Mockito.when(notificacioListHelper.complementaNotificacions(
					Mockito.eq(entitatMock),
					Mockito.anyString(),
					Mockito.<Page<NotificacioTableEntity>>any())).thenReturn(null);

		// When
		notificacioMassivaService.findNotificacions(entitatId, notMassivaId,
				new NotificacioFiltreDto(), new PaginacioParamsDto());

		// Then
		Mockito.verify(notificacioTableViewRepository, Mockito.times(1)).findAmbFiltreByNotificacioMassiva(
				Mockito.anyBoolean(),
				Mockito.eq(entitatId),
				Mockito.eq(notificacioMassivaMock),
				Mockito.anyBoolean(),
				Mockito.nullable(NotificaEnviamentTipusEnumDto.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Integer.class),
//				Mockito.nullable(NotificacioEstatEnumDto.class),
//				Mockito.nullable(EnviamentEstat.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(TipusUsuariEnumDto.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.anyBoolean(),
				Mockito.anyBoolean(),
				Mockito.nullable(Boolean.class),
				Mockito.nullable(Pageable.class));

		Mockito.verify(notificacioListHelper, Mockito.times(1)).complementaNotificacions(
				Mockito.eq(entitatMock),
				Mockito.anyString(),
				Mockito.<Page<NotificacioTableEntity>>any());
	}

	@Test
	public void whenFindAmbFiltrePaginat_GivenAdminRole_ThenCallFindEntitatAdminRolePage() throws Exception {
		// When
		notificacioMassivaService.findAmbFiltrePaginat(entitatId, new NotificacioMassivaFiltreDto(), RolEnumDto.NOT_ADMIN,
				new PaginacioParamsDto());

		// Then
		Mockito.verify(notificacioMassivaRepository, Mockito.times(1)).findEntitatAdminRolePage(
				Mockito.eq(entitatMock),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(NotificacioMassivaEstatDto.class),
				Mockito.anyBoolean(),
				Mockito.nullable(String.class),
				Mockito.nullable(Pageable.class)
		);
	}

	@Test
	public void whenFindAmbFiltrePaginat_GivenUserRole_ThenCallFindUserRolePage() throws Exception {
		// When
		notificacioMassivaService.findAmbFiltrePaginat(entitatId, new NotificacioMassivaFiltreDto(), RolEnumDto.tothom,
				new PaginacioParamsDto());

		// Then
		Mockito.verify(notificacioMassivaRepository, Mockito.times(1)).findUserRolePage(
				Mockito.eq(entitatMock),
				Mockito.nullable(String.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(Date.class),
				Mockito.anyBoolean(),
				Mockito.nullable(NotificacioMassivaEstatDto.class),
				Mockito.nullable(Pageable.class)
		);
	}

	@Test
	public void whenPosposar_ThenCallPosposarNotificacions() throws Exception {
		// When
		notificacioMassivaService.posposar(entitatId, notMassivaId);

		// Then
		Mockito.verify(notificacioMassivaHelper, Mockito.times(1)).posposarNotificacions(
				Mockito.eq(notMassivaId)
		);
	}

	@Test
	public void whenReactivar_ThenCallReactivarNotificacions() throws Exception {
		// When
		notificacioMassivaService.reactivar(entitatId, notMassivaId);

		// Then
		Mockito.verify(notificacioMassivaHelper, Mockito.times(1)).reactivarNotificacions(
				Mockito.eq(notMassivaId)
		);
	}

	@Test
	public void whenGetCSVFile_ThenCallGestioDocumentalGet() throws Exception {
		// When
		FitxerDto fitxer = notificacioMassivaService.getCSVFile(entitatId, notMassivaId);

		// Then
		Assert.assertEquals(fitxer.getNom(), notificacioMassivaMock.getCsvFilename());
		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getCsvGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV),
				Mockito.<OutputStream>any());
	}

	@Test
	public void whenGetZipFile_ThenCallGestioDocumentalGet() throws Exception {
		// When
		FitxerDto fitxer = notificacioMassivaService.getZipFile(entitatId, notMassivaId);

		// Then
		Assert.assertEquals(fitxer.getNom(), notificacioMassivaMock.getZipFilename());
		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getZipGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP),
				Mockito.<OutputStream>any());
	}
	@Test
	public void whenGetResumFile_ThenCallGestioDocumentalGet() throws Exception {
		// When
		FitxerDto fitxer = notificacioMassivaService.getResumFile(entitatId, notMassivaId);

		// Then
		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getResumGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
				Mockito.<OutputStream>any());
	}
	@Test
	public void whenGetErrorsFile_ThenCallGestioDocumentalGet() throws Exception {
		// When
		FitxerDto fitxer = notificacioMassivaService.getErrorsValidacioFile(entitatId, notMassivaId);

		// Then
		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getErrorsGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS),
				Mockito.<OutputStream>any());
	}

	@After
	public void tearDown() {
		Mockito.reset(pluginHelper);
	}
}