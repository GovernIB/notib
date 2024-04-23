package es.caib.notib.logic.service;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.DocumentHelper;
import es.caib.notib.logic.helper.EmailNotificacioMassivaHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.helper.NotificacioMassivaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.RegistreNotificaHelper;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaInfoDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.mapper.NotificacioTableMapper;
import es.caib.notib.logic.service.ws.NotificacioValidator;
import es.caib.notib.logic.test.NotificacioMassivaTests;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.filtres.FiltreNotificacio;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

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
	private NotificacioMassivaHelper notificacioMassivaHelper;
	@Mock
	private ProcSerRepository procSerRepository;
	@Mock
	private OrganGestorRepository organGestorRepository;
	@Mock
	private NotificacioMassivaRepository notificacioMassivaRepository;
	@Mock
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Mock
	private NotificacioListHelper notificacioListHelper;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private DocumentHelper documentHelper;
	@Mock
	private NotificacioValidator notificacioValidator;
	@Mock
	private AuditHelper auditHelper;
	@Mock
	private EnviamentSmService enviamentSmService;
	@Mock
	private PaginacioHelper paginacioHelper;
	@Mock
	private CacheHelper cacheHelper;
	@Mock
	private NotificacioTableMapper notificacioTableMapper;
	@Mock
	private EmailNotificacioMassivaHelper emailNotificacioMassivaHelper;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private JmsTemplate jmsTemplate;

	@InjectMocks
	NotificacioMassivaServiceImpl notificacioMassivaService;

	Long entitatId = 2L;
	EntitatEntity entitatMock;
	ProcSerEntity procSerMock;
	OrganGestorEntity organMock;
	NotificacioEntity notificacioMock;

	Long notMassivaId = 2L;
	NotificacioMassivaEntity notificacioMassivaMock;

	String entitatCodiDir3 = "A000000";

	private static String csvNom = "test1.csv";
	private static String zipNom = "test1.zip";
	private static String email = "test@limit.com";
	private static String codiUsuari = "CODI_USER";

	private static DocumentValidDto document;

	@BeforeClass
	public static void setUpAll() throws IOException {
		document = new DocumentValidDto();
		byte[] contingut = IOUtils.toByteArray(NotificacioMassivaServiceTest.class.getResourceAsStream("/es/caib/notib/logic/arxiu.pdf"));
		document.setArxiuNom("arxiu.pdf");
		document.setMediaType("application/pdf");
		document.setArxiuGestdocId("012345");
		document.setMida(Long.valueOf(contingut.length));
		document.setNormalitzat(false);
		document.setOrigen(OrigenEnum.ADMINISTRACIO);
		document.setValidesa(ValidesaEnum.ORIGINAL);
		document.setTipoDocumental(TipusDocumentalEnum.ALTRES);
		document.setModoFirma(false);
	}

	@Before
	public void setUp() {

		entitatMock = Mockito.mock(EntitatEntity.class);
		procSerMock = Mockito.mock(ProcSerEntity.class);
		organMock = Mockito.mock(OrganGestorEntity.class);
		notificacioMock = Mockito.mock(NotificacioEntity.class);
		Mockito.when(entitatMock.getDir3Codi()).thenReturn(entitatCodiDir3);
		Mockito.when(entitatMock.isActiva()).thenReturn(true);
		Mockito.when(procSerMock.isActiu()).thenReturn(true);
		Mockito.when(metricsHelper.iniciMetrica()).thenReturn(null);
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.eq(entitatId))).thenReturn(entitatMock);
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.eq(entitatId), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(entitatMock);
//		Mockito.when(registreNotificaHelper.isSendDocumentsActive()).thenReturn(false);
		Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.anyString(), Mockito.any(byte[].class))).thenReturn("rnd_gesid");
		Mockito.when(notificacioHelper.saveNotificacio(Mockito.any(EntitatEntity.class), Mockito.any(Notificacio.class), Mockito.anyBoolean(), Mockito.any(NotificacioMassivaEntity.class), Mockito.<Map<String, Long>>any()))
				.thenReturn(NotificacioEntity.builder().enviaments(new HashSet<>()).build());
		Mockito.when(procSerRepository.findByCodiAndEntitat(Mockito.anyString(), Mockito.<EntitatEntity>any())).thenReturn(procSerMock);
		Mockito.when(organGestorRepository.findByCodi(Mockito.anyString())).thenReturn(organMock);
		Mockito.when(messageHelper.getMessage(Mockito.anyString())).thenReturn("Missatge mock");
		Mockito.when(configHelper.getConfigAsLong(eq("es.caib.notib.massives.maxim.files"), eq(999L))).thenReturn(999L);
		Mockito.when(cacheHelper.findUsuariAmbCodi(anyString())).thenReturn(DadesUsuari.builder().codi(codiUsuari).build());
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
		Mockito.when(organMock.getCodi()).thenReturn("A04003746");
		Mockito.when(organMock.getEstat()).thenReturn(OrganGestorEstatEnum.V);
//		Mockito.when(notificacioValidator.validarNotificacioMassiu(
//			Mockito.any(NotificacioDatabaseDto.class), Mockito.any(EntitatEntity.class), Mockito.<Map<String, Long>>any()))
//			.thenReturn(new ArrayList<String>());

		NotificacioMassivaTests.TestMassiusFiles test = NotificacioMassivaTests.getTestInteressatSenseNif();
		NotificacioMassivaDto not = NotificacioMassivaDto.builder().build();
		NotificacioMassivaDto notificacioMassiu = NotificacioMassivaDto.builder().ficheroCsvNom(csvNom).ficheroZipNom(zipNom).ficheroCsvBytes(test.getCsvContent())
				.ficheroZipBytes(test.getZipContent()).caducitat(new Date()).email(email).build();

		// When
		notificacioMassivaService.create(entitatId, codiUsuari, notificacioMassiu);

		// Then
		Mockito.verify(notificacioHelper, Mockito.times(1)).
				altaEnviamentsWeb(Mockito.any(EntitatEntity.class), Mockito.any(NotificacioEntity.class), Mockito.<List<Enviament>>any());
	}

	@Test
	public void whenCreate_GivenNoErrors_ThenCallAltaNotificacioWeb() throws Exception {
		// Given
		Mockito.when(organMock.getCodi()).thenReturn("E04975701");
		Mockito.when(organMock.getEstat()).thenReturn(OrganGestorEstatEnum.V);
//		Mockito.when(notificacioValidatorHelper.validarNotificacioMassiu(
//				Mockito.any(NotificacioDatabaseDto.class), Mockito.any(EntitatEntity.class), Mockito.<Map<String, Long>>any()))
//				.thenReturn(new ArrayList<String>());
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
		Mockito.verify(notificacioHelper, Mockito.times(4))
				.altaEnviamentsWeb(Mockito.any(EntitatEntity.class), Mockito.any(NotificacioEntity.class), Mockito.<List<Enviament>>any()
		);
	}

	@Test
	public void whenCreate_GivenSomeErrors_ThenNoCallAltaNotificacioWeb() throws Exception {
		// Given
//		Mockito.when(notificacioValidatorHelper.validarNotificacioMassiu(
//				Mockito.any(NotificacioV2.class), Mockito.any(EntitatEntity.class), Mockito.<Map<String, Long>>any()))
//				.thenReturn(Arrays.asList("Error 1", "Error 2"));
//		Mockito.doCallRealMethod().when(notificacioValidator).validate();
//		Mockito.doCallRealMethod().when(notificacioValidator).setNotificacio(Mockito.any(Notificacio.class));
//		Mockito.doCallRealMethod().when(notificacioValidator).setErrors(Mockito.any(BindException.class));
//		Mockito.when(notificacioValidator.error(Mockito.anyInt(), Mockito.nullable(Locale.class), Mockito.nullable(List.class))).thenReturn("Mossatge mock");
		String usuariCodi = "CODI_USER";
		NotificacioMassivaTests.TestMassiusFiles test1Data = NotificacioMassivaTests.getTest1Files();
		var notificacioMassiu = NotificacioMassivaDto.builder()
				.ficheroCsvNom("test1.csv")
				.ficheroZipNom("test1.zip")
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
				Mockito.<List<Enviament>>any());
	}

	@Test
	public void whenFindById_ThenCallFindOne() throws Exception {

		// When
		notificacioMassivaService.findById(entitatId, notMassivaId);
		// Then
		Mockito.verify(notificacioMassivaRepository, Mockito.times(1)).findById(Mockito.eq(notMassivaId));
	}

	@Test
	public void whenGetNotificacioMassivaInfo_ThenCallFindOneAndGestioDocumentalGet() throws Exception {
		// Given
		Mockito.when(conversioTipusHelper.convertir(
				Mockito.any(NotificacioMassivaEntity.class), Mockito.any(Class.class)))
				.thenReturn(new NotificacioMassivaInfoDto()); // ho ignorarem per a la prova
		Mockito.doAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			OutputStream out = ((OutputStream)args[2]);
//			byte[] contingut = Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("/es/caib/notib/logic/massiu/resum.csv")).getFile()));
			byte[] contingut = Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("/es/caib/notib/logic/massiu/resum.csv")).toURI()));
			ByteArrayInputStream in = new ByteArrayInputStream(contingut);
			IOUtils.copy(in, out);
			return null;
		}).when(pluginHelper).gestioDocumentalGet(Mockito.anyString(), Mockito.anyString(), Mockito.any(OutputStream.class));

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
		var authToken = new UsernamePasswordAuthenticationToken("user", "user", List.of(new SimpleGrantedAuthority("tothom")));
		SecurityContextHolder.getContext().setAuthentication(authToken);
		Mockito.when(notificacioListHelper.getMappeigPropietats(Mockito.any(PaginacioParamsDto.class))).thenReturn(null); // ho ignorarem per a la prova
//		var filtre = Mockito.any(NotificacioFiltreDto.class);
//		var rol=  Mockito.any(RolEnumDto.class);
//		var usuariCodi = Mockito.anyString();
//		var rols = Mockito.any(List.class);
		Mockito.when(notificacioListHelper.getFiltre(Mockito.any(NotificacioFiltreDto.class), Mockito.anyLong(), Mockito.nullable(RolEnumDto.class), Mockito.nullable(String.class), Mockito.nullable(List.class)))
				.thenReturn( FiltreNotificacio.builder()
						.entitatId(entitatId)
						.entitat(EntitatEntity.hiddenBuilder().dir3Codi("D3").build())
//						.comunicacioTipus(null)
						.enviamentTipus(null)
						.estatMask(null)
						.concepte(null)
						.dataInici(null)
						.dataFi(null)
						.titular(null)
						.organCodi(null)
						.procedimentCodi(null)
						.tipusUsuari(null)
						.numExpedient(null)
						.creadaPer(null)
						.identificador(null)
						.nomesAmbErrors(false)
						.nomesSenseErrors(false)
						.build()); // ho ignorarem per a la prova
//		Page<NotificacioTableEntity> page = Page.empty();
		Mockito.when(notificacioTableViewRepository.findAmbFiltreByNotificacioMassiva(Mockito.any(FiltreNotificacio.class), Mockito.nullable(Pageable.class))).thenReturn(Page.empty());
//			Mockito.when(notificacioListHelper.complementaNotificacions(Mockito.eq(entitatMock), Mockito.anyString(), Mockito.<Page<NotificacioTableEntity>>any())).thenReturn(null);

		// When
		notificacioMassivaService.findNotificacions(entitatId, notMassivaId, new NotificacioFiltreDto(), new PaginacioParamsDto());

		// Then
		Mockito.verify(notificacioTableViewRepository, Mockito.times(1)).findAmbFiltreByNotificacioMassiva(Mockito.any(FiltreNotificacio.class),
//				Mockito.anyBoolean(),
//				Mockito.eq(entitatId),
//				Mockito.eq(notificacioMassivaMock),
//				Mockito.anyBoolean(),
//				Mockito.nullable(EnviamentTipus.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(Integer.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(Date.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(Date.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(TipusUsuariEnumDto.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.nullable(String.class),
//				Mockito.anyBoolean(),
//				Mockito.anyBoolean(),
				Mockito.nullable(Pageable.class));

//		Mockito.verify(notificacioListHelper, Mockito.times(1)).complementaNotificacions(Mockito.eq(entitatMock), Mockito.anyString(), Mockito.<Page<NotificacioTableEntity>>any());
	}

	@Test
	public void whenFindAmbFiltrePaginat_GivenAdminRole_ThenCallFindEntitatAdminRolePage() throws Exception {
		// When
		notificacioMassivaService.findAmbFiltrePaginat(entitatId, new NotificacioMassivaFiltreDto(), RolEnumDto.NOT_ADMIN, new PaginacioParamsDto());

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
				Mockito.nullable(Pageable.class));
	}

	@Test
	public void whenFindAmbFiltrePaginat_GivenUserRole_ThenCallFindUserRolePage() throws Exception {

		var authToken = new UsernamePasswordAuthenticationToken("user", "user", List.of(new SimpleGrantedAuthority("tothom")));
		SecurityContextHolder.getContext().setAuthentication(authToken);
		// When
		notificacioMassivaService.findAmbFiltrePaginat(entitatId, new NotificacioMassivaFiltreDto(), RolEnumDto.tothom, new PaginacioParamsDto());

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
				Mockito.nullable(Pageable.class));
	}

	@Test
	public void whenPosposar_ThenCallPosposarNotificacions() throws Exception {

		// When
		notificacioMassivaService.posposar(entitatId, notMassivaId);
		// Then
		Mockito.verify(notificacioMassivaHelper, Mockito.times(1)).posposarNotificacions(Mockito.eq(notMassivaId));
	}

	@Test
	public void whenReactivar_ThenCallReactivarNotificacions() throws Exception {

		// When
		notificacioMassivaService.reactivar(entitatId, notMassivaId);
		// Then
		Mockito.verify(notificacioMassivaHelper, Mockito.times(1)).reactivarNotificacions(Mockito.eq(notMassivaId));
	}

	@Test
	public void whenGetCSVFile_ThenCallGestioDocumentalGet() throws Exception {

		// When
		var fitxer = notificacioMassivaService.getCSVFile(entitatId, notMassivaId);
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
		var fitxer = notificacioMassivaService.getZipFile(entitatId, notMassivaId);
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
		var fitxer = notificacioMassivaService.getResumFile(entitatId, notMassivaId);
		// Then
		Mockito.verify(pluginHelper, Mockito.times(1)).gestioDocumentalGet(
				Mockito.eq(notificacioMassivaMock.getResumGesdocId()),
				Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES),
				Mockito.<OutputStream>any());
	}
	@Test
	public void whenGetErrorsFile_ThenCallGestioDocumentalGet() throws Exception {

		// When
		var fitxer = notificacioMassivaService.getErrorsValidacioFile(entitatId, notMassivaId);
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
