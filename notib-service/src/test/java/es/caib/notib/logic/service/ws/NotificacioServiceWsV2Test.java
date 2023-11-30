package es.caib.notib.logic.service.ws;

import com.codahale.metrics.Timer;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.client.domini.EnviamentReferencia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.DocumentHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.RegistreNotificaHelper;
import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.logic.utils.MimeUtils;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PersonaRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.statemachine.StateMachine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static es.caib.notib.logic.intf.util.ValidacioErrorCodes.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

//@RunWith(JUnitParamsRunner.class)
@ExtendWith({MockitoExtension.class})
public class NotificacioServiceWsV2Test {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	protected static final String ENTITAT_DIR3CODI = "A04003003";
	protected static final String ORGAN_CODI = "A04035965";
	protected static final String ORGAN_POSTAL_CODI = "A04035966";
	protected static final String LLIBRE = "L16";
	protected static final String OFICINA = "O00009390";
	protected static final String IDENTIFICADOR_PROCEDIMENT = "874510";
	protected static final String IDENTIFICADOR_PROCEDIMENT_POSTAL = "8745100";
	protected static final String IDIOMA = "ca";
	protected static final String USUARI_CODI = "e18225486x";
	protected static final String APP_CODI = "mockApp";
//	protected static final NotificaDomiciliConcretTipus TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipus.NACIONAL;
	protected static final NotificaDomiciliConcretTipus TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipus.SENSE_NORMALITZAR;

	// Autowired del servei
	@Mock
	private EntitatRepository entitatRepository;
	@Mock
	private NotificacioRepository notificacioRepository;
	@Mock
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Mock
	private ProcSerRepository procSerRepository;
	@Mock
	private ProcSerOrganRepository procedimentOrganRepository;
	@Mock
	private PersonaRepository personaRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private OrganGestorRepository organGestorRepository;
	@Mock
	private UsuariRepository usuariRepository;
	@Mock
	private PermisosHelper permisosHelper;
	@Mock
	private NotificaHelper notificaHelper;
	@Mock
	private EnviamentTableHelper enviamentTableHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private RegistreNotificaHelper registreNotificaHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private CacheHelper cacheHelper;
	@Mock
	private MetricsHelper metricsHelper;
	@Mock
	private NotificacioTableHelper notificacioTableHelper;
	@Mock
	private NotificacioHelper notificacioHelper;
	@Mock
	private JustificantService justificantService;
	@Mock
	private ConfigHelper configHelper;
	@Mock
	private AuditHelper auditHelper;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private  DocumentHelper documentHelper;

	// Autowired de NotificacioValidator
	@Mock
	private AplicacioRepository aplicacioRepository;
	@Mock
	private GrupService grupService;
	@Mock
	private OrganGestorCachable organGestorCachable;

	// Mocks entitats
	@Mock
	private LlibreDto llibreOrganMock;
	@Mock
	private Map<String, NodeDir3> arbreUnitatsMock;
	@Mock
	private List<OficinaDto> oficinesSIRMock;
	//	@Mock
	//	private EntitatEntity entitatMock;
	@Mock
	private Authentication auth;
	@Mock
	private NotificacioEnviamentEntity enviamentSavedMock;
	@Mock
	private NotificacioEventEntity notificacioEventEntityMock;
	@Mock
	private EnviamentSmService enviamentSmService;

//	@Spy
//	private NotificacioValidator notificacioValidator; // = new NotificacioValidator(aplicacioRepository, grupService, messageHelper, cacheHelper, organGestorCachable, configHelper);


	private EntitatEntity entitatMock;
	private OrganGestorEntity organGestorMock;
	private OrganGestorEntity organGestorPostalMock;
	private ProcedimentEntity procedimentMock;
	private ProcedimentEntity procedimentPostalMock;
	private ProcedimentEntity procedimentComuMock;
	private ServeiEntity serveiMock;
	private ProcSerOrganEntity procedimentOrganMock;
	private AplicacioEntity aplicacioMock;
	private DadesUsuari dadesUsuariMock;

	private Document document;

	private es.caib.plugins.arxiu.api.Document documentArxiuMock;
	private DocumentEntity documentEntityMock;

	@InjectMocks
	NotificacioServiceWsV2 notificacioService = new NotificacioServiceWsImplV2();

 	@BeforeEach
	public void setUp() throws IOException {

		((NotificacioServiceWsImplV2)notificacioService).setNotificacioValidator(new NotificacioValidator(aplicacioRepository, grupService, messageHelper, cacheHelper, organGestorCachable, configHelper));
		entitatMock = EntitatEntity.hiddenBuilder().codi("GOIB").nom("Govern de les Illes Balears").tipus(EntitatTipusEnumDto.GOVERN).dir3Codi(ENTITAT_DIR3CODI).activa(true).apiKey("xxxxxx").ambEntregaDeh(false).llibreEntitat(false).oficinaEntitat(false).build();
		aplicacioMock = AplicacioEntity.builder().entitat(entitatMock).activa(true).usuariCodi(APP_CODI).callbackUrl("http://callback.url").build();
		organGestorMock = OrganGestorEntity.builder().codi(ORGAN_CODI).nom("Direcció General de Política Lingüística").entitat(entitatMock).build();
		organGestorPostalMock = OrganGestorEntity.builder().codi(ORGAN_POSTAL_CODI).nom("Direcció General de Política Lingüística").entregaCie(new EntregaCieEntity()).entitat(entitatMock).build();
		procedimentMock = ProcedimentEntity.builder().codi(IDENTIFICADOR_PROCEDIMENT).nom("Convocatòria d'ajuts destinats a la premsa de caràcter local").entitat(entitatMock).retard(0).caducitat(10).agrupar(false).organGestor(organGestorMock).comu(false).requireDirectPermission(false).build();
		procedimentPostalMock = ProcedimentEntity.builder().codi(IDENTIFICADOR_PROCEDIMENT_POSTAL).nom("Convocatòria d'ajuts destinats a la premsa de caràcter local").entitat(entitatMock).retard(0).caducitat(10).agrupar(false).organGestor(organGestorPostalMock).comu(false).requireDirectPermission(false).build();
		procedimentComuMock = ProcedimentEntity.builder().codi(IDENTIFICADOR_PROCEDIMENT).nom("Convocatòria d'ajuts destinats a la premsa de caràcter local").entitat(entitatMock).retard(0).caducitat(10).agrupar(false).comu(true).requireDirectPermission(false).build();
		((ProcSerEntity)procedimentMock).setTipus(ProcSerTipusEnum.PROCEDIMENT);
		((ProcSerEntity)procedimentComuMock).setTipus(ProcSerTipusEnum.PROCEDIMENT);
		serveiMock = ServeiEntity.builder()
				.codi(IDENTIFICADOR_PROCEDIMENT)
				.nom("SERVEI - Convocatòria d'ajuts destinats a la premsa de caràcter local")
				.entitat(entitatMock)
				.retard(0)
				.caducitat(10)
				.agrupar(false)
				.organGestor(organGestorMock)
				.comu(false)
				.requireDirectPermission(false)
				.build();
		((ProcSerEntity)serveiMock).setTipus(ProcSerTipusEnum.SERVEI);
		procedimentOrganMock = ProcSerOrganEntity.getBuilder(procedimentMock, organGestorMock).build();
		document = Document.builder().arxiuNom("document.pdf").uuid("00000000-0000-0000-0000-000000000000").normalitzat(false).build();;
		documentArxiuMock = initDocument(document.getUuid());
		documentEntityMock = DocumentEntity.getBuilderV2(
				document.getUuid(),
				document.getArxiuNom(),
				document.isNormalitzat(),
				document.getUuid(),
				document.getCsv(),
				documentArxiuMock.getContingut().getTipusMime(),
				documentArxiuMock.getContingut().getTamany(),
				document.getOrigen(),
				document.getValidesa(),
				document.getTipoDocumental(),
				document.getModoFirma()).build();
		dadesUsuariMock = DadesUsuari.builder().codi("e18225486x").nom("Usuari").llinatges("Llinatge1 Llinatge2").nif("12345678Z").email("usuari@limit.es").build();

		lenient().when(auth.getName()).thenReturn(APP_CODI);
		lenient().doNothing().when(metricsHelper).fiMetrica(nullable(Timer.Context.class));
		lenient().when(enviamentSmService.altaEnviament(anyString())).thenReturn(null);
		lenient().when(metricsHelper.iniciMetrica()).thenReturn(null);
		lenient().doNothing().when(integracioHelper).addAplicacioAccioParam(nullable(IntegracioInfo.class), nullable(Long.class));
		lenient().doNothing().when(integracioHelper).addAccioError(nullable(IntegracioInfo.class), nullable(String.class));
		lenient().when(entitatRepository.findByDir3Codi(anyString())).thenReturn(entitatMock);
		lenient().when(aplicacioRepository.findByEntitatIdAndUsuariCodi(nullable(Long.class), anyString())).thenReturn(aplicacioMock);
		lenient().when(procSerRepository.findByCodiAndEntitat(eq(IDENTIFICADOR_PROCEDIMENT), any(EntitatEntity.class))).thenReturn(procedimentMock);
		lenient().when(procSerRepository.findByCodiAndEntitat(eq(IDENTIFICADOR_PROCEDIMENT_POSTAL), any(EntitatEntity.class))).thenReturn(procedimentPostalMock);
//		lenient().when(procSerRepository.findByCodiAndEntitat(anyString(), any(EntitatEntity.class))).thenReturn(procedimentMock);
		lenient().when(procSerRepository.findByCodiAndEntitat(eq("COMU"), any(EntitatEntity.class))).thenReturn(procedimentComuMock);
		lenient().when(organGestorRepository.findByCodi(eq(ORGAN_CODI))).thenReturn(organGestorMock);
		lenient().when(organGestorRepository.findByCodi(eq(ORGAN_POSTAL_CODI))).thenReturn(organGestorPostalMock);
		lenient().when(procedimentOrganRepository.findByProcSerIdAndOrganGestorId(anyLong(), anyLong())).thenReturn(procedimentOrganMock);
		lenient().when(documentRepository.saveAndFlush(any(DocumentEntity.class))).thenReturn(documentEntityMock);
		lenient().when(pluginHelper.arxiuGetImprimible(anyString(), anyBoolean())).thenReturn(documentArxiuMock.getContingut());
		lenient().when(pluginHelper.arxiuDocumentConsultar(anyString(), nullable(String.class), anyBoolean(), anyBoolean())).thenReturn(documentArxiuMock);
		lenient().when(notificacioRepository.saveAndFlush(any(NotificacioEntity.class))).thenAnswer(n -> n.getArgument(0));
		lenient().when(notificacioEnviamentRepository.saveAndFlush(any(NotificacioEnviamentEntity.class))).thenReturn(NotificacioEnviamentEntity.builder().notificaReferencia(UUID.randomUUID().toString()).build());
		lenient().when(personaRepository.save(any(PersonaEntity.class))).thenAnswer(p -> p.getArgument(0));
		lenient().when(messageHelper.getMessage(anyString())).thenAnswer(code -> "[" + code.getArgument(0).toString().substring(16) + "] Error");
		lenient().when(messageHelper.getMessage(anyString(), any(Locale.class))).thenAnswer(code -> "[" + code.getArgument(0).toString().substring(16) + "] Error");
		lenient().when(messageHelper.getMessage(anyString(), nullable(Object[].class), any(Locale.class))).thenAnswer(code -> "[" + code.getArgument(0).toString().substring(16) + "] Error");
		lenient().when(cacheHelper.findUsuariAmbCodi(Mockito.anyString())).thenReturn(dadesUsuariMock);
		lenient().when(cacheHelper.unitatPerCodi(eq(ORGAN_CODI))).thenReturn(OrganGestorDto.builder().codi(ORGAN_CODI).build());
		lenient().when(configHelper.getConfigAsLong(eq("es.caib.notib.notificacio.document.size"))).thenReturn(10485760L);
		lenient().when(configHelper.getConfigAsLong(eq("es.caib.notib.notificacio.document.total.size"))).thenReturn(15728640L);
		lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.comunicacions.sir.internes"))).thenReturn(false);
		lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.destinatari.multiple"))).thenReturn(false);
		lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.document.metadades.por.defecto"))).thenReturn(true);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// Test de validacions de alta de notificació
	@ParameterizedTest(name = "[{index}] Validació error {1}")
	@ArgumentsSource(NotificacioProvider.class)
	public void whenAltaUuid_thenReturnErrorOrRespostaAltaOK(Notificacio notificacio, int errorEsperat) throws IOException {
		
		// Given
		switch (errorEsperat) {
			case EMISOR_DIR3_NULL:
				when(entitatRepository.findByDir3Codi(Mockito.isNull())).thenReturn(null);
				break;
			case EMISOR_DIR3_NO_EXIST:
				when(entitatRepository.findByDir3Codi(eq("NO_EXIST"))).thenReturn(null);
				break;
			case ENTITAT_INACTIVA:
				when(entitatRepository.findByDir3Codi(anyString())).thenReturn(EntitatEntity.hiddenBuilder().codi("GOIB").nom("Govern de les Illes Balears").activa(false).ambEntregaDeh(false).llibreEntitat(false).oficinaEntitat(false).build());
				break;
			case APLICACIO_NO_EXIST:
				when(aplicacioRepository.findByEntitatIdAndUsuariCodi(nullable(Long.class), anyString())).thenReturn(null);
				break;
			case PROCSER_NO_EXIST:
				when(procSerRepository.findByCodiAndEntitat(eq("NO_EXIST"), any(EntitatEntity.class))).thenReturn(null);
				break;
			case PROCSER_INACTIU:
				procedimentMock.setActiu(false);
				break;
			case SERVEI_EN_NOTIFICACIO:
				when(procSerRepository.findByCodiAndEntitat(eq("SERVEI"), any(EntitatEntity.class))).thenReturn(serveiMock);
				break;
			case ORGAN_ALTRE_ENTITAT:
				when(organGestorRepository.findByCodi(eq("NO_EXIST"))).thenReturn(null);
				break;
			case ORGAN_DIFF_AL_DEL_PROCEDIMENT:
				lenient().when(organGestorRepository.findByCodi(eq("ORGAN_DIFERENT"))).thenReturn(OrganGestorEntity.builder().codi("XXXXXXXX").nom("Qualsevol altre organ").entitat(entitatMock).build());
				break;
			case USUARI_INEXISTENT:
				when(cacheHelper.findUsuariAmbCodi(eq("NO_EXIST"))).thenReturn(null);
				break;
			case DOCUMENT_FORMAT_INVALID:
				var doc = DocumentValidDto.builder().mediaType(MimeUtils.getMimeTypeFromBase64(notificacio.getDocument().getContingutBase64(), "document.pdf")).build();
				lenient().when(documentHelper.getDocument(any(Document.class))).thenReturn(doc);
				break;
			case DOCUMENT_FORMAT_SIR_INVALID:
				var docSir = DocumentValidDto.builder().mediaType(MimeUtils.getMimeTypeFromBase64(notificacio.getDocument().getContingutBase64(), "document.pdf")).build();
				lenient().when(documentHelper.getDocument(any(Document.class))).thenReturn(docSir);
				organGestorMock.setOficina("Oficina");
				break;
			case DOCUMENT_ERROR_OBTENINT:
				var doc3 = DocumentValidDto.builder().mediaType(MimeUtils.getMimeTypeFromBase64(notificacio.getDocument().getContingutBase64(), "document.pdf")).build();
				lenient().when(documentHelper.getDocument(any(Document.class))).thenReturn(doc3);
				lenient().when(pluginHelper.arxiuGetImprimible(eq("00000000-0000-0000-0000-00000000000E"), eq(true))).thenThrow(new RuntimeException("Error obtenint fitxer"));
				break;
			case DOCUMENT_ERROR_OBTENINT_METADADES:
				lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.document.metadades.por.defecto"))).thenReturn(false);
				lenient().when(pluginHelper.arxiuDocumentConsultar(eq("00000000-0000-0000-0000-0000000000ME"), nullable(String.class), eq(true), eq(true))).thenThrow(new RuntimeException("Error obtenint metadades"));
				lenient().when(pluginHelper.arxiuDocumentConsultar(eq("00000000-0000-0000-0000-0000000000MN"), nullable(String.class), eq(true), eq(true))).thenReturn(null);
				break;
			case DOCUMENT_ERROR_VALIDANT_FIRMA:
				lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.plugins.validatesignature.enable.rest"))).thenReturn(true);
				lenient().when(pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(any(byte[].class), eq("document_error_firma.pdf"), anyString())).thenReturn(SignatureInfoDto.builder().error(true).errorMsg("Error validant firma").build());
				break;
			case DOCUMENT_METADADES_ORIGEN_NULL:
			case DOCUMENT_METADADES_VALIDESA_NULL:
			case DOCUMENT_METADADES_TIPUS_DOCUMENTAL_NULL:
			case DOCUMENT_METADADES_MODE_FIRMA_NULL:
				lenient().when(configHelper.getConfigAsBoolean(eq("es.caib.notib.document.metadades.por.defecto"))).thenReturn(false);
				break;
			case DOCUMENT_MASSA_GRAN:
				DocumentContingut contingut = new DocumentContingut();
				contingut.setTamany(15728640L);
				contingut.setTipusMime("application/pdf");
				lenient().when(pluginHelper.arxiuGetImprimible(eq("00000000-0000-0000-0000-00000000000G"), eq(true))).thenReturn(contingut);
				break;
			case DOCUMENTS_SIR_MASSA_GRANS:
				organGestorMock.setOficina("Oficina");
				DocumentContingut contingutMig = new DocumentContingut();
				contingutMig.setTamany(8388608L);
				contingutMig.setTipusMime("application/pdf");
				lenient().when(pluginHelper.arxiuGetImprimible(eq("00000000-0000-0000-0000-00000000000M"), eq(true))).thenReturn(contingutMig);
				break;
			case GRUP_INEXISTENT:
				procedimentMock.setAgrupar(true);
				break;
			case GRUP_EN_PROCEDIMENT_NO_AGRUPADA:
				lenient().when(grupService.findByCodi(eq("GRUP"), nullable(Long.class))).thenReturn(GrupDto.builder().codi("GRUP").build());
				procedimentMock.setAgrupar(true);
				break;
			case GRUP_NO_ASSIGNAT:
				lenient().when(grupService.findByCodi(eq("GRUP"), nullable(Long.class))).thenReturn(GrupDto.builder().codi("GRUP").build());
				lenient().when(grupService.findByProcedimentAndUsuariGrups(nullable(Long.class))).thenReturn(List.of(GrupDto.builder().codi("ALTRE_GRUP").build()));
				procedimentMock.setAgrupar(true);
				break;
			case PERSONA_DIR3CODI_PROPIA_ENTITAT:
				Map<String, OrganismeDto> organigrama = new HashMap();
				organigrama.put(ORGAN_CODI, OrganismeDto.builder().codi(ORGAN_CODI).build());
				lenient().when(organGestorCachable.findOrganigramaByEntitat(eq(ENTITAT_DIR3CODI))).thenReturn(organigrama);
				break;
			case DEH_NULL:
			case DEH_NIF_NULL:
				EntitatEntity entitatDehMock = EntitatEntity.hiddenBuilder().codi("GOIB").nom("Govern de les Illes Balears").tipus(EntitatTipusEnumDto.GOVERN).dir3Codi(ENTITAT_DIR3CODI).activa(true).apiKey("xxxxxx").ambEntregaDeh(true).llibreEntitat(false).oficinaEntitat(false).build();
				lenient().when(entitatRepository.findByDir3Codi(anyString())).thenReturn(entitatDehMock);
				break;
			case POSTAL_ENTREGA_INACTIVA:
				organGestorPostalMock.setEntregaCie(null);
				break;
		}

		// When
		RespostaAlta respostaAlta = notificacioService.alta(notificacio);

		// Then
		assertNotNull(respostaAlta);

		if (errorEsperat == 0) {
			assertFalse("Resposta amb error (" + respostaAlta.getErrorDescripcio() + ") quan s'esperava OK", respostaAlta.isError());
			assertNull(
					"Resposta OK amb descripció d'error (" + respostaAlta.getErrorDescripcio() + ")",
					respostaAlta.getErrorDescripcio());
			List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
			assertEquals(
					"No s'han rebut el mateix nombre de referències (" + referencies.size() + ") que d'enviaments (" + notificacio.getEnviaments().size() + ")",
					notificacio.getEnviaments().size(), referencies.size());
			assertEquals("La notificació no es troba en estat PENDENT", NotificacioEstatEnum.PENDENT, respostaAlta.getEstat());
			return;
		}

		assertTrue("Resposta OK quan s'esperava amb error", respostaAlta.isError());
		assertNotNull("Error sense descripció", respostaAlta.getErrorDescripcio());
		assertTrue(
				"L'error retornat no conté el codi d'error [" + errorEsperat + "]",
				respostaAlta.getErrorDescripcio().contains("[" + errorEsperat + "]"));

		System.out.println(respostaAlta.getErrorDescripcio());

	}


	private es.caib.plugins.arxiu.api.Document initDocument(String identificador) throws IOException {

		var documentArxiu = new es.caib.plugins.arxiu.api.Document();
		DocumentContingut contingut = new DocumentContingut();
		contingut.setArxiuNom("arxiu.pdf");
		contingut.setTipusMime("application/pdf");
		contingut.setContingut(IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/logic/arxiu.pdf")));
		contingut.setTamany(contingut.getContingut().length);
		documentArxiu.setContingut(contingut);
		documentArxiu.setEstat(DocumentEstat.DEFINITIU);
		documentArxiu.setFirmes(null);
		documentArxiu.setIdentificador(identificador);
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
		metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		metadades.setTipusDocumental(DocumentTipus.INFORME);
		documentArxiu.setMetadades(metadades);
		documentArxiu.setNom("Nombre Document Arxiu");
		documentArxiu.setVersio("Version");
		return documentArxiu;
	}
	
	@After
	public void tearDown() {
		Mockito.reset(pluginHelper);
	}
	
}
