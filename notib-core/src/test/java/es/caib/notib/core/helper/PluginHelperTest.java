package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.*;
import es.caib.notib.plugin.conversio.ConversioPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.*;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PluginHelperTest {
    @Mock
    private DadesUsuariPlugin dadesUsuariPlugin;
    @Mock
    private GestioDocumentalPlugin gestioDocumentalPlugin;
    @Mock
    private RegistrePlugin registrePlugin;
    @Mock
    private IArxiuPlugin arxiuPlugin;
    @Mock
    private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
    @Mock
    private GestorContingutsAdministratiuPlugin gestorDocumentalAdministratiuPlugin;
    @Mock
    private ConversioPlugin conversioPlugin;
    @Mock
    private FirmaServidorPlugin firmaServidorPlugin;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private PluginHelper pluginHelper;

    @Before
    public void setUp() throws Exception {
        Mockito.when(configHelper.getAsInt(Mockito.eq("es.caib.notib.plugin.registre.segons.entre.peticions"))).thenReturn(secondsBetweenCalls);
//        Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.class"))).thenReturn("");
		Mockito.when(configHelper.getAsInt(Mockito.eq("es.caib.notib.procediment.alta.auto.retard"))).thenReturn(10);
		Mockito.when(configHelper.getAsInt(Mockito.eq("es.caib.notib.procediment.alta.auto.caducitat"))).thenReturn(15);
    }

    static int secondsBetweenCalls = 2;

    @Test
    public void whenObtenirJustificant_thenWaitForNextCall() {

        RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
        resposta.setErrorCodi(null);
        resposta.setErrorDescripcio("respostaMock");
        Mockito.when(registrePlugin.obtenerJustificante(Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn(resposta);
        // Given
        String codiDir3Entitat = "A00000000";
        String numeroRegistreFormatat = "9874";

        // When
        pluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);

        // Then

        long iniTime = System.nanoTime();
        long lastTime;
        double timeSpend;
        RespostaJustificantRecepcio resposta2;
        do {
            resposta2 = pluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);
            lastTime = System.nanoTime();
            timeSpend = (lastTime - iniTime) / 1e9;
        }while (resposta2.getErrorDescripcio() == null && timeSpend < secondsBetweenCalls + 2);

        Assert.assertTrue(timeSpend >= secondsBetweenCalls - 2);
    }
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroCsv_thenCsvNullIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("BINARI");
    	
		Document documentArxiuAmbContingut = initDocument();

		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			true);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }
    
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroUuid_thenUuidNullIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("BINARI");
    	
		Document documentArxiuAmbContingut = initDocument();

		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			false);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroCsv_thenCsvInformatIContingutNull() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("CSV");
    	
    	Document documentArxiuAmbContingut = initDocument();
    	
		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			true);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNull(anexo.getNombreFicheroAnexado());
	    		assertNull(anexo.getFicheroAnexado());
	    		assertNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }
    
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroUuid_thenUuidInformatIContingutNull() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("CSV");
    	
    	Document documentArxiuAmbContingut = initDocument();
    	
    	EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			false);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
   	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNull(anexo.getNombreFicheroAnexado());
	    		assertNull(anexo.getFicheroAnexado());
	    		assertNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }
    
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroCsv_thenCsvInformatIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("TOT");
    	
		Document documentArxiuAmbContingut = initDocument();

		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			true);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }

    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroUuid_thenUuidInformatIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
        .thenReturn("TOT");
    	
		Document documentArxiuAmbContingut = initDocument();

		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.COMUNICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			false);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }

    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicNoSirAmbFicheroCsv_thenCsvInformatIContingutInformat() throws IOException {
        
        // Given
//    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar")))
//        .thenReturn("TOT");
    	
		Document documentArxiuAmbContingut = initDocument();

		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.FISICA)
		);
		enviaments.add(enviament);
    	OrganGestorEntity organGestor = initOrganGestor(entidad);
    	ProcedimentEntity procediment = initProcediment(entidad);
    	ProcSerOrganEntity procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	NotificacioEntity notificacio = initNotificacio(entidad, 
    			NotificaEnviamentTipusEnumDto.NOTIFICACIO, 
    			enviaments, 
    			organGestor, 
    			procediment,
    			procedimentOrgan,
    			true);
    	Mockito.when(arxiuPlugin.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
    	
    	// When
    	AsientoRegistralBeanDto asientoRegistralBeanDto;
		try {
			asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio,
					enviament,
					true, // inclou_documents
					false); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }

	private NotificacioEnviamentEntity initEnviament(PersonaEntity titular) {
		NotificacioEnviamentEntity enviament = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviament.getTitular()).thenReturn(titular);

		return enviament;
	}
	
	private PersonaEntity initPersonaAdministracio(InteressatTipusEnumDto interessatTipus) {
		return PersonaEntity.builder()
				.interessatTipus(interessatTipus)
				.email("sandreu@limit.es")
				.llinatge1("Andreu")
				.llinatge2("Nadal")
				.nif("00000000T")
				.nom("Siòn")
				.telefon("666010101").build();
	}

	private EntitatEntity initEntitat() {
		 return EntitatEntity.getBuilder("codi",
				"nom",
				null,
				"dir3Codi",
				"dir3CodiReg",
				"apiKey",
				false,
				null,
				null,
				"colorFons",
				"colorLletra",
				null,
				"oficina",
				"nomOficinaVirtual",
				true,                // llibreEntitat
				"llibre",
				"llibreNom",
				true)                // oficinaEntitat
				.build();
	}

	private NotificacioEntity initNotificacio(EntitatEntity entitat,
			NotificaEnviamentTipusEnumDto enviamentTipus,
			HashSet<NotificacioEnviamentEntity> enviaments,
			OrganGestorEntity organGestor, 
			ProcedimentEntity procediment,
			ProcSerOrganEntity procedimentOrgan,
			Boolean isCsv) {

		String notificacioId = Long.toString(System.currentTimeMillis());
		Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		
		NotificacioEntity notificacioGuardada = NotificacioEntity.getBuilderV2(entitat, 
				notificacioId, organGestor, null, null, notificacioId, notificacioId, caducitat, 
				null, caducitat, notificacioId, notificacioId, procediment, notificacioId, notificacioId, 
				null, procedimentOrgan, null).document(initDocumentEntity(notificacioId, isCsv)).build();

		notificacioGuardada.updateEstat(NotificacioEstatEnumDto.PENDENT);

		for (NotificacioEnviamentEntity enviament: enviaments) {
			enviament.setNotificacio(notificacioGuardada);
		}

		return notificacioGuardada;
	}

	private OrganGestorEntity initOrganGestor(EntitatEntity entitatMock) {
		OrganGestorEntity organGestor = OrganGestorEntity.builder(null, null, null,
				entitatMock, null, null, null, null, null).build();
		return organGestor;

	}
	
	private ProcedimentEntity initProcediment(EntitatEntity entitatMock) {
		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
				"1",
				"",
				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.retard"),
				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.caducitat"),
				entitatMock,
				false,
				null, // organGestor
				null,
				null,
				null,
				null,
				false,
				false).build();
		return procediment;
	}
	
	private ProcSerOrganEntity initProcedimentOrgan (ProcedimentEntity procediment,
                                                     OrganGestorEntity organGestor) {
		ProcSerOrganEntity procedimentOrgan = ProcSerOrganEntity.getBuilder(procediment,
				organGestor).build();
		return procedimentOrgan;
	}
	
	private DocumentEntity initDocumentEntity(String identificador, Boolean isCsv) {
		Document documentArxiu = new Document();
		String documentGesdocId = "documentGesdocId";
		
		DocumentDto document = new DocumentDto();
		document.setId(Long.toString(new Random().nextLong()));
		if (isCsv) {
			document.setCsv("54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
		} else {
			document.setUuid("7c47a378-bc04-47de-86e6-16a17064deb1");
		}
		document.setNormalitzat(false);
		document.setGenerarCsv(true);
		
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

		return documentEntity;
	}
	
	private FitxerDto getFitxerPdfDeTest() throws IOException {
		FitxerDto dto = new FitxerDto();
		dto.setNom("arxiu.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(
				IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/core/arxiu.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}
	

	private Document initDocument() throws IOException {
		Document documentArxiuAmbContingut = new Document();
		documentArxiuAmbContingut.setIdentificador(UUID.randomUUID().toString());
		documentArxiuAmbContingut.setNom("nom");
		documentArxiuAmbContingut.setVersio("1");
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom("arxiu.pdf");
		documentContingut.setTipusMime("application/pdf");
		FitxerDto fitxer = getFitxerPdfDeTest();
		documentContingut.setArxiuNom(fitxer.getNom());
		documentContingut.setTipusMime(fitxer.getContentType());
		documentContingut.setContingut(fitxer.getContingut());
		documentContingut.setTamany(fitxer.getTamany());
		documentArxiuAmbContingut.setContingut(documentContingut);
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
		metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		metadades.setTipusDocumental(DocumentTipus.INFORME);
		HashMap<String, Object> metadadesAddicionals = new HashMap<String, Object>();
		metadadesAddicionals.put("csv", "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
		metadades.setMetadadesAddicionals(metadadesAddicionals);
		documentArxiuAmbContingut.setMetadades(metadades);
		return documentArxiuAmbContingut;
	}
}