package es.caib.notib.core.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EntitatTipusDocEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.NotificacioHelper.NotificacioData;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentOrganRepository;
import es.caib.notib.core.test.data.ConfigTest;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioHelperTest {
	
	@Mock
	private EntityComprovarHelper entityComprovarHelper;
	@Mock
	private OrganGestorHelper organGestorHelper;
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private ProcedimentOrganRepository procedimentOrganRepository;
	@Mock
	private GrupRepository grupRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private GrupEntity grupNotificacio;
	@Mock
	private OrganGestorEntity organGestor;
	@Mock
	private ProcedimentEntity procediment;
	@Mock
	private ProcedimentOrganEntity procedimentOrgan;
	
	@InjectMocks
	private NotificacioHelper notificacioHelper;
	
	@Before
	public void setUp() {
		System.setProperty("es.caib.notib.procediment.alta.auto.retard", "10");
		System.setProperty("es.caib.notib.procediment.alta.auto.caducitat", "15");
	}
	
	@Test
	public void whenBuildNotificacioData_thenReturn() {
		
		// Given	
		EntitatEntity entidad = new EntitatEntity();

		Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		
		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
				"",
				"",
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "10")),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "15")),
				entidad,
				null,
				null,
				false,
				null, // organGestor
				null,
				null,
				null,
				null,
				false).build();
		
		ProcedimentDto procedimentDto = new ProcedimentDto();
		procedimentDto.setId(1L);
		
		GrupDto grupDto = new GrupDto();
		grupDto.setId(1L);
		
		DocumentDto document = new DocumentDto();
		document.setId(Long.toString(new Random().nextLong()));
		document.setContingutBase64("/es/caib/notib/core/arxiu.pdf");
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
                .enviaments(new ArrayList<NotificacioEnviamentDtoV2>())
                .usuariCodi("admin")
                .numExpedient("EXPEDIENTEX")
                .idioma(IdiomaEnumDto.CA)
                .document(document)
                .document2(document2)
                .document3(document3)
                .procediment(procedimentDto)
                .grup(grupDto)
                .build();

		OrganGestorEntity organGestor = OrganGestorEntity.getBuilder(null, null, entidad, null, null, null, null).build();
		ProcedimentOrganEntity procedimentOrgan = ProcedimentOrganEntity.getBuilder(procediment, organGestor).build();
		GrupEntity grupNotificacio = GrupEntity.getBuilder(null, null, entidad, organGestor).build();
		String documentGesdocId = "documentGesdocId";
		
		Document documentArxiuUuid = initDocument(document2.getUuid());
		Document documentArxiuCsv = initDocument(document3.getCsv());
		
		DocumentEntity documentEntity = initDocumentEntity(document, documentGesdocId);
		DocumentEntity documentEntity2 = initDocumentEntity(document2, documentGesdocId);
		DocumentEntity documentEntity3 = initDocumentEntity(document3, documentGesdocId);
		
		// Mocks
		Mockito.when(entityComprovarHelper.comprovarProcediment(Mockito.any(EntitatEntity.class), Mockito.anyLong())).thenReturn(procediment);
		Mockito.when(procedimentOrganRepository.findByProcedimentIdAndOrganGestorId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(procedimentOrgan);
		Mockito.when(entityComprovarHelper.comprovarProcedimentOrgan(
				Mockito.any(EntitatEntity.class),
				Mockito.anyLong(),
				Mockito.nullable(ProcedimentOrganEntity.class),
				Mockito.eq(false),
				Mockito.eq(false),
				Mockito.eq(true),
				Mockito.eq(false))).thenReturn(procediment);
		Mockito.when(organGestorHelper.createOrganGestorFromNotificacio(Mockito.any(NotificacioDatabaseDto.class), 
				Mockito.any(EntitatEntity.class))).thenReturn(organGestor);
		Mockito.when(grupRepository.findOne(Mockito.anyLong())).thenReturn(grupNotificacio);
		Mockito.when(documentRepository.findOne(Mockito.anyLong())).thenReturn(documentEntity);
		Mockito.when(documentRepository.save(Mockito.any(DocumentEntity.class))).thenReturn(documentEntity2, documentEntity3);
		
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
		NotificacioData notificacioData = notificacioHelper.buildNotificacioData(entidad, notificacio, Boolean.TRUE);
		
		// Then
		Mockito.verify(documentRepository, Mockito.times(1)).findOne(Long.valueOf(document.getId())); //ArxiuBase64
		Mockito.verify(documentRepository, Mockito.times(2)).save(Mockito.any(DocumentEntity.class)); //Uuid y CSV
		assertNotNull(notificacioData);
		assertEquals(notificacio, notificacioData.getNotificacio());
		assertEquals(entidad, notificacioData.getEntitat());
		assertEquals(grupNotificacio, notificacioData.getGrupNotificacio());
		assertEquals(organGestor, notificacioData.getOrganGestor());
		assertEquals(procediment, notificacioData.getProcediment());
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
