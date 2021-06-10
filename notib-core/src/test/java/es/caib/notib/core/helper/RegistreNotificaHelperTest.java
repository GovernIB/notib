package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.test.data.ConfigTest;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.plugins.arxiu.api.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashSet;

@RunWith(MockitoJUnitRunner.class)
public class RegistreNotificaHelperTest {
	
	@Mock
	private PluginHelper pluginHelper;
	@Mock
	private RegistreHelper registreHelper;
	@Mock
	private AuditNotificacioHelper auditNotificacioHelper;
	@Mock
	private IntegracioHelper integracioHelper;
	@Mock
	private NotificacioEventHelper notificacioEventHelper;

	@InjectMocks
	private RegistreNotificaHelper registreNotificaHelper;
	
	@Before
	public void setUp() throws RegistrePluginException {
		Mockito.when(
				pluginHelper.notificacioToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>any(), Mockito.anyBoolean())
		).thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(
				pluginHelper.notificacioEnviamentsToAsientoRegistralBean(Mockito.any(NotificacioEntity.class), Mockito.<NotificacioEnviamentEntity>anySet(), Mockito.anyBoolean())
		).thenReturn(new AsientoRegistralBeanDto());
		Mockito.when(
				pluginHelper.crearAsientoRegistral(
						Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()
				)
		).thenReturn(new RespostaConsultaRegistre());

	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenComunicacioSIRAAdministracio_ThenInclouDocuments() throws RegistreNotificaException, RegistrePluginException {
		// Given
		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
		NotificacioEntity notificacio = initNotificacio(entidad, NotificaEnviamentTipusEnumDto.COMUNICACIO, enviaments);

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		Mockito.verify(pluginHelper, Mockito.times(1)).notificacioToAsientoRegistralBean(
				Mockito.eq(notificacio),
				Mockito.eq(enviament),
				Mockito.eq(true)
		);
	}

	@Test
	public void whenRealitzarProcesRegistrar_GivenNotificacio_ThenNoInclouDocuments() throws RegistreNotificaException, RegistrePluginException {
		// Given
		EntitatEntity entidad = initEntitat();
		HashSet<NotificacioEnviamentEntity> enviaments = new HashSet<>();
		NotificacioEnviamentEntity enviament = initEnviament(
				initPersonaAdministracio(InteressatTipusEnumDto.ADMINISTRACIO)
		);
		enviaments.add(enviament);
		NotificacioEntity notificacio = initNotificacio(entidad, NotificaEnviamentTipusEnumDto.NOTIFICACIO, enviaments);

		// When
		registreNotificaHelper.realitzarProcesRegistrar(notificacio);

		// Then
		Mockito.verify(pluginHelper, Mockito.times(1)).notificacioEnviamentsToAsientoRegistralBean(
				Mockito.eq(notificacio),
				Mockito.<NotificacioEnviamentEntity>anySet(),
				Mockito.eq(false)
		);
	}

	private NotificacioEntity initNotificacio(EntitatEntity entitat,
											  NotificaEnviamentTipusEnumDto enviamentTipus,
											  HashSet<NotificacioEnviamentEntity> enviaments) {
		Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);

		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
				"",
				"",
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "10")),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "15")),
				entitat,
				null,
				null,
				false,
				null, // organGestor
				null,
				null,
				null,
				null,
				false).build();

		GrupDto grupDto = new GrupDto();
		grupDto.setId(1L);

		DocumentEntity document = new DocumentEntity();
//		document.setId(Long.toString(new Random().nextLong()));
//		document.setContingutBase64("/es/caib/notib/core/arxiu.pdf");
//		document.setNormalitzat(false);
//		document.setGenerarCsv(false);

		DocumentEntity document2 = new DocumentEntity();
//		document2.setId(Long.toString(new Random().nextLong()));
//		document2.setUuid(UUID.randomUUID().toString());
//		document2.setNormalitzat(false);
//		document2.setGenerarCsv(false);

		DocumentEntity document3 = new DocumentEntity();
//		document3.setId(Long.toString(new Random().nextLong()));
//		document3.setCsv("54a27c163550ef2d5f3a8cd985a4ab949b6dfb5e66174a11c2bc979e0070090a");
//		document3.setNormalitzat(false);
//		document3.setGenerarCsv(true);
		OrganGestorEntity organGestor = OrganGestorEntity.builder(null, null, entitat, null,
				null, null, null, null).build();
		NotificacioEntity notificacio =  Mockito.mock(NotificacioEntity.class);
		Mockito.when(notificacio.getEmisorDir3Codi()).thenReturn(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
		Mockito.when(notificacio.getId()).thenReturn(1L);
		Mockito.when(notificacio.getEnviamentTipus()).thenReturn(enviamentTipus);
		Mockito.when(notificacio.getEntitat()).thenReturn(entitat);
		Mockito.when(notificacio.getEnviaments()).thenReturn(enviaments);
//		NotificacioEntity.builder()
//				.entitat(entitat)
//				.enviamentDataProgramada(enviamentDataProgramada)
//				.concepte("Test")
//				.descripcio("Test descripció")
//				.organGestor(organGestor)
//				.enviamentDataProgramada(new Date())
//				.retard(5)
//				.caducitat(caducitat)
//				.enviaments(enviaments)
//				.usuariCodi("admin")
//				.numExpedient("EXPEDIENTEX")
//				.idioma(IdiomaEnumDto.CA)
//				.document(document)
//				.document2(document2)
//				.document3(document3)
//				.procediment(procediment)
//				.grupCodi("CODI_GRUP")
//				.build();

		for (NotificacioEnviamentEntity enviament: enviaments) {
			enviament.setNotificacio(notificacio);
		}

		return notificacio;
	}

	private NotificacioEnviamentEntity initEnviament(PersonaEntity titular) {
		NotificacioEnviamentEntity enviament = Mockito.mock(NotificacioEnviamentEntity.class);
		Mockito.when(enviament.getId()).thenReturn(1L);
		Mockito.when(enviament.getTitular()).thenReturn(titular);

		return enviament;
	}
	private EntitatEntity initEntitat() {
		 return EntitatEntity.getBuilder("codi",
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
		Mockito.reset(pluginHelper);
		Mockito.reset(registreHelper);
		Mockito.reset(auditNotificacioHelper);
		Mockito.reset(integracioHelper);
		Mockito.reset(notificacioEventHelper);
	}

}
