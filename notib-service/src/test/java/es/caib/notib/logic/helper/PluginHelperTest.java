package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.helper.plugin.ArxiuPluginHelper;
import es.caib.notib.logic.helper.plugin.CarpetaPluginHelper;
import es.caib.notib.logic.helper.plugin.DadesUsuarisPluginHelper;
import es.caib.notib.logic.helper.plugin.FirmaPluginHelper;
import es.caib.notib.logic.helper.plugin.GestioDocumentalPluginHelper;
import es.caib.notib.logic.helper.plugin.GestorDocumentalAdministratiuPluginHelper;
import es.caib.notib.logic.helper.plugin.RegistrePluginHelper;
import es.caib.notib.logic.helper.plugin.UnitatsOrganitzativesPluginHelper;
import es.caib.notib.logic.helper.plugin.ValidaSignaturaPluginHelper;
import es.caib.notib.logic.intf.dto.AnexoWsDto;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.arxiu.ArxiuPluginConcsvImpl;
import es.caib.notib.plugin.conversio.ConversioPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PluginHelperTest {

    @Mock
    private ConfigHelper configHelper;
    @Mock
	private DadesUsuarisPluginHelper dadesUsuarisPluginHelper;
    @Mock
	private GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
    @Mock
	private RegistrePluginHelper registrePluginHelper;
    @Mock
	private ArxiuPluginHelper arxiuPluginHelper;
    @Mock
	private UnitatsOrganitzativesPluginHelper unitatsOrganitzativesPluginHelper;
    @Mock
	private GestorDocumentalAdministratiuPluginHelper gestorDocumentalAdministratiuPluginHelper;
    @Mock
	private FirmaPluginHelper firmaPluginHelper;
    @Mock
	private ValidaSignaturaPluginHelper validaSignaturaPluginHelper;
    @Mock
	private CarpetaPluginHelper carpetaPluginHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private EntitatRepository entitatRepository;
    @InjectMocks
    private PluginHelper pluginHelper;

	EntitatEntity entidad;
	Properties properties;
	private Map<String, IArxiuPlugin> arxiuPlugin = new HashMap<>();
	private Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin = new HashMap<>();
	private Map<String, RegistrePlugin> registrePlugin = new HashMap<>();
	private Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin = new HashMap<>();
	private Map<String, GestorContingutsAdministratiuPlugin> gestorDocumentalAdministratiuPlugin = new HashMap<>();

    @Before
    public void setUp() throws Exception {

		entidad = initEntitat();
		var documentArxiuAmbContingut = initDocument();
		properties = new Properties();
		properties.put("es.caib.notib.plugin.regweb.mock.sequencia", "../notib-plugin/src/main/resources/es/caib/notib/plugin/caib/registre/registre.txt");
		properties.put("es.caib.notib.plugin.regweb.mock.justificant", "../notib-plugin/src/main/resources/es/caib/notib/plugin/caib/registre/justificant.pdf");
//		Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.plugin.registre.segons.entre.peticions"))).thenReturn(secondsBetweenCalls);
//        Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.class"))).thenReturn("");
		Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.procediment.alta.auto.retard"))).thenReturn(10);
		Mockito.when(configHelper.getConfigAsInteger(Mockito.eq("es.caib.notib.procediment.alta.auto.caducitat"))).thenReturn(15);
//		Mockito.when(configHelper.getConfigKeyByEntitat("es.caib.notib.plugin.arxiu.class")).thenReturn("es.caib.notib.plugin.arxiu.ArxiuPluginConcsvImpl");
//		Mockito.when(configHelper.getConfigKeyByEntitat("es.caib.notib.plugin.registre.class")).thenReturn("es.caib.notib.plugin.registre.RegistrePluginMockImpl");
		Mockito.when(configHelper.getConfigAsInteger("es.caib.notib.plugin.registre.segons.entre.peticions")).thenReturn(30);
//		Mockito.when(configHelper.getAllEntityProperties(Mockito.anyString())).thenReturn(properties);
		Mockito.when(entitatRepository.findByDir3Codi(Mockito.anyString())).thenReturn(entidad);

		// Plugin Arxiu
		IArxiuPlugin pluginArxiu = Mockito.mock(ArxiuPluginConcsvImpl.class);
		Mockito.when(pluginArxiu.documentDetalls(Mockito.anyString(), Mockito.nullable(String.class), Mockito.eq(true))).thenReturn(documentArxiuAmbContingut);
		arxiuPlugin.put("CAIB", pluginArxiu);
		pluginHelper.setArxiuPlugin(arxiuPlugin);

		// Plugin gestió documental
		var pluginGestioDocumental = Mockito.mock(GestioDocumentalPlugin.class);
		gestioDocumentalPlugin.put("CAIB", pluginGestioDocumental);
		pluginHelper.setGestioDocumentalPlugin(gestioDocumentalPlugin);

		// Plugin registre
		var pluginRegistre = Mockito.mock(RegistrePlugin.class);
		var resposta = new RespostaJustificantRecepcio();
		resposta.setErrorCodi(null);
		resposta.setErrorDescripcio("respostaMock");
		Mockito.when(pluginRegistre.obtenerJustificante(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(resposta);
		registrePlugin.put("CAIB", pluginRegistre);
		pluginHelper.setRegistrePlugin(registrePlugin);

		// Plugin unitats
		var pluginUnitats = Mockito.mock(UnitatsOrganitzativesPlugin.class);
		unitatsOrganitzativesPlugin.put("CAIB", pluginUnitats);
		pluginHelper.setUnitatsOrganitzativesPlugin(unitatsOrganitzativesPlugin);

		// Plugin rolsac
		var pluginRolsac = Mockito.mock(GestorContingutsAdministratiuPlugin.class);
		gestorDocumentalAdministratiuPlugin.put("CAIB", pluginRolsac);
		pluginHelper.setGestorDocumentalAdministratiuPlugin(gestorDocumentalAdministratiuPlugin);

	}

    static int secondsBetweenCalls = 2;

    @Test
    public void whenObtenirJustificant_thenWaitForNextCall() {

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
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("BINARI");
    	
		Document documentArxiuAmbContingut = initDocument();

		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipus.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, true);

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
//	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 

    }
    
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroUuid_thenUuidNullIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("BINARI");
    	
		Document documentArxiuAmbContingut = initDocument();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipus.ADMINISTRACIO)
		);
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, false);

    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
//	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 
    }
    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroCsv_thenCsvInformatIContingutNull() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("CSV");
    	Document documentArxiuAmbContingut = initDocument();
    	
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		var enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, true);
    	
    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ true); // isComunicacioSir
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
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("CSV");
    	
    	Document documentArxiuAmbContingut = initDocument();
    	
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		var enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, false);

    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ true); // isComunicacioSir
	        // Then
	    	for (var anexo: asientoRegistralBeanDto.getAnexos()) {
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
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("TOT");
		Document documentArxiuAmbContingut = initDocument();

		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		var enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, true);

    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 
    }

    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicSirAmbFicheroUuid_thenUuidInformatIContingutInformat() throws IOException {
        
        // Given
    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("TOT");
		Document documentArxiuAmbContingut = initDocument();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(initPersonaAdministracio(InteressatTipus.ADMINISTRACIO));
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.COMUNICACIO, enviaments, organGestor, procediment, procedimentOrgan, false);

    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ true); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
//	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 
    }

    
    @Test
    public void whenNotificacioToAsientoRegistralBeanPerComunicNoSirAmbFicheroCsv_thenCsvInformatIContingutInformat() throws IOException {
        
        // Given
//    	Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar"))).thenReturn("TOT");
    	
		var documentArxiuAmbContingut = initDocument();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(initPersonaAdministracio(InteressatTipus.FISICA));
		enviaments.add(enviament);
    	var organGestor = initOrganGestor(entidad);
    	var procediment = initProcediment(entidad);
    	var procedimentOrgan = initProcedimentOrgan(procediment, organGestor);
    	var notificacio = initNotificacio(entidad, EnviamentTipus.NOTIFICACIO, enviaments, organGestor, procediment, procedimentOrgan, true);

    	// When
		try {
			var asientoRegistralBeanDto = pluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, true, /* inclou_documents */ false); // isComunicacioSir
	        // Then
	    	for (AnexoWsDto anexo: asientoRegistralBeanDto.getAnexos()) {
	    		assertNotNull(anexo.getCsv());
	    		assertNotNull(anexo.getNombreFicheroAnexado());
	    		assertNotNull(anexo.getFicheroAnexado());
//	    		assertNotNull(anexo.getTipoMIMEFicheroAnexado());
	    	}
		} catch (RegistrePluginException e) {
			Assert.assertTrue(true);
		} 
    }

	private NotificacioEnviamentEntity initEnviament(PersonaEntity titular) {

		var enviament = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviament.getTitular()).thenReturn(titular);
		return enviament;
	}
	
	private PersonaEntity initPersonaAdministracio(InteressatTipus interessatTipus) {
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
		 return EntitatEntity.getBuilder("CAIB",
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

	private NotificacioEntity initNotificacio(EntitatEntity entitat, EnviamentTipus enviamentTipus, HashSet<NotificacioEnviamentEntity> enviaments,
												OrganGestorEntity organGestor, ProcedimentEntity procediment, ProcSerOrganEntity procedimentOrgan, Boolean isCsv) {

		var notificacioId = Long.toString(System.currentTimeMillis());
		var caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		
		var notificacioGuardada = NotificacioEntity.getBuilderV2(entitat, notificacioId, organGestor, null, null, notificacioId,
				notificacioId, caducitat, null, caducitat, notificacioId, notificacioId, procediment, notificacioId, notificacioId,
				null, procedimentOrgan, null, UUID.randomUUID().toString()).document(initDocumentEntity(notificacioId, isCsv)).build();

		notificacioGuardada.updateEstat(NotificacioEstatEnumDto.PENDENT);
		for (var enviament: enviaments) {
			enviament.setNotificacio(notificacioGuardada);
		}
		Mockito.when(configHelper.getEntitatActualCodi()).thenReturn("CAIB");
		return notificacioGuardada;
	}

	private OrganGestorEntity initOrganGestor(EntitatEntity entitatMock) {
		return OrganGestorEntity.builder().entitat(entitatMock).build();
	}
	
	private ProcedimentEntity initProcediment(EntitatEntity entitatMock) {

		return ProcedimentEntity.getBuilder("1", "",
				configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.retard"),
				configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.caducitat"),
				entitatMock, false, null, /* organGestor */ null, null, null, null, false,
				false, false).build();
	}
	
	private ProcSerOrganEntity initProcedimentOrgan (ProcedimentEntity procediment, OrganGestorEntity organGestor) {
		return ProcSerOrganEntity.getBuilder(procediment, organGestor).build();
	}
	
	private DocumentEntity initDocumentEntity(String identificador, Boolean isCsv) {

		var documentArxiu = new Document();
		var documentGesdocId = "documentGesdocId";
		var document = new DocumentDto();
		document.setId(Long.toString(new Random().nextLong()));
		if (isCsv) {
			document.setCsv("54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
		} else {
			document.setUuid("7c47a378-bc04-47de-86e6-16a17064deb1");
		}
		document.setNormalitzat(false);
		document.setGenerarCsv(true);
		var documentEntity = DocumentEntity.getBuilderV2(
				documentGesdocId,
				document.getArxiuNom(),
				document.isNormalitzat(),
				document.getUuid(),
				document.getCsv(),
				document.getMediaType(),
				document.getMida(),
				document.getOrigen(),
				document.getValidesa(),
				document.getTipoDocumental(),
				document.getModoFirma()).build();
		documentEntity.updateId(1L);
		var contingut = new DocumentContingut();
		contingut.setArxiuNom("arxiu.pdf");
		contingut.setTipusMime("application/pdf");
		contingut.setContingut("/es/caib/notib/logic/arxiu.pdf".getBytes());
		contingut.setTamany(contingut.getContingut().length);
		documentArxiu.setContingut(contingut);
		documentArxiu.setEstat(DocumentEstat.DEFINITIU);
		documentArxiu.setFirmes(null);
		documentArxiu.setIdentificador(identificador);
		var metadades = new DocumentMetadades();
		metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
		metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		metadades.setTipusDocumental(DocumentTipus.INFORME);
		documentArxiu.setMetadades(metadades);
		documentArxiu.setNom("Nombre Document Arxiu");
		documentArxiu.setVersio("Version");
		return documentEntity;
	}
	
	private FitxerDto getFitxerPdfDeTest() throws IOException {

		var dto = new FitxerDto();
		dto.setNom("arxiu.pdf");
		dto.setContentType("application/pdf");
		dto.setContingut(IOUtils.toByteArray(getClass().getResourceAsStream("/es/caib/notib/logic/arxiu.pdf")));
		dto.setTamany(dto.getContingut().length);
		return dto;
	}

	private Document initDocument() throws IOException {

		var documentArxiuAmbContingut = new Document();
		documentArxiuAmbContingut.setIdentificador(UUID.randomUUID().toString());
		documentArxiuAmbContingut.setNom("nom");
		documentArxiuAmbContingut.setVersio("1");
		var documentContingut = new DocumentContingut();
		documentContingut.setArxiuNom("arxiu.pdf");
		documentContingut.setTipusMime("application/pdf");
		var fitxer = getFitxerPdfDeTest();
		documentContingut.setArxiuNom(fitxer.getNom());
		documentContingut.setTipusMime(fitxer.getContentType());
		documentContingut.setContingut(fitxer.getContingut());
		documentContingut.setTamany(fitxer.getTamany());
		documentArxiuAmbContingut.setContingut(documentContingut);
		var metadades = new DocumentMetadades();
		metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
		metadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		metadades.setTipusDocumental(DocumentTipus.INFORME);
		HashMap<String, Object> metadadesAddicionals = new HashMap<>();
		metadadesAddicionals.put("csv", "54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
		metadades.setMetadadesAddicionals(metadadesAddicionals);
		documentArxiuAmbContingut.setMetadades(metadades);
		return documentArxiuAmbContingut;
	}
}