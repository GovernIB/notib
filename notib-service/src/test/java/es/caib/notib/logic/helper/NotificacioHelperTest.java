package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.logic.helper.NotificacioHelper.NotificacioData;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.logic.test.data.ConfigTest;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioHelperTest {
	
	@Mock
	private EntityComprovarHelper entityComprovarHelper;
	@Mock
	private OrganGestorHelper organGestorHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private ProcSerOrganRepository procedimentOrganRepository;
	@Mock
	private GrupRepository grupRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private OrganGestorRepository organGestorRepository;
	@Mock
	private GrupEntity grupNotificacio;
	@Mock
	private OrganGestorEntity organGestor;
	@Mock
	private ProcedimentEntity procediment;
	@Mock
	private ProcSerOrganEntity procedimentOrgan;
	@Mock
	private ConfigHelper configHelper;

	@InjectMocks
	private NotificacioHelper notificacioHelper;
	
	@Before
	public void setUp() {
		Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.procediment.alta.auto.retard"))).thenReturn(10);
		Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.procediment.alta.auto.caducitat"))).thenReturn(15);
	}
	
	@Test
	public void whenBuildNotificacioData_thenReturn() {
		
		// Given	
		var entitat = new EntitatEntity();

		var caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		var enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);

		var retard = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.retard");
		var c = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.caducitat");
		var procediment = ProcedimentEntity.builder().codi("").nom("").retard(retard).caducitat(c).entitat(entitat).build();

//		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
//				"",
//				"",
//				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.retard"),
//				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.caducitat"),
//				entitat,
//				false,
//				null, // organGestor
//				null,
//				null,
//				null,
//				null,
//				false,
//				false).build();
		
		ProcSerDto procedimentDto = new ProcSerDto();
		procedimentDto.setId(1L);
		
		GrupDto grupDto = new GrupDto();
		grupDto.setId(1L);
		
		DocumentDto document = new DocumentDto();
		document.setId(Long.toString(new Random().nextLong()));
		document.setContingutBase64("/es/caib/notib/logic/arxiu.pdf");
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		
		DocumentDto document2 = new DocumentDto();
		document2.setId(Long.toString(new Random().nextLong()));
		document2.setUuid(UUID.randomUUID().toString());
		document2.setNormalitzat(false);
		document2.setGenerarCsv(false);
		
		DocumentDto document3 = new DocumentDto();
		document3.setId(Long.toString(new Random().nextLong()));
		document3.setCsv("54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
		document3.setNormalitzat(false);
		document3.setGenerarCsv(true);
		
		NotificacioDatabaseDto notificacio = NotificacioDatabaseDto.builder()
                .emisorDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI)
                .enviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO)
                .enviamentDataProgramada(enviamentDataProgramada)
                .concepte("Test")
                .descripcio("Test descripció")
                .organGestorCodi("A00000000")
                .enviamentDataProgramada(new Date())
                .retard(5)
                .caducitat(caducitat)
                .enviaments(new ArrayList<NotEnviamentDatabaseDto>())
                .usuariCodi("admin")
                .numExpedient("EXPEDIENTEX")
                .idioma(Idioma.CA)
                .document(document)
                .document2(document2)
                .document3(document3)
                .procediment(procedimentDto)
                .grup(grupDto)
                .build();

		OrganGestorEntity organGestor = OrganGestorEntity.builder().entitat(entitat).build();
		ProcSerOrganEntity procedimentOrgan = ProcSerOrganEntity.getBuilder(procediment, organGestor).build();
		GrupEntity grupNotificacio = GrupEntity.getBuilder(null, null, entitat, organGestor).build();
		String documentGesdocId = "documentGesdocId";
		
		Document documentArxiuUuid = initDocument(document2.getUuid());
		Document documentArxiuCsv = initDocument(document3.getCsv());
		
		DocumentEntity documentEntity = initDocumentEntity(document, documentGesdocId);
		DocumentEntity documentEntity2 = initDocumentEntity(document2, documentGesdocId);
		DocumentEntity documentEntity3 = initDocumentEntity(document3, documentGesdocId);
		
		// Mocks
		Mockito.when(entityComprovarHelper.comprovarProcediment(Mockito.any(EntitatEntity.class), Mockito.anyLong())).thenReturn(procediment);
		Mockito.when(procedimentOrganRepository.findByProcSerIdAndOrganGestorId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(procedimentOrgan);
		Mockito.when(entityComprovarHelper.comprovarProcedimentOrgan(
				Mockito.any(EntitatEntity.class),
				Mockito.anyLong(),
				Mockito.nullable(ProcSerOrganEntity.class),
				Mockito.eq(false),
				Mockito.eq(false),
				Mockito.eq(true),
				Mockito.eq(false),
				Mockito.eq(false))).thenReturn(procediment);
//		Mockito.when(organGestorHelper.createOrganGestorFromNotificacio(Mockito.any(NotificacioDatabaseDto.class), Mockito.any(EntitatEntity.class))).thenReturn(organGestor);
		Mockito.when(grupRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(grupNotificacio));
		Mockito.when(documentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(documentEntity));
		Mockito.when(documentRepository.save(Mockito.any(DocumentEntity.class))).thenReturn(documentEntity2, documentEntity3);
		Mockito.when(organGestorRepository.findByCodi(Mockito.any(String.class))).thenReturn(organGestor);

		//base64
		Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.anyString(), Mockito.any(byte[].class))).thenReturn(documentGesdocId);
		
		//Uuid
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(true)))
			.thenReturn(documentArxiuUuid);
		
		//CSV
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(false)))
			.thenReturn(documentArxiuCsv);
		Mockito.when(pluginHelper.arxiuGetImprimible(Mockito.anyString(), Mockito.eq(false))).thenReturn(documentArxiuCsv.getContingut());
		
		Mockito.when(pluginHelper.isArxiuPluginDisponible()).thenReturn(Boolean.TRUE);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.estatElaboracioToValidesa(Mockito.any(DocumentEstatElaboracio.class))).thenReturn(ValidesaEnum.ORIGINAL.getValor());
				
		// When	
		NotificacioData notificacioData = notificacioHelper.buildNotificacioData(entitat, notificacio, Boolean.TRUE);
		
		// Then
		Mockito.verify(documentRepository, Mockito.times(1)).findById(Long.valueOf(document.getId())); //ArxiuBase64
		Mockito.verify(documentRepository, Mockito.times(2)).save(Mockito.any(DocumentEntity.class)); //Uuid y CSV
		assertNotNull(notificacioData);
		assertEquals(notificacio, notificacioData.getNotificacio());
		assertEquals(entitat, notificacioData.getEntitat());
		assertEquals(grupNotificacio, notificacioData.getGrupNotificacio());
		assertEquals(organGestor, notificacioData.getOrganGestor());
		assertEquals(procediment, notificacioData.getProcSer());
		assertEquals(documentEntity, notificacioData.getDocumentEntity());
		assertEquals(documentEntity2, notificacioData.getDocument2Entity());
		assertEquals(documentEntity3, notificacioData.getDocument3Entity());
		assertNull(notificacioData.getDocument4Entity());
		assertNull(notificacioData.getDocument5Entity());
	}
	
	private Document initDocument(String identificador) {
		Document documentArxiu = new Document();
		
		DocumentContingut contingut = new DocumentContingut();
		contingut.setArxiuNom("arxiu.pdf");
		contingut.setTipusMime("application/pdf");
		contingut.setContingut("/es/caib/notib/logic/arxiu.pdf".getBytes());
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
	
	
	private DocumentEntity initDocumentEntity(DocumentDto document, String documentGesdocId) {
		DocumentEntity documentEntity = DocumentEntity.getBuilderV2(
				document.getId(),
				documentGesdocId,
				document.getArxiuNom(),
				document.getUrl(),
				document.isNormalitzat(),
				document.getUuid(),
				document.getCsv(),
				document.getMediaType(),
				document.getMida(),
				document.getOrigen(),
				document.getValidesa(),
				document.getTipoDocumental(),
				document.getModoFirma()
			).build();
		return documentEntity;
	}
	
	@After
	public void tearDown() {
		Mockito.reset(entityComprovarHelper);
		Mockito.reset(organGestorHelper);
		Mockito.reset(pluginHelper);
		Mockito.reset(procedimentOrganRepository);
		Mockito.reset(grupRepository);
		Mockito.reset(documentRepository);
		Mockito.reset(grupNotificacio);
		Mockito.reset(organGestor);
		Mockito.reset(procediment);
		Mockito.reset(procedimentOrgan);
	}

}
