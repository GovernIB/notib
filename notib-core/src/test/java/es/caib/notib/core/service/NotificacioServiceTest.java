package es.caib.notib.core.service;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.PluginHelper;
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

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioServiceTest {

	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private ConfigHelper configHelper;
	
	@InjectMocks
	NotificacioService notificacioService = new NotificacioServiceImpl();
	
	@Before
	public void setUp() {
		Mockito.when(configHelper.getAsIntByEntitat(Mockito.eq("es.caib.notib.document.consulta.id.csv.mida.min"))).thenReturn(16);

	}

	//Test consultaDocumentIMetadades para Uuid docu existent y metadades existents
	@Test
	public void whenConsultaDocumentIMetadades_uuid_withDocuAndMetadades_thenReturn() {

		// Given	
		String identificador = "7c47a378-bc04-47de-86e6-16a17064deb1";
		Boolean esUuid = Boolean.TRUE;
		
		Document documentArxiu = initDocument(identificador);
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(true))).thenReturn(documentArxiu);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.estatElaboracioToValidesa(Mockito.any(DocumentEstatElaboracio.class))).thenReturn(ValidesaEnum.ORIGINAL.getValor());
				
		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);
			
		// Then
		assertNotNull(document);
		assertNotNull(document.getCsv());
		assertNotNull(document.getOrigen());
		assertNotNull(document.getValidesa());
		assertNotNull(document.getTipoDocumental());
		assertNotNull(document.getModoFirma());
		assertEquals("El identificador no coincide", identificador, document.getCsv());
		comprobarMetadadesCoinciden(documentArxiu, document);
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid); 
	}
	
	//Test consultaDocumentIMetadades para CSV docu existent y metadades existents
	@Test
	public void whenConsultaDocumentIMetadades_csv_withDocuAndMetadades_thenReturn() {
		
		// Given	
		String identificador = "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a";
		Boolean esUuid = Boolean.FALSE;
		
		Document documentArxiu = initDocument(identificador);
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(false))).thenReturn(documentArxiu);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.estatElaboracioToValidesa(Mockito.any(DocumentEstatElaboracio.class))).thenReturn(ValidesaEnum.ORIGINAL.getValor());
			
		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);		
		
		// Then
		assertNotNull(document);
		assertNotNull(document.getCsv());
		assertNotNull(document.getOrigen());
		assertNotNull(document.getValidesa());
		assertNotNull(document.getTipoDocumental());
		assertNotNull(document.getModoFirma());
		assertEquals("El identificador no coincide", identificador, document.getCsv());
		comprobarMetadadesCoinciden(documentArxiu, document);
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid);
		
		
	}
	
	//Test consultaDocumentIMetadades para Uuid docu existent y metadades inexistents
	@Test
	public void whenConsultaDocumentIMetadades_uuid_withDocuWithoutMetadades_thenReturn() {
		
		// Given	
		String identificador = "7c47a378-bc04-47de-86e6-16a17064deb1";
		Boolean esUuid = Boolean.TRUE;
		
		Document documentArxiu = initDocument(identificador);
		documentArxiu.setMetadades(null);
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(true))).thenReturn(documentArxiu);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.estatElaboracioToValidesa(Mockito.any(DocumentEstatElaboracio.class))).thenReturn(ValidesaEnum.ORIGINAL.getValor());
				
		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);
			
		// Then
		assertNotNull(document);
		assertNotNull(document.getCsv());
		assertNull(document.getOrigen());
		assertNull(document.getValidesa());
		assertNull(document.getTipoDocumental());
		assertNull(document.getModoFirma());
		assertEquals("El identificador no coincide", identificador, document.getCsv());
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid); 
		
	}
	//Test consultaDocumentIMetadades para CSV docu existent y metadades inexistents
	@Test
	public void whenConsultaDocumentIMetadades_csv_withDocuWithoutMetadades_thenReturn() {
		
		// Given	
		String identificador = "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a";
		Boolean esUuid = Boolean.FALSE;
		
		Document documentArxiu = initDocument(identificador);
		documentArxiu.setMetadades(null);
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(false))).thenReturn(documentArxiu);
		Mockito.when(pluginHelper.getModeFirma(Mockito.any(Document.class), Mockito.anyString())).thenReturn(1); //TRUE
		Mockito.when(pluginHelper.estatElaboracioToValidesa(Mockito.any(DocumentEstatElaboracio.class))).thenReturn(ValidesaEnum.ORIGINAL.getValor());
		
		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);		
		
		// Then
		assertNotNull(document);
		assertNotNull(document.getCsv());
		assertNull(document.getOrigen());
		assertNull(document.getValidesa());
		assertNull(document.getTipoDocumental());
		assertNull(document.getModoFirma());
		assertEquals("El identificador no coincide", identificador, document.getCsv());
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid);
		
	}
	//Test consultaDocumentIMetadades para Uuid docu inexistent
	@Test(expected = Exception.class)
	public void whenConsultaDocumentIMetadades_uuid_withoutDocument_thenException() {
		
		// Given	
		String identificador = "7c47a378-bc04-47de-86e6-16a17064deb1";
		Boolean esUuid = Boolean.TRUE;
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(true))).thenThrow(Exception.class);

		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);
		
		// Then
		assertNull(document);
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid);
		
	}
	//Test consultaDocumentIMetadades para CSV docu inexistent
	@Test(expected = Exception.class)
	public void whenConsultaDocumentIMetadades_csv_withoutDocument_thenException() {
		
		// Given	
		String identificador = "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a";
		Boolean esUuid = Boolean.FALSE;
		
		Mockito.when(pluginHelper.arxiuDocumentConsultar(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true), Mockito.eq(false))).thenThrow(Exception.class);

		// When	
		DocumentDto document = notificacioService.consultaDocumentIMetadades(identificador, esUuid);		
		
		// Then
		assertNull(document);
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(pluginHelper).arxiuDocumentConsultar(identificador, null, true, esUuid);
		
	}	
	private Document initDocument(String identificador) {
		Document documentArxiu = new Document();
		
		DocumentContingut contingut = new DocumentContingut();
		contingut.setArxiuNom("arxiu.pdf");
		contingut.setTipusMime("application/pdf");
		contingut.setContingut("/es/caib/notib/core/arxiu.pdf".getBytes());
//		try {
//			byte[] arxiuBytes = IOUtils.toByteArray(getClass().getResourceAsStream(
//					"/es/caib/notib/core/notificacio_adjunt.pdf"));		
//			contingut.setContingut(arxiuBytes);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
	
	private void comprobarMetadadesCoinciden(Document documentArxiu, DocumentDto document) {
		assertEquals("El origen no coincide", OrigenEnum.valorAsEnum(documentArxiu.getMetadades().getOrigen().ordinal()), document.getOrigen());
		assertEquals("Validesa no coincide", ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(documentArxiu.getMetadades().getEstatElaboracio())), document.getValidesa());
		assertEquals("El tipo documental no coincide", TipusDocumentalEnum.valorAsEnum(documentArxiu.getMetadades().getTipusDocumental().toString()), document.getTipoDocumental());
		assertEquals("El modo firma no coincide", pluginHelper.getModeFirma(documentArxiu, documentArxiu.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE, document.getModoFirma());
	}
	
	@Test
	public void whenValidarIdCsv_valid_thenReturnTrue() {
		String identificador = "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a";
		Boolean validaIdCsv = notificacioService.validarIdCsv(identificador);
		assertTrue(validaIdCsv);
	}
	
	@Test
	public void whenValidarIdCsv_noValid_thenReturnFalse() {
		String identificador = "54a27c163550ef2";
		Boolean validaIdCsv = notificacioService.validarIdCsv(identificador);
		assertFalse(validaIdCsv);
	}
	
	@After
	public void tearDown() {
		Mockito.reset(pluginHelper);
	}
}
