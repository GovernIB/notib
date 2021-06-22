package es.caib.notib.core.service.ws;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.ws.notificacio.*;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioServiceWsV2Test {

	protected static final String ENTITAT_DIR3CODI = "A04003003";
	protected static final String ORGAN_CODI = "A04003003";
	protected static final String LLIBRE = "L16";
	protected static final String OFICINA = "O00009390";
	protected static final String IDENTIFICADOR_PROCEDIMENT = "2095292";
	protected static final String IDIOMA = "ca";
	protected static final String USUARI_CODI = "e18225486x";
//	protected static final NotificaDomiciliConcretTipusEnumDto TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
	protected static final NotificaDomiciliConcretTipusEnumDto TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;

	
	@Mock
	private EntitatRepository entitatRepository;
	@Mock
	private NotificacioRepository notificacioRepository;
	@Mock
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Mock
	private ProcedimentRepository procedimentRepository;
	@Mock
	private ProcedimentOrganRepository procedimentOrganRepository;
	@Mock
	private PersonaRepository personaRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private AplicacioRepository aplicacioRepository;
	@Mock
	private OrganGestorRepository organGestorRepository;
	@Mock
	private ConversioTipusHelper conversioTipusHelper;
	@Mock 
	private PermisosHelper permisosHelper;
	@Mock 
	private NotificacioEventRepository notificacioEventRepository;
	@Mock
	private NotificaHelper notificaHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private GrupService grupService;
	@Mock
	private RegistreNotificaHelper registreNotificaHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private CacheHelper cacheHelper;
	@Mock
	private MetricsHelper metricsHelper;
	@Mock
	private AuditNotificacioHelper auditNotificacioHelper;
	@Mock
	private AuditEnviamentHelper auditEnviamentHelper;
	@Mock
	private OrganGestorCachable organGestorCachable;
	@Mock
	private NotificacioHelper notificacioHelper;
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

	private AplicacioEntity aplicacio;
	
	private NotificacioEventEntity notificacioEventEntity = null;
	
	@InjectMocks
	NotificacioServiceWsV2 notificacioService = new NotificacioServiceWsImplV2();
	
	@Before
	public void setUp() {
		System.setProperty("es.caib.notib.procediment.alta.auto.retard", "10");
		System.setProperty("es.caib.notib.procediment.alta.auto.caducitat", "15");
		System.setProperty("es.caib.notib.document.metadades.por.defecto", "true");
		Mockito.when(auth.getName()).thenReturn("mockedName");
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	//Test consultaDocumentIMetadades para Uuid docu existent y metadades existents
	@Test
	public void whenAltaUuid_thenReturnRespostaAltaOK() throws IOException {
		
		// Given	
//		entitatMock.setCreatedDate(new DateTime());
//		Mockito.when(llibreOrganMock.getCodi()).thenReturn("AB");
//		Mockito.when(llibreOrganMock.getNomLlarg()).thenReturn("CD");
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		EntitatEntity entitatMock = EntitatEntity.getBuilder("codi", 
				"nom", 
				null, 
				"dir3Codi",
				"dir3CodiReg", 
				"apiKey", 
				false, 
				false, 
				null, 
				null, 
				"colorFons", 
				"colorLletra", 
				null, 
				"oficina", 
				"nomOficinaVirtual", 
				false, 
				"llibre", 
				"llibreNom", 
				false)
				.build();
		
		Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
				"",
				"",
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "10")),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "15")),
				entitatMock,
				null,
				null,
				false,
				null, // organGestor
				null,
				null,
				null,
				null,
				false).build();
		
		List<GrupDto> grups = new ArrayList<GrupDto>();
		GrupDto grupDto = new GrupDto();
		grupDto.setId(1L);
		grups.add(grupDto);
		
		OrganGestorEntity organGestor = OrganGestorEntity.builder(null, null,
				entitatMock, null, null, null, null, null).build();
		
//		Map<String, OrganismeDto> organigramaEntitat = null;
		
		ProcedimentOrganEntity procedimentOrgan = ProcedimentOrganEntity.getBuilder(procediment, organGestor).build();
		
//		DocumentV2 document = new DocumentV2();
//		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
//		try {
//			String arxiuB64 = Base64.encodeBase64String(IOUtils.toByteArray(getClass().getResourceAsStream(
//					"/es/caib/notib/core/notificacio_adjunt.pdf")));		
//			document.setContingutBase64(arxiuB64);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		document.setNormalitzat(false);
//		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");

		DocumentV2 document2 = new DocumentV2();
		document2.setUuid(UUID.randomUUID().toString());
		document2.setNormalitzat(false);
		document2.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		
//		DocumentV2 document3 = new DocumentV2();
//		document3.setCsv("54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
//		document3.setNormalitzat(false);
//		document3.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		
//		Document documentArxiuCsv = initDocument(document3.getCsv());
		Document documentArxiuUuid = initDocument(document2.getUuid());
		
		NotificacioV2 notificacio = generarNotificacioV2(notificacioId, 1, false, null, document2, null);
		
		aplicacio = AplicacioEntity.builder()
                .usuariCodi("")
                .callbackUrl("")
                .activa(true)
                .entitat(entitatMock)
                .build();

		PersonaEntity personaEntity = PersonaEntity.builder()
				.email("sandreu@limit.es")
				.llinatge1("Andreu")
				.llinatge2("Nadal")
				.nif("00000000T")
				.nom("Siòn")
				.telefon("666010101").build();
		OrganGestorDto organ = new OrganGestorDto();
		organ.setSir(true);
		
		NotificacioEntity notificacioGuardada = NotificacioEntity.getBuilderV2(entitatMock, 
				notificacioId, organGestor, null, null, notificacioId, notificacioId, caducitat, 
				null, caducitat, notificacioId, notificacioId, procediment, notificacioId, notificacioId, 
				null, procedimentOrgan, null).build();
		
		List<NotificacioEnviamentEntity> listaNotificacioGuardada = new ArrayList<NotificacioEnviamentEntity>();
		
		// When	
		Mockito.when(entitatRepository.findByDir3Codi(Mockito.anyString())).thenReturn(entitatMock);
		Mockito.when(aplicacioRepository.findByEntitatIdAndUsuariCodi(Mockito.nullable(Long.class), Mockito.anyString())).thenReturn(aplicacio);
		Mockito.when(procedimentRepository.findByCodiAndEntitat(Mockito.anyString(), Mockito.any(EntitatEntity.class))).thenReturn(procediment);
//		Mockito.when(grupService.findByProcedimentAndUsuariGrups(Mockito.anyLong())).thenReturn(grups);
//		Mockito.when(grupService.findByCodi(Mockito.anyString(), Mockito.anyLong())).thenReturn(null);//GrupDto grupNotificacio
		Mockito.when(organGestorRepository.findByCodi(notificacio.getOrganGestor())).thenReturn(organGestor);
//		Mockito.when(organGestorCachable.findOrganigramaByEntitat(Mockito.anyString())).thenReturn(organigramaEntitat);
		Mockito.when(pluginHelper.llistarLlibreOrganisme(Mockito.anyString(), Mockito.anyString())).thenReturn(llibreOrganMock);
//		Mockito.when(cacheHelper.findOrganigramaNodeByEntitat(Mockito.anyString())).thenReturn(arbreUnitatsMock);
//		Mockito.when(cacheHelper.getOficinesSIRUnitat(Mockito.anyMapOf(String.class, NodeDir3.class), Mockito.anyString())).thenReturn(oficinesSIRMock);
//		Mockito.when(organGestorRepository.save(Mockito.any(OrganGestorEntity.class))).thenReturn(null);
//		Mockito.when(procedimentOrganRepository.findByProcedimentIdAndOrganGestorId(procediment.getId(), organGestor.getId())).thenReturn(procedimentOrgan);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.arxiuGetImprimible(Mockito.anyString(), Mockito.eq(true))).thenReturn(documentArxiuUuid.getContingut());
//		Mockito.when(pluginHelper.arxiuGetImprimible(Mockito.anyString(), Mockito.eq(false))).thenReturn(documentArxiuCsv.getContingut());
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(true)))
			.thenReturn(documentArxiuUuid);
//		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(false)))
//		.thenReturn(documentArxiuCsv);
		Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.anyString(), Mockito.any(byte[].class))).thenReturn(Long.toString(new Random().nextLong()));
		Mockito.when(auditEnviamentHelper.desaEnviamentAmbReferencia(Mockito.any(EntitatEntity.class), 
				Mockito.nullable(NotificacioEntity.class), Mockito.any(Enviament.class), 
				Mockito.any(ServeiTipusEnumDto.class), Mockito.nullable(NotificaDomiciliNumeracioTipusEnumDto.class), 
				Mockito.nullable(NotificaDomiciliConcretTipusEnumDto.class), Mockito.any(PersonaEntity.class), 
				Mockito.anyListOf(PersonaEntity.class), Mockito.nullable(EntregaPostalViaTipusEnum.class))).thenReturn(enviamentSavedMock);
		Mockito.when(personaRepository.save(Mockito.any(PersonaEntity.class))).thenReturn(personaEntity);
		Mockito.when(auditNotificacioHelper.desaNotificacio(Mockito.any(NotificacioEntity.class))).thenReturn(notificacioGuardada);
		Mockito.when(notificacioRepository.saveAndFlush(Mockito.any(NotificacioEntity.class))).thenReturn(notificacioGuardada);
		Mockito.when(pluginHelper.getNotibTipusComunicacioDefecte()).thenReturn(NotificacioComunicacioTipusEnumDto.ASINCRON);
		Mockito.when(notificacioEnviamentRepository.findByNotificacio(Mockito.any(NotificacioEntity.class))).thenReturn(listaNotificacioGuardada);
		Mockito.when(notificacioHelper.getNotificaErrorEvent(Mockito.any(NotificacioEntity.class))).thenReturn(notificacioEventEntity);
		Mockito.doNothing().when(integracioHelper).addAccioOk(Mockito.any(IntegracioInfo.class));
//		Mockito.when(organGestorCachable.findOrganigramaByEntitat(Mockito.anyString())).thenReturn(new HashMap<String, OrganismeDto>());
		Mockito.when(cacheHelper.unitatPerCodi(Mockito.anyString())).thenReturn(organ);


		//		try {
//			Mockito.when(registreNotificaHelper.realitzarProcesRegistrar(notificacioGuardada)).thenReturn(false);
//		} catch (RegistreNotificaException e) {
//			e.printStackTrace();
//		}
//		try {
//			Mockito.when(notificaHelper.xifrarId(Mockito.anyLong())).thenReturn(Long.toString(new Random().nextLong()));
//		} catch (GeneralSecurityException e1) {
//			e1.printStackTrace();
//		}
		
		// Then
		RespostaAlta respostaAlta = notificacioService.alta(notificacio);
		
		if (respostaAlta.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
			
		} else {
			System.out.println(">>> Reposta Ok");
		}
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertEquals(
				NotificacioEstatEnum.PENDENT, //ASINCRON
				respostaAlta.getEstat());

	}

	private Document initDocument(String identificador) {
		Document documentArxiu = new Document();
		
		DocumentContingut contingut = new DocumentContingut();
		contingut.setArxiuNom("arxiu.pdf");
		contingut.setTipusMime("application/pdf");
		contingut.setContingut("/es/caib/notib/core/arxiu.pdf".getBytes());
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
	
	private NotificacioV2 generarNotificacioV2(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal,
			DocumentV2 document, DocumentV2 document2, DocumentV2 document3) {

		NotificacioV2 notificacio = new NotificacioV2();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
		notificacio.setUsuariCodi(USUARI_CODI);
//		notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
		notificacio.setOrganGestor(ORGAN_CODI);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));
		
		notificacio.setDocument(document2);
//		notificacio.setDocument(document);
//		notificacio.setDocument2(document2);
//		notificacio.setDocument3(document3);
		
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("Siòn");
			titular.setLlinatge1("Andreu");
			titular.setLlinatge2("Nadal");
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("sandreu@limit.es");
			titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				titular.setDir3Codi(ENTITAT_DIR3CODI);
			enviament.setTitular(titular);
			Persona destinatari = new Persona();
			destinatari.setNom("melcior");
			destinatari.setLlinatge1("Andreu");
			destinatari.setLlinatge2("Nadal");
			destinatari.setNif("18225486x");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("sandreu@limit.es");
			destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				destinatari.setDir3Codi(ENTITAT_DIR3CODI);
			enviament.setDestinataris(new ArrayList<Persona>());
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				if (NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR.equals(TIPUS_ENTREGA_POSTAL)) {
					entregaPostal.setTipus(TIPUS_ENTREGA_POSTAL);
					entregaPostal.setLinea1("linea1_" + i);
					entregaPostal.setLinea2("linea2_" + i);
				} else {
					entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
					entregaPostal.setViaNom("Bas");
					entregaPostal.setNumeroCasa("25");
					entregaPostal.setNumeroQualificador("bis");
					//entregaPostal.setApartatCorreus("0228");
					entregaPostal.setPortal("pt" + i);
					entregaPostal.setEscala("es" + i);
					entregaPostal.setPlanta("pl" + i);
					entregaPostal.setPorta("pr" + i);
					entregaPostal.setBloc("bl" + i);
					entregaPostal.setComplement("complement" + i);
					entregaPostal.setCodiPostal("07500");
					entregaPostal.setPoblacio("poblacio" + i);
					entregaPostal.setMunicipiCodi("070337");
					entregaPostal.setProvincia("07");
					entregaPostal.setPaisCodi("ES");
				}
				entregaPostal.setCie(new Integer(0));
				enviament.setEntregaPostal(entregaPostal);
				enviament.setEntregaPostalActiva(true);
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
			notificacio.setEnviaments(new ArrayList<Enviament>());
			notificacio.getEnviaments().add(enviament);
		}
		return notificacio;
	}

}
